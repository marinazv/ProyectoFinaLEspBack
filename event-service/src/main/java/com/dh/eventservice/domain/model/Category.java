package com.dh.eventservice.domain.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    private String urlImage;

}
