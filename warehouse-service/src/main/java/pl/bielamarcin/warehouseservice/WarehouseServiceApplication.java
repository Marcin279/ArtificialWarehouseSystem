package pl.bielamarcin.warehouseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class WarehouseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseServiceApplication.class, args);
    }

}
