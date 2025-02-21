package pl.bielamarcin.productsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
product-service/
│── src/main/java/com/example/productservice/
│   ├── ProductServiceApplication.java
│   ├── config/                # Konfiguracje aplikacji (Kafka, Security itp.)
│   ├── controller/            # Kontrolery REST API
│   ├── service/               # Logika biznesowa
│   ├── repository/            # Interakcja z bazą danych
│   ├── model/                 # Modele JPA
│   ├── event/                 # Klasy zdarzeń Kafka
│── src/main/resources/
│   ├── application.yml        # Konfiguracja aplikacji
│── Dockerfile                 # Plik Dockerfile
│── pom.xml                    # Zależności Maven
* */

@SpringBootApplication
public class ProductsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductsServiceApplication.class, args);
    }

}
