package ru.practicum.shareit.item;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findByOwner(int userId, Pageable pageable);

    @Query(value = "select i.ID, i.NAME, i.DESCRIPTION, i.USER_ID, i.IS_AVAILABLE, i.REQUEST_ID " +
            "from ITEMS as i " +
            "where (lower(i.DESCRIPTION) like %?1% or lower(i.NAME) like %?1%) and i.IS_AVAILABLE = true",
            nativeQuery = true)
    Page<Item> findAllByText(String text, Pageable pageable);

    List<Item> findAllByRequestId(Integer id);

    @Query(value = "select new ru.practicum.shareit.item.model.Item(" +
            "i.id, i.name, i.description, i.available, i.owner, i.request) " +
            "from Item as i " +
            "left join Request r on i.request.id = r.id " +
            "where r.requester.id = ?1")
    List<Item> findAllByRequester(Integer id);

    @Query(value = "select i.ID, i.NAME, i.DESCRIPTION, i.USER_ID, i.IS_AVAILABLE, i.REQUEST_ID " +
            "from ITEMS as i " +
            "right join REQUESTS R on i.REQUEST_ID = R.ID " +
            "where R.USER_ID <> ?1",
            nativeQuery = true)
    List<Item> findAllWithRequests(Integer id);
}