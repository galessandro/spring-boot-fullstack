package com.granados;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.granados.customer.Customer;
import com.granados.customer.CustomerRepository;
import com.granados.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            Faker faker = new Faker();
            Random random = new Random();
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            Gender gender = faker.options().option(Gender.class);
            Customer customer = new Customer(
                    firstName + " " + lastName,
                    firstName.toLowerCase() + "." + lastName.toLowerCase() + "@granados.com",
                    random.nextInt(16, 99), gender);
            customerRepository.save(customer);
        };
    }
}