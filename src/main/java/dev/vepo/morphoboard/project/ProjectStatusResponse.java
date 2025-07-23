package dev.vepo.morphoboard.project;

import java.util.List;

public record ProjectStatusResponse(long id,
                                    String name,
                                    boolean start,
                                    List<Long> moveable) {
}