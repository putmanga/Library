package com.library.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookOrder {
    private Book book;
    private Reader reader;
    private Date orderDate;
    private Date returnDate;
}
