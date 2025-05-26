import http from 'k6/http';
import {Trend} from 'k6/metrics';
import {check} from 'k6';

export const options = {
    vus: 2,  // Liczba wirtualnych użytkowników
    duration: '30s',  // Czas trwania testu
};

export default function () {
    const start = new Date().getTime();
    const response = http.get('http://localhost:8080/api/products');
    const end = new Date().getTime();

    apiDuration.add(end - start);

    check(response, {
        'status is 200': (r) => r.status === 200,
        'response time is below 500ms': (r) => r.timings.duration < 500,
    });
}
