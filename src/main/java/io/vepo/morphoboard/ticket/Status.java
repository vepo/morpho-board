package io.vepo.morphoboard.ticket;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_statuses")
public class Status extends PanacheEntity {
    public String name;
} 