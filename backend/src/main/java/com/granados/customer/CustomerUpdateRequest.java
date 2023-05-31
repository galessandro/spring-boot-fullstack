package com.granados.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age) {}
