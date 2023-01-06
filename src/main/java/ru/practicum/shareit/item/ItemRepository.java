package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByOwner(int userId);

    @Query(value = "select new ru.practicum.shareit.item.model.Item(" +
            "i.id, i.name, i.description, i.available, i.owner, i.request) " +
            "from Item as i " +
            "where (lower(i.description) like %?1% or lower(i.description) like %?1%) and i.available = true")
    List<Item> findAllByText(String text);
}