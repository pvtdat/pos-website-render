package com.finalpos.POSsystem.Model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderDetailModelRepository extends MongoRepository<OrderDetailModel, String> {


    OrderDetailModel findByOrderNumber(String orderNumber);
}
