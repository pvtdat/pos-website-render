package com.finalpos.POSsystem.Model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustomerModel {
    @Id
    private String id;

    private String name;
    private String phone;
    private String address;
    private String image;

    public CustomerModel(String name, String phone, String address,String url) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.image = url;
    }
}