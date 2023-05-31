package com.granados.customer;

import com.granados.exception.DuplicateResourceException;
import com.granados.exception.RequestValidationException;
import com.granados.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDAO customerDAO;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO);
    }

    @Test
    void getAllCustomers() {
        //when
        underTest.getAllCustomers();
        //then
        verify(customerDAO).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //given
        Integer id = 1;
        Customer customer = new Customer(
                id,
                "German",
                "ggranados@gmail.com",
                20
        );
        when(customerDAO.selectCustomerById(id))
                .thenReturn(Optional.of(customer));
        //when
        Customer actual = underTest.getCustomer(id);
        //then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        //given
        Integer id = 1;

        when(customerDAO.selectCustomerById(id))
                .thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "customer with id [%s] not found".formatted(id));
    }

    @Test
    void canAddCustomer() {
        //given
        String email = "ggranados@gmail.com";
        when(customerDAO.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "German",
                email,
                20
        );
        //when
        underTest.addCustomer(request);

        //then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCostumer = customerArgumentCaptor.getValue();

        assertThat(capturedCostumer.getId()).isNull();
        assertThat(capturedCostumer.getName()).isEqualTo(request.name());
        assertThat(capturedCostumer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCostumer.getAge()).isEqualTo(request.age());

    }

    @Test
    void willThrowWhenEmailExistsWhileAddCustomer() {
        //given
        String email = "ggranados@gmail.com";
        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "German",
                email,
                20
        );
        //when
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //then
        verify(customerDAO, never()).insertCustomer(any());
    }

    @Test
    void canDeleteCustomerById() {
        //given
        Integer id = 1;
        when(customerDAO.existsPersonWithId(id)).thenReturn(true);
        //when
        underTest.deleteCustomerById(id);
        //then
        verify(customerDAO).deleteCustomerById(id);
    }

    @Test
    void willThrowDeleteCustomerByIdNotExists() {
        //given
        Integer id = 1;
        when(customerDAO.existsPersonWithId(id)).thenReturn(false);
        //when
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
        //then
        verify(customerDAO, never()).deleteCustomerById(id);
    }

    @Test
    void canUpdateAllCustomerProperties() {
        //given
        Integer id = 1;
        String newName = "Germani";
        String newEmail = "ggranadi@gmail.com";
        Integer newAge = 30;

        Customer customer = new Customer(
                id,
                "German",
                "ggranados@gmail.com",
                20
        );
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                newName,
                newEmail,
                newAge
        );

        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(false);

        //when
        underTest.updateCustomer(id, request);

        //then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCostumer = customerArgumentCaptor.getValue();

        assertThat(capturedCostumer.getId()).isEqualTo(id);
        assertThat(capturedCostumer.getName()).isEqualTo(request.name());
        assertThat(capturedCostumer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCostumer.getAge()).isEqualTo(request.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        //given
        Integer id = 1;
        String newName = "Germani";

        Customer customer = new Customer(
                id,
                "German",
                "ggranados@gmail.com",
                20
        );
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                newName,
                null,
                null
        );

        //when
        underTest.updateCustomer(id, request);

        //then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCostumer = customerArgumentCaptor.getValue();

        assertThat(capturedCostumer.getId()).isEqualTo(id);
        assertThat(capturedCostumer.getName()).isEqualTo(request.name());
        assertThat(capturedCostumer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCostumer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        //given
        Integer id = 1;

        Customer customer = new Customer(
                id,
                "German",
                "ggranados@gmail.com",
                20
        );
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "ggranadi@gmai.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                newEmail,
                null
        );
        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(false);

        //when
        underTest.updateCustomer(id, request);

        //then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCostumer = customerArgumentCaptor.getValue();

        assertThat(capturedCostumer.getId()).isEqualTo(id);
        assertThat(capturedCostumer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCostumer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCostumer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        //given
        Integer id = 1;

        Customer customer = new Customer(
                id,
                "German",
                "ggranados@gmail.com",
                20
        );
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                null,
                30
        );

        //when
        underTest.updateCustomer(id, request);

        //then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCostumer = customerArgumentCaptor.getValue();

        assertThat(capturedCostumer.getId()).isEqualTo(id);
        assertThat(capturedCostumer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCostumer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCostumer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        //given
        Integer id = 1;

        Customer customer = new Customer(
                id,
                "German",
                "ggranados@gmail.com",
                20
        );
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "ggranadi@gmai.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                newEmail,
                null
        );
        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(true);

        //then
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDAO, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenUpdateCustomerHasNoChanges() {
        //given
        Integer id = 1;
        Customer customer = new Customer(
                id,
                "German",
                "ggranados@gmail.com",
                20
        );
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );

        //then
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        verify(customerDAO, never()).updateCustomer(any());
    }

}