package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findByOwner(int userId, Pageable pageable);

    @Query(value = "select new ru.practicum.shareit.item.model.Item(" +
            "i.id, i.name, i.description, i.available, i.owner, i.request) " +
            "from Item as i " +
            "where (lower(i.description) like %?1% or lower(i.description) like %?1%) and i.available = true")
    Page<Item> findAllByText(String text, Pageable pageable);


    List<Item> findAllByRequestId(Integer id);

    @Query(value = "select new ru.practicum.shareit.item.model.Item(" +
            "i.id, i.name, i.description, i.available, i.owner, i.request) " +
            "from Item as i " +
            "left join Request r on i.request.id = r.id " +
            "where r.requester = ?1")
    List<Item> findAllByRequester(Integer id);

    @Query(value = "select i.ID, i.NAME, i.DESCRIPTION, i.IS_AVAILABLE, i.REQUEST_ID " +
            "from items as i " +
            "right join REQUESTS R on i.REQUEST_ID = R.ID",
            nativeQuery = true)
    List<Item> findAllWithRequests();
}