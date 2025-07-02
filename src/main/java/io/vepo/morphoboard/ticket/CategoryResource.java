package io.vepo.morphoboard.ticket;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {
    @Inject
    CategoryRepository repository;

    @GET
    public List<Category> listAll() {
        return repository.listAll();
    }
} 