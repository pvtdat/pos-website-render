package com.finalpos.POSsystem.Model;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

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