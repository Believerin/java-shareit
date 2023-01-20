package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.RequestController;
import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest(
		properties = "db.name=test",
		webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestIntegrationAndWebTest {

	private final EntityManager em;
	private final JdbcTemplate jdbcTemplate;
	private final ItemService itemService;
	private final UserService userService;
	private final RequestService requestService;
	@Mock
	private final RequestService requestServiceWeb;
	private MockMvc mvc;
	@Autowired
	private final ObjectMapper mapper;
	@InjectMocks
	private RequestController requestController;

	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	UserDto userDto1 = new UserDto(null, "Иван", "ivan@test.ru");
	UserDto userDto2 = new UserDto(2, "Андрей", "andrey@test.ru");
	User user2 = new User(2, "Андрей", "andrey@test.ru");
	ItemDtoCreated itemDtoCreated = new ItemDtoCreated(null, "Чайник", "Металлический", true, null);

	RequestDto requestDto = new RequestDto("Нужна пила");
	ResponseDtoCreated responseDtoCreated = new ResponseDtoCreated(1, "Нужна пила", LocalDateTime.now().minusDays(2));
	ResponseDto responseDto = new ResponseDto(1, "Нужна пила", LocalDateTime.now().minusDays(2),
			List.of(ResponseDto.ItemOffer.builder().id(2).name("Бензопила").description("Poulan").available(true).requestId(1).build()));


	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
				.standaloneSetup(requestController)
				.build();
	}

	@Test
	void testGetAllOwn() {
		String sql = "DELETE FROM users CASCADE; " +
				"ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
		jdbcTemplate.update(sql);
		userService.add(userDto1);
		userService.add(userDto2);
		itemService.add(1, itemDtoCreated);
		requestService.add(requestDto, 2);

		TypedQuery<Request> query = em.createQuery("Select r from Request r where r.requester.id = :requesterId", Request.class);
		Request request = query.setParameter("requesterId", 2).getSingleResult();

		assertThat(request.getId(), notNullValue());
		assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
		assertThat(request.getRequester(), equalTo(user2));
		assertThat(request.getCreated(), greaterThan(LocalDateTime.now().minusSeconds(5)));
	}

	@Test
	void add() throws Exception {
		when(requestServiceWeb.add(any(),anyInt()))
				.thenReturn(responseDtoCreated);

		mvc.perform(post("/requests")
						.content(mapper.writeValueAsString(requestDto))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(responseDtoCreated.getId()), Integer.class))
				.andExpect(jsonPath("$.description", is(responseDtoCreated.getDescription())))
				.andExpect(jsonPath("$.created").value((responseDtoCreated.getCreated()).format(dateFormatter)));
	}

	@Test
	void getRequest() throws Exception {
		when(requestServiceWeb.get(anyInt(),anyInt()))
				.thenReturn(responseDto);

		mvc.perform(get("/requests/1")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(responseDto.getId()), Integer.class))
				.andExpect(jsonPath("$.description", is(responseDto.getDescription())))
				.andExpect(jsonPath("$.created").value((responseDto.getCreated()).format(dateFormatter)))
				.andExpect(jsonPath("$.items[0].id", is(responseDto.getItems().get(0).getId())))
				.andExpect(jsonPath("$.items[0].name", is(responseDto.getItems().get(0).getName())))
				.andExpect(jsonPath("$.items[0].description", is(responseDto.getItems().get(0).getDescription())))
				.andExpect(jsonPath("$.items[0].available", is(responseDto.getItems().get(0).isAvailable())))
				.andExpect(jsonPath("$.items[0].requestId", is(responseDto.getItems().get(0).getRequestId())));
	}

	@Test
	void findAll() throws Exception {
		when(requestServiceWeb.getAll(anyInt(),anyInt(), anyInt()))
				.thenReturn(List.of(responseDto));

		mvc.perform(get("/requests/all")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(responseDto.getId()), Integer.class))
				.andExpect(jsonPath("$[0].description", is(responseDto.getDescription())))
				.andExpect(jsonPath("$[0].created").value((responseDto.getCreated()).format(dateFormatter)))
				.andExpect(jsonPath("$[0].items[0].id", is(responseDto.getItems().get(0).getId())))
				.andExpect(jsonPath("$[0].items[0].name", is(responseDto.getItems().get(0).getName())))
				.andExpect(jsonPath("$[0].items[0].description", is(responseDto.getItems().get(0).getDescription())))
				.andExpect(jsonPath("$[0].items[0].available", is(responseDto.getItems().get(0).isAvailable())))
				.andExpect(jsonPath("$[0].items[0].requestId", is(responseDto.getItems().get(0).getRequestId())));
	}

	@Test
	void findOwnAll() throws Exception {
		when(requestServiceWeb.getAllOwn(anyInt(),anyInt(), anyInt()))
				.thenReturn(List.of(responseDto));

		mvc.perform(get("/requests")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(responseDto.getId()), Integer.class))
				.andExpect(jsonPath("$[0].description", is(responseDto.getDescription())))
				.andExpect(jsonPath("$[0].created").value((responseDto.getCreated()).format(dateFormatter)))
				.andExpect(jsonPath("$[0].items[0].id", is(responseDto.getItems().get(0).getId())))
				.andExpect(jsonPath("$[0].items[0].name", is(responseDto.getItems().get(0).getName())))
				.andExpect(jsonPath("$[0].items[0].description", is(responseDto.getItems().get(0).getDescription())))
				.andExpect(jsonPath("$[0].items[0].available", is(responseDto.getItems().get(0).isAvailable())))
				.andExpect(jsonPath("$[0].items[0].requestId", is(responseDto.getItems().get(0).getRequestId())));
	}
}