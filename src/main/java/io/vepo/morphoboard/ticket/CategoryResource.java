package io.vepo.morphoboard.ticket;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {
    public static record CategoryResponse(String name) {
    }

    private static final CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.name);
    }

    @Inject
    CategoryRepository repository;

    @GET
    public List<CategoryResponse> listAll() {
        return repository.streamAll()
                         .map(CategoryResource::toResponse)
                         .toList();
    }
}