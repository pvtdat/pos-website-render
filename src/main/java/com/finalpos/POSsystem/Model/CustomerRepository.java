package com.finalpos.POSsystem.Model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<CustomerModel, String>{
    CustomerModel findByPhone(String phone);
    CustomerModel findCustomerById(String id);
}
