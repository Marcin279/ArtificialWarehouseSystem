package pl.bielamarcin.ordersservice.exception;

public class ServiceGrpcCommunicationException extends RuntimeException {
    public ServiceGrpcCommunicationException(String message) {
        super(message);
    }
}
