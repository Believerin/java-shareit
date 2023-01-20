package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserJunitTest {

	@InjectMocks
	UserServiceImpl userService;
	@Mock
	UserRepository mockUserRepository;

	User user1 = new User(1, "Иван", "ivan@test.ru");
	UserDto userDto = new UserDto(1, "Иван", "ivan@test.ru");


	@Test
	void testAdd() {
		Mockito
				.when(mockUserRepository.save(UserMapper.toUser(userDto)))
				.thenReturn(user1);
		Assertions.assertEquals(userDto, userService.add(userDto));
	}

	@Test
	void testUpdate() {
		Mockito
				.when(mockUserRepository.findById(1))
				.thenReturn(Optional.of(user1));
		Assertions.assertEquals(userDto, userService.update(1, userDto));
	}

	@Test
	void testGet() {
		Mockito
				.when(mockUserRepository.findById(1))
				.thenReturn(Optional.of(user1));
		Assertions.assertEquals(userDto, userService.get(1));
	}
}