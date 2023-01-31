package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest(
		properties = "db.name=test",
		webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserIntegrationAndWebTest {

	private final EntityManager em;
	private final UserService userService;
	@Mock
	private final UserService userServiceWeb;
	private MockMvc mvc;
	private final ObjectMapper mapper = new ObjectMapper();
	@InjectMocks
	private UserController userController;

	UserDto userDto = new UserDto(null, "Иван", "ivan@test.ru");
	UserDto userDtoCreated1 = new UserDto(1, "Иван", "ivan@test.ru");
	UserDto userDtoCreated2 = new UserDto(2, "Андрей", "andrey@test.ru");

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
				.standaloneSetup(userController)
				.build();
	}

	@Test
	void testFindAllOwn() {
		userService.add(userDto);

		TypedQuery<User> query = em.createQuery("Select u from User u where u.name = :name", User.class);
		User user = query.setParameter("name", "Иван").getSingleResult();

		assertThat(user.getId(), notNullValue());
		assertThat(user.getName(), equalTo(userDto.getName()));
		assertThat(user.getEmail(), equalTo(userDto.getEmail()));
	}

	@Test
	void add() throws Exception {
		when(userServiceWeb.add(any()))
				.thenReturn(userDtoCreated1);

		mvc.perform(post("/users")
						.content(mapper.writeValueAsString(userDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1), Integer.class))
				.andExpect(jsonPath("$.name", is(userDto.getName())))
				.andExpect(jsonPath("$.email", is(userDto.getEmail())));
	}

	@Test
	void update() throws Exception {
		when(userServiceWeb.update(anyInt(), any()))
				.thenReturn(userDtoCreated1);

		mvc.perform(patch("/users/1")
						.content(mapper.writeValueAsString(userDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1), Integer.class))
				.andExpect(jsonPath("$.name", is(userDto.getName())))
				.andExpect(jsonPath("$.email", is(userDto.getEmail())));
	}

	@Test
	void getUser() throws Exception {
		when(userServiceWeb.get(anyInt()))
				.thenReturn(userDtoCreated1);

		mvc.perform(get("/users/1")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1), Integer.class))
				.andExpect(jsonPath("$.name", is(userDto.getName())))
				.andExpect(jsonPath("$.email", is(userDto.getEmail())));
	}

	@Test
	void deleteUser() throws Exception {
		mvc.perform(delete("/users/1")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void findAll() throws Exception {
		when(userServiceWeb.findAll())
				.thenReturn(List.of(userDtoCreated1, userDtoCreated2));

		ResultActions resultActions = mvc.perform(get("/users/")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		List.of(userDtoCreated1, userDtoCreated2).stream().peek(userDto -> {
					try {
						resultActions
								.andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
								.andExpect(jsonPath("$.name", is(userDto.getName())))
								.andExpect(jsonPath("$.email", is(userDto.getEmail())));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
		);
	}
}