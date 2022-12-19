package ru.practicum.shareit.item.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
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
    @NotBlank
    String name;
    @NotNull
    String description;
    @NotNull
    Boolean available;
    int owner;
    Integer request;
}