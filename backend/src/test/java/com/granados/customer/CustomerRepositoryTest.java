package com.granados.customer;

import com.granados.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers{

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    void setUp() {
        System.out.println(context.getBeanDefinitionCount());
    }

    @Test
    void existsCustomerByEmail() {
        //given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.save(customer);
        //when
        var actual = underTest.existsCustomerByEmail(email);
        //then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmailFailsWhenEmailNotPresent() {
        //given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        //when
        var actual = underTest.existsCustomerByEmail(email);
        //then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {
        //given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.save(customer);

        Integer id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        //when
        var actual = underTest.existsCustomerById(id);
        //then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByIdFailsWhenIdNotPresent() {
        //given
        Integer id = -1;
        //when
        var actual = underTest.existsCustomerById(id);
        //then
        assertThat(actual).isFalse();
    }
}