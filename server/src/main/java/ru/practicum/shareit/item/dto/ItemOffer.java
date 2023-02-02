package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.persistence.*;

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
    @Column
    private String name;
    @Column
    private String description;
    @Column(name = "is_available")
    private boolean available;
    @Id
    @Column(name = "request_id")
    private int requestId;
}