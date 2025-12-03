import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        high_rps_test: {
            executor: 'constant-arrival-rate',
            rate: 10,
            timeUnit: '1s',
            duration: '1m',
            preAllocatedVUs: 20,
            maxVUs: 100,
        },
    },
};

const BASE_URL = 'https://jk-project.site';
const LOGIN_ENDPOINT = `${BASE_URL}/api/v1/auth/login`;
const RECRUITMENT_PUBLISH_ENDPOINT = `${BASE_URL}/api/v1/recruitments/publish`;
const APPLY_CREATE_ENDPOINT = `${BASE_URL}/api/v1/applications`;

function randomString(len = 6) {
    const chars = 'abcdefghijklmnopqrstuvwxyz';
    return Array.from({ length: len }, () => chars[Math.floor(Math.random() * chars.length)]).join('');
}

export default function () {
    /** 로그인 **/
    const loginPayload = JSON.stringify({
        email: 'shash042319@naver.com',
        password: 'Abc123!@',
    });

    const headers = {
        'Content-Type': 'application/json',
    };

    const loginRes = http.post(LOGIN_ENDPOINT, loginPayload, { headers });

    check(loginRes, {
        'status is 200': (r) => r.status === 200,
        'Authorization token is present': (r) =>
            r.headers['Authorization'] && r.headers['Authorization'].startsWith('Bearer '),
    });

    const token = loginRes.headers['Authorization'];

    if (!token) {
        console.error('Authorization token not found!');
        return;
    }

    /** 공고 생성 **/
    const recruitmentPayload = JSON.stringify({
        recruitmentId: null,
        title: '2025-1 큐시즘 모집',
        content: '큐시즘 학회원 모집합니다.',
        positions: ['백엔드', '디자인'],
        applicationQuestions: [
            {
                title: '자기소개를 해주세요',
                description: '자유롭게 자신을 표현해 주세요.',
                type: 'TEXT',
                required: true,
                textLimit: 1000,
                includeWhitespace: true,
                maxFileCount: 1,
                maxFileSizeMb: 10,
                positionName: '백엔드'
            }
        ],
        documentDeadline: '2026-06-01',
        isDocumentResultRequired: true,
        documentResultDate: '2026-06-10',
        finalResultDate: '2026-06-20',
        interviewDuration: 30,
        organizationId: 1,
        needImage: false,
        needGender: false,
        needAddress: false,
        needSchool: false,
        needBirthDate: false,
        needMajor: false,
        needAcademicStatus: false,
        documentScaleType: 'SCORE',
        interviewScaleType: 'SCORE',
        documentEvaluationCriteria: [
            {
                content: '성실성',
                description: '열심히 참여할 것 같은지',
                type: 'INTERVIEW',
                positionName: '백엔드'
            }
        ],
        interviewEvaluationCriteria: [
            {
                content: '성실성',
                description: '열심히 참여할 것 같은지',
                type: 'INTERVIEW',
                positionName: '백엔드'
            }
        ],
        isInterviewRequired: true,
        availableTimeRanges: [
            {
                date: '2026-07-30',
                startTime: '12:00',
                endTime: '12:00'
            }
        ]
    });

    const publishRes = http.post(RECRUITMENT_PUBLISH_ENDPOINT, recruitmentPayload, {
        headers: {
            'Content-Type': 'application/json',
            Authorization: token,
        },
    });

    check(publishRes, {
        'publish status is 201 or 200': (r) => r.status === 201 || r.status === 200,
    });

    if (publishRes.status >= 400) {
        console.error(`❌ 공고 생성 실패: status=${publishRes.status}, body=${publishRes.body}`);
    }

    const recruitmentId = publishRes.json()?.result?.recruitmentId;
    if (!recruitmentId) {
        console.error('❌ recruitmentId 추출 실패:', publishRes.body);
        return;
    }

    const detailRes = http.get(`${BASE_URL}/api/v1/recruitments/${recruitmentId}`, {
        headers: { Authorization: token }
    });

    const detail = detailRes.json()?.result;
    const positionId = detail?.positions?.[0]?.id;
    const questionId = detail?.applicationQuestions?.[0]?.questionId;

    if (!positionId || !questionId) {
        console.error('❌ 상세 조회에서 ID 추출 실패:', detailRes.body);
        return;
    }


    /** 지원서 생성 추가 **/
    const applicantName = `김${randomString(3)}`;
    const applicantEmail = `test${randomString(4)}@example.com`;

    const applicationRequest = {
        name: applicantName,
        email: applicantEmail,
        phoneNumber: '01012341234',
        gender: 'MALE',
        university: '상명대학교',
        major: '컴퓨터공학과',
        academicStatus: 'ENROLLED',
        birthDate: '2000-01-01',
        address: '서울시 도봉구 56로 501',
        recruitmentId,
        positionId,
        answers: [
            {
                questionId,
                answer: '열정과 책임감을 가지고 지원합니다.',
            }
        ],
        availableTimes: [
            '2025-06-01T10:00:00',
            '2025-06-01T14:00:00'
        ]
    };

    const formData = {
        request: http.file(JSON.stringify(applicationRequest), 'request.json', 'application/json'),
    };

    const applyRes = http.post(APPLY_CREATE_ENDPOINT, formData, {
        headers: {
            Authorization: token
        },
    });

    check(applyRes, {
        'application created': (r) => r.status === 201 || r.status === 200,
    });

    if (applyRes.status >= 400) {
        console.error('❌ 지원서 생성 실패:', applyRes.status, applyRes.body);
    }


    /** 지원서 상태 변경 **/
    const listRes = http.get(`${BASE_URL}/api/v1/applications/recruitment/${recruitmentId}`, {
        headers: { Authorization: token }
    });

    const allApps = listRes.json()?.result?.data;
    if (!Array.isArray(allApps) || allApps.length === 0) {
        console.error('❌ 지원서 목록 조회 실패 또는 비어있음:', listRes.body);
        return;
    }

    const applicationIdsToPass = allApps.slice(0, 5).map(app => app.id);

    const statusChangePayload = JSON.stringify({
        applicationIds: applicationIdsToPass,
        stage: 'DOCUMENT',
        status: 'PASS'
    });

    const statusRes = http.patch(`${BASE_URL}/api/v1/admin/applications/status`, statusChangePayload, {
        headers: {
            Authorization: token,
            'Content-Type': 'application/json'
        }
    });

    check(statusRes, {
        '지원서 상태 변경 성공': (r) => r.status === 200,
    });

    if (statusRes.status >= 400) {
        console.error('❌ 지원서 상태 변경 실패:', statusRes.status, statusRes.body);
    }


    /** 면접 생성 **/
    const interviewCreateRes = http.post(
        `${BASE_URL}/api/v1/interviews/recruitments/${recruitmentId}/interviews`,
        null, // Body 없음
        {
            headers: { Authorization: token },
        }
    );

    check(interviewCreateRes, {
        '면접 생성 성공': (r) => r.status === 200 && r.json()?.result !== undefined,
    });

    if (interviewCreateRes.status >= 400) {
        console.error('❌ 면접 생성 실패:', interviewCreateRes.status, interviewCreateRes.body);
        return;
    }

    const interviewId = interviewCreateRes.json()?.result;

    /** 면접 타임테이블 생성 **/
    const schedulePayload = JSON.stringify({
        interviewerPerSlot: 2,
        applicantPerSlot: 2,
        assistantPerSlot: 2,
        roomCount: 2,
        roomNames: ['Room1', 'Room2'],
    });

    const scheduleRes = http.post(
        `${BASE_URL}/api/v1/interviews/recruitments/${recruitmentId}/interviews/${interviewId}/schedule`,
        schedulePayload,
        {
            headers: {
                Authorization: token,
                'Content-Type': 'application/json',
            },
        }
    );

    check(scheduleRes, {
        '면접 스케줄 생성 성공': (r) => r.status === 200,
    });

    if (scheduleRes.status >= 400) {
        console.error('❌ 면접 스케줄 생성 실패:', scheduleRes.status, scheduleRes.body);
    }
}
