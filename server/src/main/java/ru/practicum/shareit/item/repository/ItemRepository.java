package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemOffer;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findByOwner(int userId, Pageable pageable);

    @Query(value = "select i.ID, i.NAME, i.DESCRIPTION, i.USER_ID, i.IS_AVAILABLE, i.REQUEST_ID " +
            "from ITEMS as i " +
            "where (lower(i.DESCRIPTION) like %?1% or lower(i.NAME) like %?1%) and i.IS_AVAILABLE = true",
            nativeQuery = true)
    Page<Item> findAllByText(String text, Pageable pageable);

    @Query(value = "select new ru.practicum.shareit.item.dto.ItemOffer(" +
            "io.id, io.name, io.description, io.available, io.requestId) " +
            "from ItemOffer as io " +
            "where io.requestId = ?1")
    List<ItemOffer> findAllByRequestId(Integer id, Class<ItemOffer> type);
}