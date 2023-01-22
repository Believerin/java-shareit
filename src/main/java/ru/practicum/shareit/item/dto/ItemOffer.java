package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@IdClass(ItemOfferId.class)
@Table(name = "item_offers", schema = "public")
public class ItemOffer {
    @EqualsAndHashCode.Exclude
    @Id
    @Column(name = "item_id")
    private int id;
    @NotBlank
    @Column
    private String name;
    @NotBlank
    @Column
    private String description;
    @NotNull
    @Column(name = "is_available")
    private boolean available;
    @Id
    @NotNull
    @Column(name = "request_id")
    private int requestId;
}