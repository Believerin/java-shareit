package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query(value = "select new ru.practicum.shareit.booking.model.Booking(" +
            "b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b " +
            "left join Item as i on i.id = b.item.id " +
            "where b.id = ?2 and (i.owner = ?1 or b.booker.id = ?1)")
    Optional<Booking> find(int userId, int bookingId);

    @Query(value = "select new ru.practicum.shareit.booking.model.Booking(" +
            "b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b " +
            "left join Item as i on i.id = b.item.id " +
            "where b.id = ?2 and i.owner = ?1")
    Optional<Booking> findByOwner(int userId, int bookingId);

    @Modifying
    @Transactional
    @Query(value = "update bookings as b set status = case " +
            "when ?2 = true then 'APPROVED' else 'REJECTED' end where b.id = ?1",
            nativeQuery = true)
    void accept(int bookingId, boolean approved);

    List<Booking> findByItemIdOrderByStartAsc(int itemId);

    @Query(value = "select new ru.practicum.shareit.booking.model.Booking(" +
            "b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "from Booking as b left join Item as i on b.item.id = i.id " +
            "where " +
                "((b.status = 'APPROVED' or b.status = 'REJECTED' or b.status = 'WAITING' or b.status = 'CANCELLED') and ?2 = 1) " +
                "or (b.end > current_timestamp and b.start < current_timestamp and ?2 = 2) " +
                "or (b.status = 'APPROVED' and b.start < current_timestamp and b.end < current_timestamp and ?2 = 3) " +
                "or ( b.start > current_timestamp and b.end > current_timestamp and ?2 = 4) " +
                "or (b.status = 'WAITING'and ?2 = 5) " +
                "or (b.status = 'CANCELLED' or b.status = 'REJECTED' and ?2 = 6) " +
                "and ((b.booker.id = ?1 and ?3 = false) or (i.owner = ?1 and ?3 = true)) " +
                "order by b.start desc")
    List<Booking> getAllByBookerOrOwner(int bookerOrOwnerId, int status, boolean isOwner);

    List<Booking> findByItemIdInOrderByStartAsc(List<Integer> itemIds);
}