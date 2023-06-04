package com.example.p_jdbcrestapi_couchbase_mvc.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Item {
    private int invoice_nr;
    private Date invoice_date;
    private int id_item;
    private int id_prod;
    private String unit;
    private int quantity;
    private float unit_price;
}
