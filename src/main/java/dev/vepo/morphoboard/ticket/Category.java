package dev.vepo.morphoboard.ticket;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_categories")
public class Category {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String color;

    public Category() {

    }

    public Category(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Category(String name) {
        this(name, "none");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}