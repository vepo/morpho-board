package io.vepo.morphoboard.user;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.vepo.morphoboard.ticket.Ticket;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_users")
public class User extends PanacheEntity {
    public String name;
    public String email;
    public String password;
    public String role;
    // all assigned tickets
    @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
    public List<Ticket> assignedTickets;

    // all created tickets
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    public List<Ticket> createdTickets;

    public User(){}

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
} 