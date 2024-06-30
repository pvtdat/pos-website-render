package com.finalpos.POSsystem.Model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductCartRepository extends MongoRepository<ProductCartModel, String> {

}
