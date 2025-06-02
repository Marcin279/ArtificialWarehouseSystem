import grpc from 'k6/net/grpc';
import {Trend} from 'k6/metrics';
import {check} from 'k6';

const client = new grpc.Client();
console.log("Ładowanie pliku product.proto...");
client.load(['.'], 'product.proto');
console.log("Załadowano plik product.proto");

const grpcDuration = new Trend('grpc_duration');

export default function () {
    client.connect('localhost:9095', {plaintext: true});
    console.log("Połączono z serwerem gRPC");


    if (!client) {
        console.error("Błąd: Połączenie z gRPC nie zostało poprawnie nawiązane.");
        return;
    }
    

    const start = new Date().getTime();
    try {
        const response = client.invoke('pl.bielamarcin.productsservice.grpc.ProductService/getAllProducts', data);
        console.log("Odpowiedź serwera:", response);
    } catch (error) {
        console.error("Błąd podczas wywołania metody gRPC:", error);
    }
    
    const end = new Date().getTime();

    grpcDuration.add(end - start);

    check(response, {
        'status is OK': (r) => r && r.status === grpc.StatusOK,
        'contains products': (r) => r && r.message.products.length > 0
    });

    client.close();
}
