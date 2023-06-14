package com.granados.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDAO {

    private final JdbcTemplate template;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate template,
                                         CustomerRowMapper customerRowMapper) {
        this.template = template;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT id, name, email, age, gender
                FROM customer
                """;

        return template.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                SELECT id, name, email, age, gender
                FROM customer
                WHERE id = ?
                """;

        return template.query(sql, customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name, email, age, gender)
                VALUES (?, ?, ?, ?)
                """;
        template.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender().name());
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;
        Integer count = template.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;
        Integer count = template.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer id) {
        var sql = """
                DELETE FROM customer
                WHERE id = ?
                """;
        template.update(sql, id);
    }

    @Override
    public void updateCustomer(Customer editedCustomer) {
        var sql = """
                UPDATE customer
                SET name = ?, email = ?, age = ?, gender = ?
                WHERE id = ?
                """;
        template.update(sql,
                editedCustomer.getName(),
                editedCustomer.getEmail(),
                editedCustomer.getAge(),
                editedCustomer.getGender().name(),
                editedCustomer.getId());
    }
}
