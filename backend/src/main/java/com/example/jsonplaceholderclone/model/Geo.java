package com.example.jsonplaceholderclone.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Geo {

  private String lat;
  private String lng;
}
