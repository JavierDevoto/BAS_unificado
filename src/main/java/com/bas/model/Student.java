package com.bas.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    private String id = "";
    private String firstName = "";
    private String lastName = "";
    private List<String> parentsCuil = new ArrayList<>();

}
