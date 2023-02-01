package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreated;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(
		properties = "db.name=test",
		webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingIntegrationAndWebTest {

	private final EntityManager em;
	private final JdbcTemplate jdbcTemplate;
	private final BookingService bookingService;
	private final ItemService itemService;
	private final UserService userService;
	private MockMvc mvc;

	@Mock
	private final BookingService bookingServiceWeb;
	@Autowired
	private final ObjectMapper mapper;
	@InjectMocks
	private BookingController bookingController;

	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	UserDto userDto1 = new UserDto(null, "Иван", "ivan@test.ru");
	UserDto userDto2 = new UserDto(2, "Андрей", "andrey@test.ru");
	User user2 = new User(2, "Андрей", "andrey@test.ru");
	Item item1 = new Item(1, "Чайник", "Металлический", true, 1, null);
	ItemDtoCreated itemDtoCreated = new ItemDtoCreated(null, "Чайник", "Металлический", true, null);
	BookingDtoCreated bookingDtoCreated = new BookingDtoCreated(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(7), 1);
	BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(7),
			BookingDto.Item.builder().id(2).name("Бензопила").build(),
			BookingDto.Booker.builder().id(1).name("Иван").build(), BookingStatus.APPROVED);

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
				.standaloneSetup(bookingController)
				.build();
	}

	@Test
	void testGet() {
		String sql = "DELETE FROM items items; " +
				"ALTER TABLE items ALTER COLUMN id RESTART WITH 1; " +
				"DELETE FROM users CASCADE; " +
				"ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
		jdbcTemplate.update(sql);

		userService.add(userDto1);
		userService.add(userDto2);
		itemService.add(1, itemDtoCreated);
		bookingService.add(bookingDtoCreated, 2);

		TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.booker.id = :bookerId", Booking.class);
		Booking booking = query.setParameter("bookerId", 2).getSingleResult();

		assertThat(booking.getId(), notNullValue());
		assertThat(booking.getStart(), equalTo(bookingDtoCreated.getStart()));
		assertThat(booking.getEnd(), equalTo(bookingDtoCreated.getEnd()));
		assertThat(booking.getBooker(), equalTo(user2));
		assertThat(booking.getItem(), equalTo(item1));
		assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
	}

	@Test
	void add() throws Exception {
		when(bookingServiceWeb.add(any(), anyInt()))
				.thenReturn(bookingDto);

		mvc.perform(post("/bookings")
						.content(mapper.writeValueAsString(bookingDtoCreated))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
				.andExpect(jsonPath("$.start").value((bookingDto.getStart()).format(dateFormatter)))
				.andExpect(jsonPath("$.end").value((bookingDto.getEnd()).format(dateFormatter)))
				.andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId())))
				.andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
				.andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId())))
				.andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
				.andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
	}

	@Test
	void acceptOrDeny() throws Exception {
		when(bookingServiceWeb.acceptOrDeny(anyInt(), anyBoolean(), anyInt()))
				.thenReturn(bookingDto);

		mvc.perform(patch("/bookings/1?approved=true")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
				.andExpect(jsonPath("$.start").value((bookingDto.getStart()).format(dateFormatter)))
				.andExpect(jsonPath("$.end").value((bookingDto.getEnd()).format(dateFormatter)))
				.andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId())))
				.andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
				.andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId())))
				.andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
				.andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
	}

	@Test
	void getBooking() throws Exception {
		when(bookingServiceWeb.get(anyInt(), anyInt()))
				.thenReturn(bookingDto);

		mvc.perform(get("/bookings/1")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
				.andExpect(jsonPath("$.start").value((bookingDto.getStart()).format(dateFormatter)))
				.andExpect(jsonPath("$.end").value((bookingDto.getEnd()).format(dateFormatter)))
				.andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId())))
				.andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
				.andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId())))
				.andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
				.andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
	}

	@Test
	void getAllByBooker() throws Exception {
		when(bookingServiceWeb.getAllByBooker(anyInt(), anyString(), anyInt(), anyInt()))
				.thenReturn(List.of(bookingDto));

		mvc.perform(get("/bookings?state=ALL&from=0&size=2")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Integer.class))
				.andExpect(jsonPath("$[0].start").value((bookingDto.getStart()).format(dateFormatter)))
				.andExpect(jsonPath("$[0].end").value((bookingDto.getEnd()).format(dateFormatter)))
				.andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId())))
				.andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
				.andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId())))
				.andExpect(jsonPath("$[0].booker.name", is(bookingDto.getBooker().getName())))
				.andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
	}

	@Test
	void getAllByOwner() throws Exception {
		when(bookingServiceWeb.getAllByOwner(anyInt(), anyString(), anyInt(), anyInt()))
				.thenReturn(List.of(bookingDto));

		mvc.perform(get("/bookings/owner?state=ALL&from=0&size=2")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Integer.class))
				.andExpect(jsonPath("$[0].start").value((bookingDto.getStart()).format(dateFormatter)))
				.andExpect(jsonPath("$[0].end").value((bookingDto.getEnd()).format(dateFormatter)))
				.andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId())))
				.andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
				.andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId())))
				.andExpect(jsonPath("$[0].booker.name", is(bookingDto.getBooker().getName())))
				.andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
	}
}