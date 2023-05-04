package com.example.p_jdbcrestapi_couchbase_mvc.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    private int nr;
    private LocalDate invoice_date;
    private char type;
    private float value;
    private float vat;
}


