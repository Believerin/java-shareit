package ru.practicum.shareit.item.model;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class Item {
    int id;
    @NotNull
    @NotEmpty
    String name;
    @NotNull
    String description;
    @NotNull
    Boolean available;
    int owner;
    Integer request;
}