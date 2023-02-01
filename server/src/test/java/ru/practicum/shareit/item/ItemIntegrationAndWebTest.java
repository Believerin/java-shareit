package ru.practicum.shareit.item;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
public class ItemIntegrationAndWebTest {

	private final JdbcTemplate jdbcTemplate;
	private final EntityManager em;
	private final ItemService itemService;
	private final UserService userService;
	@Mock
	private final ItemService itemServiceWeb;
	private MockMvc mvc;
	private final ObjectMapper mapper = new ObjectMapper();
	@InjectMocks
	private ItemController itemController;

	Item item1 = new Item(1, "Чайник", "Металлический", true, 1, null);

	ItemDto itemDto1 = new ItemDto(1, "Чайник", "Металлический", true, null, null, null, null);
	ItemDto itemDto2 = new ItemDto(2, "Бензопила", "Poulan", true, null, null, null, null);
	ItemDtoCreated itemDtoCreatedResponse = new ItemDtoCreated(1, "Чайник", "Металлический", true, null);
	ItemDtoCreated itemDtoCreated = new ItemDtoCreated(null, "Чайник", "Металлический", true, null);
	UserDto userDto = new UserDto(null, "Иван", "ivan@test.ru");
	CommentDto commentDto1 = new CommentDto(1, "text of comment 1", 2, "Иван", LocalDateTime.now());

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
				.standaloneSetup(itemController)
				.build();
	}

	@Test
	void testFindAllOwn() {
		String sql = "DELETE FROM users CASCADE; " +
				"ALTER TABLE users ALTER COLUMN id RESTART WITH 1";
		jdbcTemplate.update(sql);
		ItemDtoCreated itemDtoCreated = new ItemDtoCreated(null, "Чайник", "Металлический", true, null);
		userService.add(userDto);
		itemService.add(1, itemDtoCreated);

		TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
		Item item = query.setParameter("name", "Чайник").getSingleResult();

		assertThat(item.getId(), notNullValue());
		assertThat(item.getName(), equalTo(item1.getName()));
		assertThat(item.getDescription(), equalTo(item1.getDescription()));
		assertThat(item.getAvailable(), equalTo(item1.getAvailable()));
		assertThat(item.getOwner(), equalTo(item1.getOwner()));
	}

	@Test
	void add() throws Exception {
		when(itemServiceWeb.add(anyInt(), any()))
				.thenReturn(itemDtoCreatedResponse);

		mvc.perform(post("/items")
						.content(mapper.writeValueAsString(itemDtoCreated))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(itemDtoCreatedResponse.getId()), Integer.class))
				.andExpect(jsonPath("$.name", is(itemDtoCreatedResponse.getName())))
				.andExpect(jsonPath("$.description", is(itemDtoCreatedResponse.getDescription())))
				.andExpect(jsonPath("$.available", is(itemDtoCreatedResponse.getAvailable())))
				.andExpect(jsonPath("$.requestId", is(itemDtoCreatedResponse.getRequestId())));
	}

	@Test
	void update() throws Exception {
		when(itemServiceWeb.update(anyInt(), anyInt(), any()))
				.thenReturn(itemDtoCreatedResponse);

		mvc.perform(patch("/items/1")
						.content(mapper.writeValueAsString(itemDtoCreated))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(itemDtoCreatedResponse.getId()), Integer.class))
				.andExpect(jsonPath("$.name", is(itemDtoCreatedResponse.getName())))
				.andExpect(jsonPath("$.description", is(itemDtoCreatedResponse.getDescription())))
				.andExpect(jsonPath("$.available", is(itemDtoCreatedResponse.getAvailable())))
				.andExpect(jsonPath("$.requestId", is(itemDtoCreatedResponse.getRequestId())));
	}

	@Test
	void getItem() throws Exception {
		when(itemServiceWeb.get(anyInt(), anyInt()))
				.thenReturn(itemDto1);

		mvc.perform(get("/items/1")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(itemDto1.getId()), Integer.class))
				.andExpect(jsonPath("$.name", is(itemDto1.getName())))
				.andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
				.andExpect(jsonPath("$.available", is(itemDto1.getAvailable())))
				.andExpect(jsonPath("$.request", is(itemDto1.getRequest())));
	}

	@Test
	void searchByKeyWord() throws Exception {
		when(itemServiceWeb.searchByKeyWord(anyString(), anyInt(), anyInt()))
				.thenReturn(List.of(itemDto1, itemDto2));

		ResultActions resultActions = mvc.perform(get("/items/1")
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		List.of(itemDto1, itemDto2).stream().peek(itemDto -> {
			try {
				resultActions
						.andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
						.andExpect(jsonPath("$.name", is(itemDto.getName())))
						.andExpect(jsonPath("$.description", is(itemDto.getDescription())))
						.andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
						.andExpect(jsonPath("$.request", is(itemDto.getRequest())));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Test
	void addComment() throws Exception {
		CommentDto commentDto = new CommentDto(null, "text of comment 2", null, null, null);
		when(itemServiceWeb.addComment(anyInt(), anyInt(), any()))
				.thenReturn(commentDto1);

		mvc.perform(post("/items/1/comment")
						.content(mapper.writeValueAsString(commentDto))
						.header("X-Sharer-User-Id", 1)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(commentDto1.getId()), Integer.class))
				.andExpect(jsonPath("$.text", is(commentDto1.getText())))
				.andExpect(jsonPath("$.item", is(commentDto1.getItem())))
				.andExpect(jsonPath("$.authorName", is(commentDto1.getAuthorName())))
				.andExpect(jsonPath("$.created", notNullValue()));
	}
}