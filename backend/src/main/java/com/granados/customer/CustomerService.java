package com.granados.customer;

import com.granados.exception.DuplicateResourceException;
import com.granados.exception.RequestValidationException;
import com.granados.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers(){
        return customerDAO.selectAllCustomers();
    }

    public Customer getCustomer(Integer id){
        return customerDAO.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(id)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest customerRequest){
        if(customerDAO.existsPersonWithEmail(customerRequest.email())){
            throw new DuplicateResourceException("email already taken");
        }

        Customer customer = new Customer(
                customerRequest.name(),
                customerRequest.email(),
                customerRequest.age()
        );
        customerDAO.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer id) {
        if(!customerDAO.existsPersonWithId(id)){
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(id));
        }
        customerDAO.deleteCustomerById(id);
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest customerRequest) {
        Customer customer = getCustomer(id);

        boolean changes = false;

        if(customerRequest.name() != null && !customerRequest.name().equals(customer.getName())){
            customer.setName(customerRequest.name());
            changes = true;
        }

        if(customerRequest.age() != null && !customerRequest.age().equals(customer.getAge())){
            customer.setAge(customerRequest.age());
            changes = true;
        }

        if(customerRequest.email() != null && !customerRequest.email().equals(customer.getEmail())){
            if(customerDAO.existsPersonWithEmail(customerRequest.email())){
                throw new DuplicateResourceException("email already taken");
            }
            customer.setEmail(customerRequest.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDAO.updateCustomer(customer);
    }
}
