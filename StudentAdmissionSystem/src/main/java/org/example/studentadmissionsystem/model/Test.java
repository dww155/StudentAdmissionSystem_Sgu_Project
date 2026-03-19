package org.example.studentadmissionsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "test")
public class Test {

    @Id
    @Column(name = "idtest")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idtest;

    @Column(name = "name")
    private String name;
}