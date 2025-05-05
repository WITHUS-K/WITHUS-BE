import http from 'k6/http';
import { check } from 'k6';

export const options = {
  scenarios: {
    high_rps_test: {
      executor: 'constant-arrival-rate',
      rate: 50, // 초당 50개의 요청
      timeUnit: '1s',
      duration: '2m',
      preAllocatedVUs: 20, // 최소한 VU 수
      maxVUs: 100,
    },
  },
};

export default function () {
  const res = http.get('https://jk-project.site/health');
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
}
