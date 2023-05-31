package com.granados.customer;

import com.granados.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        //given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );
        underTest.insertCustomer(customer);
        //when
        List<Customer> actual = underTest.selectAllCustomers();

        //then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        //given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        //when
        Optional<Customer> actual = underTest.selectCustomerById(id);
        //then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById(){
        //given
        int id = -1;

        //when
        var actual = underTest.selectCustomerById(id);

        //then
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        //given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );
        underTest.insertCustomer(customer);
        //when
        List<Customer> actual = underTest.selectAllCustomers();

        //then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void existsPersonWithEmail() {
        //given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);

        //when
        boolean actual = underTest.existsPersonWithEmail(email);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists(){
        //given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        //when
        boolean actual = underTest.existsPersonWithEmail(email);

        //then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerWithId() {
        //given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //when
        boolean actual = underTest.existsPersonWithId(id);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithIdWillReturnFalseWhenIdNotPresent() {
        //given
        Integer id = -1;
        //when
        var actual = underTest.existsPersonWithId(id);
        //then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        //given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        //when
        underTest.deleteCustomerById(id);
        //then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomer() {
        //given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String updatedName = FAKER.name().fullName();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        //when
        underTest.updateCustomer(new Customer(
                id,
                updatedName,
                email,
                20
        ));
        //then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getName()).isEqualTo(updatedName);
        });
    }
}