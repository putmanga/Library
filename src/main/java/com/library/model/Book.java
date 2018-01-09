package com.library.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Book {
    private Integer id;
    private String bookName;
    private String author;
    private Integer issueYear;

}
