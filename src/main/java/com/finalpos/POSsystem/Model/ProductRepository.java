package com.finalpos.POSsystem.Model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository extends MongoRepository<ProductModel, String> {
    ProductModel findByBarcode(String barcode);
    ProductModel removeProductModelByBarcode(String barcode);

    @Query("{'$or':[{'name': {$regex : ?0, $options: 'i'}}, {'description': {$regex : ?0, $options: 'i'}}, {'category': {$regex : ?0, $options: 'i'}}]}")
    List<ProductModel> findByTerm(String term);
}
