// 지원서 제출 몰림(write burst) 부하 테스트
//
// ApplicationServiceImpl.create() 는 단일 @Transactional 안에서 NCP 업로드와 DB 쓰기를
// 함께 수행한다. 커넥션 점유 시간이 업로드 시간에 묶이므로 처리량 상한이
//   Hikari pool / 트랜잭션 시간
// 으로 고정된다. 이 스크립트는 그 상한을 찾는다.
//
// 실행
//   k6 run -e BASE_URL=https://stg.recruit-withus.co.kr -e SLUG=fdHhU7Mle \
//          -e PROFILE=smoke k6/application-submit-burst.js
//
// PROFILE
//   smoke : VU 2, 30s     — 페이로드가 공고 설정과 맞는지 확인
//   ramp  : VU 0→150      — 처리량이 꺾이는 지점과 첫 5xx 시점
//   soak  : VU 고정, 5m    — 지속 부하 안정성
//
// FILE_KB
//   0 이면 파일형 질문을 페이로드에서 아예 빼고 보낸다(업로드 없음).
//   0 보다 크면 그 크기의 더미 파일을 첨부한다.
//   두 값을 비교하면 NCP 업로드가 트랜잭션에서 차지하는 비중이 드러난다.
//
// 주의
//   - 실제 지원서가 생성되고 NCP 에 파일이 쌓인다. 라운드마다 정리해야
//     테이블 크기가 달라지지 않아 라운드 간 비교가 유효하다.
//   - 메일은 mail.provider=noop 으로 막아둔 상태에서 돌릴 것.

import http from 'k6/http';
import { check, sleep, fail } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL;
const SLUG = __ENV.SLUG;
const PROFILE = __ENV.PROFILE || 'smoke';
const FILE_KB = Number(__ENV.FILE_KB || '500');
const SLEEP_SECONDS = Number(__ENV.SLEEP_SECONDS || '0');

if (!BASE_URL) fail('BASE_URL is required. e.g. -e BASE_URL=https://stg.recruit-withus.co.kr');
if (!SLUG) fail('SLUG is required. e.g. -e SLUG=fdHhU7Mle');

const PROFILES = {
    smoke: { vus: Number(__ENV.VUS || '2'), duration: __ENV.DURATION || '30s' },
    ramp: {
        stages: [
            { duration: '30s', target: 10 },
            { duration: '1m', target: 30 },
            { duration: '1m', target: 60 },
            { duration: '1m', target: 100 },
            { duration: '1m', target: 150 },
            { duration: '30s', target: 0 },
        ],
    },
    soak: { vus: Number(__ENV.VUS || '20'), duration: __ENV.DURATION || '5m' },
};

if (!PROFILES[PROFILE]) fail(`Unknown PROFILE: ${PROFILE}. use smoke|ramp|soak`);

export const options = {
    ...PROFILES[PROFILE],
    // 한계를 찾는 게 목적이므로 실패해도 중단하지 않는다.
    thresholds: {
        submit_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<10000'],
    },
};

const submitFailed = new Rate('submit_failed');
const submitDuration = new Trend('submit_duration', true);
const submitOk = new Counter('submit_ok');
const submit5xx = new Counter('submit_5xx');
const submit4xx = new Counter('submit_4xx');
const submitPoolExhausted = new Counter('submit_pool_exhausted');

// VU 당 한 번만 만든다. 매 반복 생성하면 클라이언트 CPU 가 병목이 된다.
const FILLER = FILE_KB > 0
    ? 'k6-loadtest-filler-'.repeat(Math.ceil((FILE_KB * 1024) / 19)).slice(0, FILE_KB * 1024)
    : '';

