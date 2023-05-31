package com.granados.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {

        //given
        CustomerRowMapper underTest = new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        Integer id = 1;
        String name = "German";
        String email = "ggranados@gmail.com";
        Integer age = 20;

        when(resultSet.getInt("id")).thenReturn(id);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getString("email")).thenReturn(email);
        when(resultSet.getInt("age")).thenReturn(age);
        //when
        Customer actual = underTest.mapRow(resultSet, 1);
        //then
        Customer expected = new Customer(id, name, email, age);

        assertThat(actual).isEqualTo(expected);
    }
}