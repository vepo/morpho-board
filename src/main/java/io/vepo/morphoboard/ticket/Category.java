package io.vepo.morphoboard.ticket;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_categories")
public class Category extends PanacheEntity {
    public String name;
} 