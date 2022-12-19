package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    Integer id;
    @NotBlank
    String name;
    String description;
    Boolean available;
    Integer request;

}