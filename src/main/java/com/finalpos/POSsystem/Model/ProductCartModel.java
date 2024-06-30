package com.finalpos.POSsystem.Model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductCartModel {
    @Id
    private String id;
    private String barcode;
    private String name;
    private int quantity;
    private int import_price;
    private int retail_price;
}
