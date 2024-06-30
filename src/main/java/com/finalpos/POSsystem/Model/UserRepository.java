package com.finalpos.POSsystem.Model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {
    UserModel findByUsername(String username);
    UserModel findUserModelById(String userId);
    UserModel removeUserModelById(String userId);

    UserModel findByEmail(String email);
}