export function setup() {
    const res = http.get(`${BASE_URL}/api/v1/recruitments/slug/${SLUG}`);
    if (res.status !== 200) {
        fail(`Failed to load recruitment. status=${res.status} body=${String(res.body).slice(0, 300)}`);
    }

    const d = res.json().result;
    if (!d) fail('Recruitment detail is empty.');

    const questions = (d.applicationQuestions || []).map((q) => ({
        questionId: q.questionId,
        type: q.type,
    }));

    // "2026.12.24" + "00:30" -> "2026-12-24T00:30:00"
    const availableTimes = (d.availableTimeRanges || []).map((r) => {
        const date = String(r.date).replace(/\./g, '-');
        const time = String(r.startTime).length === 5 ? `${r.startTime}:00` : r.startTime;
        return `${date}T${time}`;
    });

    const positions = (d.positions || []).map((p) => p.id ?? p.organizationRoleId);

    const setupData = {
        recruitmentId: d.recruitmentId,
        positionId: positions.length > 0 ? positions[0] : null,
        questions,
        availableTimes,
        needImage: d.needImage,
        needGender: d.needGender,
        needAddress: d.needAddress,
        needSchool: d.needSchool,
        needBirthDate: d.needBirthDate,
        needMajor: d.needMajor,
        needAcademicStatus: d.needAcademicStatus,
    };

    console.log(
        `[setup] recruitmentId=${setupData.recruitmentId} ` +
        `questions=${questions.length}(file=${questions.filter((q) => q.type === 'FILE').length}) ` +
        `availableTimes=${availableTimes.length} needImage=${d.needImage} ` +
        `deadline=${d.documentDeadline} FILE_KB=${FILE_KB} PROFILE=${PROFILE}`
    );

    return setupData;
}

export default function (data) {
    const suffix = `${__VU}-${__ITER}-${Date.now()}`;
    const attachFile = FILE_KB > 0;

    // ApplicationValidator.validateFileAnswers 는 answers 중 FILE 질문 수와
    // 실제 파일 개수가 정확히 일치해야 통과한다. FILE_KB=0 이면 FILE 질문을
    // answers 에서 제외해 파일 없이 보낸다.
    const answers = [];
    let fileName = null;

    for (const q of data.questions) {
        if (q.type === 'FILE') {
            if (!attachFile) continue;
            fileName = `loadtest-${suffix}.pdf`;
            answers.push({ questionId: q.questionId, answerText: null, fileName });
        } else {
            answers.push({
                questionId: q.questionId,
                answerText: `[k6] VU=${__VU} ITER=${__ITER} 자동 생성 답변입니다.`,
                fileName: null,
            });
        }
    }

    const request = {
        name: `부하테스트${__VU}-${__ITER}`,
        email: `loadtest+${suffix}@example.com`,
        phoneNumber: `010${String(Math.floor(Math.random() * 100000000)).padStart(8, '0')}`,
        recruitmentId: data.recruitmentId,
        positionId: data.positionId,
        answers,
        availableTimes: data.availableTimes,
        gender: data.needGender ? 'MALE' : null,
        university: data.needSchool ? '상명대학교' : null,
        major: data.needMajor ? '컴퓨터과학과' : null,
        academicStatus: data.needAcademicStatus ? 'ENROLLED' : null,
        birthDate: data.needBirthDate ? '2000-01-01' : null,
        address: data.needAddress ? '서울시 도봉구 56로 501' : null,
    };

    const payload = {
        request: http.file(JSON.stringify(request), 'request.json', 'application/json'),
    };

    if (data.needImage) {
        payload.profileImage = http.file(FILLER || 'x', `loadtest-${suffix}.jpg`, 'image/jpeg');
    }

    if (attachFile) {
        payload.files = http.file(FILLER, fileName, 'application/pdf');
    }

    const res = http.post(`${BASE_URL}/api/v1/applications`, payload, {
        tags: { name: 'POST /api/v1/applications' },
        timeout: '60s',
    });

    submitDuration.add(res.timings.duration);

    const ok = check(res, { 'submit 200': (r) => r.status === 200 });
    submitFailed.add(!ok);

    if (ok) {
        submitOk.add(1);
        return;
    }

    const body = String(res.body || '');

    if (res.status >= 500 || res.status === 0) {
        submit5xx.add(1);
        // 커넥션 풀 고갈을 따로 센다. 이게 지배적이면 트랜잭션 길이가 병목이다.
        if (/SQLTransientConnection|Connection is not available|HikariPool/i.test(body)) {
            submitPoolExhausted.add(1);
        }
        if (__ITER % 50 === 0) {
            console.error(`5xx status=${res.status} body=${body.slice(0, 200)}`);
        }
    } else {
        submit4xx.add(1);
        // 400 이면 페이로드가 공고 설정과 안 맞는 것이므로 즉시 드러나야 한다.
        if (__ITER === 0) {
            console.error(`${res.status} status=${res.status} body=${body.slice(0, 500)}`);
        }
    }

    if (SLEEP_SECONDS > 0) sleep(SLEEP_SECONDS);
}
