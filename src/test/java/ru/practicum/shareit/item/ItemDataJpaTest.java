package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemDataJpaTest {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    User user1 = new User(null, "Иван", "ivan@test.ru");
    User user2 = new User(null, "Андрей", "andrey@test.ru");
    Item item1 = new Item(null, "Чайник", "Металлический", true, 1, null);
    Item item2 = new Item(null, "Бензопила", "Poulan", true, 2, null);
    Request request1 = new Request(null, "Нужна пила", user1, LocalDateTime.now().minusDays(10));


    @BeforeEach
    void create() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(request1);
    }

    @AfterEach
    void delete() {
        String sql = "DELETE FROM items CASCADE; " +
                "ALTER TABLE items ALTER COLUMN id RESTART WITH 1;" +
                "DELETE FROM requests CASCADE; " +
                "ALTER TABLE requests ALTER COLUMN id RESTART WITH 1;" +
                "DELETE FROM users CASCADE;" +
                "ALTER TABLE users ALTER COLUMN id RESTART WITH 1;";
        jdbcTemplate.update(sql);
        em.clear();
    }

    @Test
    void testFindAllByText() {
        Pageable pageable = PageRequest.of(0, 2);
        Page pageOfItem2 = new PageImpl<Item>(List.of(item1.toBuilder().id(1).build()), pageable, 1);
        Page<Item> item = itemRepository.findAllByText("чайник", pageable);

        Assertions.assertEquals(item, pageOfItem2);
    }

    @Test
    void testFindAllByRequester() {
        Request savedRequest1 = request1.toBuilder().id(1).build();
        em.persist(item2.toBuilder().request(savedRequest1).build());

        Item savedItem2 = item2.toBuilder().id(1).request(request1).build();

        List<Item> items = itemRepository.findAllByRequester(1);
        Assertions.assertEquals(List.of(savedItem2), items);
    }

    @Test
    void testFindAllWithRequests() {
        Request savedRequest1 = request1.toBuilder().id(1).build();
        em.persist(item2.toBuilder().request(savedRequest1).build());

        Item savedItem2 = item2.toBuilder().id(1).request(request1).build();

        List<Item> items = itemRepository.findAllWithRequests(2);
        Assertions.assertEquals(List.of(savedItem2), items);
    }
}