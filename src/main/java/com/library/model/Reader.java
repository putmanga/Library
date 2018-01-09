package com.library.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Reader {
    private Integer id;
    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
}
