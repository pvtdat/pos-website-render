package com.finalpos.POSsystem.Model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductModel {
    @Id
    private String id;
    @Indexed(unique = true)
    private String barcode;
    private String name;
    private int quantity;
    private String description;
    private double import_price;
    private double retail_price;
    private String image;
    private String category;
    private String creation_date;
    private Boolean purchase;
}
