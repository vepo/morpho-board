package dev.vepo.morphoboard.ticket;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_categories")
public class Category extends PanacheEntity {
    public String name;
    public String color;

    public Category() {

    }

    public Category(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Category(String name) {
        this(name, "none");
    }
}