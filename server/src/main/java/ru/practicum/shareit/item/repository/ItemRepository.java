package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findByOwnerOrderByIdAsc(int userId, Pageable pageable);

    @Query(value = "select i.ID, i.NAME, i.DESCRIPTION, i.USER_ID, i.IS_AVAILABLE, i.REQUEST_ID " +
            "from ITEMS as i " +
            "where (lower(i.DESCRIPTION) like %?1% or lower(i.NAME) like %?1%) and i.IS_AVAILABLE = true",
            nativeQuery = true)
    Page<Item> findAllByText(String text, Pageable pageable);
}