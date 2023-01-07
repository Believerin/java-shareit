package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class RequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private boolean available;
}