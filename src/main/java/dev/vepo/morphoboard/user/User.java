package dev.vepo.morphoboard.user;

import java.util.List;
import java.util.Set;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import dev.vepo.morphoboard.ticket.Ticket;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_users")
public class User extends PanacheEntity {
    public String name;

    public String email;
    
    @Column(name = "encoded_password")
    public String encodedPassword;
    
    @Enumerated(EnumType.STRING)
    public Set<Role> roles;
    
    // all assigned tickets    
    @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
    public List<Ticket> assignedTickets;

    // all created tickets
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    public List<Ticket> createdTickets;

    public User(){}

    public User(String name, String email, String encodedPassword, Set<Role> roles) {
        this.name = name;
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.roles = roles;
    }
} 