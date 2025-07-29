package dev.vepo.morphoboard.user;

import java.util.List;
import java.util.Set;

import dev.vepo.morphoboard.ticket.Ticket;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String email;

    @Column(name = "encoded_password")
    private String encodedPassword;

    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    // all assigned tickets
    @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
    private List<Ticket> assignedTickets;

    // all created tickets
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Ticket> createdTickets;

    public User() {}

    public User(String name, String email, String encodedPassword, Set<Role> roles) {
        this.name = name;
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.roles = roles;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Ticket> getAssignedTickets() {
        return assignedTickets;
    }

    public void setAssignedTickets(List<Ticket> assignedTickets) {
        this.assignedTickets = assignedTickets;
    }

    public List<Ticket> getCreatedTickets() {
        return createdTickets;
    }

    public void setCreatedTickets(List<Ticket> createdTickets) {
        this.createdTickets = createdTickets;
    }
}