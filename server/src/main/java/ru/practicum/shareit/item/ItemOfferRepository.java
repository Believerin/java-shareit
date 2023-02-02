package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemOffer;

import java.util.List;

public interface ItemOfferRepository extends JpaRepository<ItemOffer, Integer> {

    List<ItemOffer> findAllByRequestId(int requestId);

    @Query(value = "select new ru.practicum.shareit.item.dto.ItemOffer(" +
            "io.id, io.name, io.description, io.available, io.requestId) " +
            "from ItemOffer as io " +
            "left join Request r on io.requestId = r.id " +
            "where r.requester.id = ?1")
    List<ItemOffer> findAllByRequester(int requesterId);

    @Query(value = "select io.ITEM_ID, io.NAME, io.DESCRIPTION, io.IS_AVAILABLE, io.REQUEST_ID " +
            "from ITEM_OFFERS as io " +
            "right join REQUESTS R on io.REQUEST_ID = R.ID " +
            "where R.USER_ID <> ?1",
            nativeQuery = true)
    List<ItemOffer> findAllWithRequests(int userId);


}