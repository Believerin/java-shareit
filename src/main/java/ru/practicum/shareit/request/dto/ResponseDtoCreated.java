package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDtoCreated {
    @EqualsAndHashCode.Exclude
    private int id;
    @NotBlank
    private String description;
    @NotNull
    private LocalDateTime created;
}