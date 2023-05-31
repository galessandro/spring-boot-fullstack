package com.granados.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return service.getAllCustomers();
    }

    @GetMapping("{id}")
    public Customer getCustomer(@PathVariable("id") Integer id) {
        return service.getCustomer(id);
    }

    @PostMapping
    public void registerCustomer(
            @RequestBody CustomerRegistrationRequest customerRequest){
        service.addCustomer(customerRequest);
    }

    @PutMapping("{id}")
    public void editCustomer(@PathVariable("id") Integer id,
            @RequestBody CustomerUpdateRequest customerRequest){
        service.updateCustomer(id, customerRequest);
    }

    @DeleteMapping("{id}")
    public void deleteCustomer(@PathVariable("id") Integer id){
        service.deleteCustomerById(id);
    }
}
