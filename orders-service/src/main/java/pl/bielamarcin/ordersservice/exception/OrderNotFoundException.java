package pl.bielamarcin.ordersservice.exception;

public class OrderNotFoundException extends Exception {

    public OrderNotFoundException(String message) {
        super(message);
    }
}