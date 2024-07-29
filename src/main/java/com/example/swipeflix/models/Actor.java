package com.example.swipeflix.models;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Actor {

    String character_name;
    String actor_name;

}
