package com.finalpos.POSsystem.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserModel {
    @Id
    private String id;
    private String username;
    private String name;
    private String email;
    private String password;
    private String image;
    private String role;
    private String status;
    private LocalDateTime created_at;
}

