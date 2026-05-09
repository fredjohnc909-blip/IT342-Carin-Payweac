package edu.cit.carin.payweac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("edu.cit.carin.payweac.features")
@EnableJpaRepositories("edu.cit.carin.payweac.features")
public class PayweacApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayweacApplication.class, args);
    }
}
