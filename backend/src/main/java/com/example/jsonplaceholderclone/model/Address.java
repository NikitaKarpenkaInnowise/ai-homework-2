package com.example.jsonplaceholderclone.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "addresses")
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String street;
  private String suite;
  private String city;
  private String zipcode;

  @Embedded private Geo geo;
}
