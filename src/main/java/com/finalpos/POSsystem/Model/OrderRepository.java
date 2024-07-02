package com.finalpos.POSsystem.Model;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<OrderModel, String> {
    OrderModel findByOrderNumber(String order_number);
    List<OrderModel> findByCustomerId(String customer_id);
}