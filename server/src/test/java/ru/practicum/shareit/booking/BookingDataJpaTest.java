package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingDataJpaTest {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    private static final ZoneId zoneId = ZoneId.of("Europe/Moscow");
    private static final Timestamp currentTime = Timestamp.valueOf(ZonedDateTime.ofInstant(Instant.now(), zoneId).toLocalDateTime());

    User user1 = new User(null, "Иван", "ivan@test.ru");
    User user2 = new User(null, "Андрей", "andrey@test.ru");
    Item item2 = new Item(null, "Бензопила", "Poulan", true, 2, null);
    Booking booking1 = new Booking(null, LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(2), item2, user1, BookingStatus.APPROVED);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void create() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking1);
    }

    @AfterEach
    void delete() {
        String sql = "DELETE FROM bookings CASCADE; " +
                "ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1;" +
                "DELETE FROM items CASCADE; " +
                "ALTER TABLE items ALTER COLUMN id RESTART WITH 1;" +
                "DELETE FROM users CASCADE;" +
                "ALTER TABLE users ALTER COLUMN id RESTART WITH 1;";
        jdbcTemplate.update(sql);
        em.clear();
    }

    @Test
    void testFind() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking1);
        Booking bookingSaved = booking1.toBuilder()
                .id(1)
                .start(LocalDateTime.parse(booking1.getStart().format(dateFormatter), dateFormatter))
                .end(LocalDateTime.parse(booking1.getEnd().format(dateFormatter), dateFormatter))
                .build();
        Booking booking = bookingRepository.find(1, 1).get();
        Booking bookingTest = booking.toBuilder()
                .start(LocalDateTime.parse(booking.getStart().format(dateFormatter), dateFormatter))
                .end(LocalDateTime.parse(booking.getEnd().format(dateFormatter), dateFormatter))
                .build();
        Assertions.assertEquals(bookingSaved, bookingTest);
    }

    @Test
    void testFindByOwner() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking1);
        Booking bookingSaved = booking1.toBuilder()
                .id(1)
                .start(LocalDateTime.parse(booking1.getStart().format(dateFormatter), dateFormatter))
                .end(LocalDateTime.parse(booking1.getEnd().format(dateFormatter), dateFormatter))
                .build();
        Booking booking = bookingRepository.findByOwner(2, 1).get();
        Booking bookingTest = booking.toBuilder()
                .start(LocalDateTime.parse(booking.getStart().format(dateFormatter), dateFormatter))
                .end(LocalDateTime.parse(booking.getEnd().format(dateFormatter), dateFormatter))
                .build();
        Assertions.assertEquals(bookingSaved, bookingTest);
    }

    @Test
    void testGetAllByBookerOrOwner() {
        Pageable pageable = PageRequest.of(0, 2);
        em.persist(user1);
        em.persist(user2);
        em.persist(item2);
        em.persist(booking1);
        Booking bookingSaved = booking1.toBuilder()
                .id(1)
                .start(LocalDateTime.parse(booking1.getStart().format(dateFormatter), dateFormatter))
                .end(LocalDateTime.parse(booking1.getEnd().format(dateFormatter), dateFormatter))
                .build();
        LocalDateTime currentTime = ZonedDateTime.ofInstant(Instant.now(), zoneId).toLocalDateTime();

        Page<Booking> page = bookingRepository.getAllByBookerOrOwner(2, 1, true, currentTime, pageable);
        List<Booking> bookings = page.stream().map(booking -> booking.toBuilder()
                .start(LocalDateTime.parse(booking.getStart().format(dateFormatter), dateFormatter))
                .end(LocalDateTime.parse(booking.getEnd().format(dateFormatter), dateFormatter))
                .build())
                .collect(Collectors.toList());
        Assertions.assertEquals(List.of(bookingSaved), bookings);
    }
}