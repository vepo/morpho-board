package io.vepo.morphoboard.change;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Change extends PanacheEntity {
    public String title;
    public String description;
    public String status; // "TO_DO", "IN_PROGRESS", etc.
}