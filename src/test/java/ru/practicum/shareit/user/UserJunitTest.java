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
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserJunitTest {

	@InjectMocks
	UserServiceImpl userService;
	@Mock
    UserRepository mockUserRepository;
	@Mock
	private UserMapper mockUserMapper;

	User user1 = new User(1, "Иван", "ivan@test.ru");
	User user = new User(null, "Иван", "ivan@test.ru");
	UserDto userDto = new UserDto(1, "Иван", "ivan@test.ru");


	@Test
	void testAdd() {
		Mockito
				.when(mockUserMapper.toUserDto(user1))
				.thenReturn(userDto);
		Mockito
				.when(mockUserMapper.toUser(userDto))
				.thenReturn(user1);
		Mockito
				.when(mockUserRepository.save(user))
				.thenReturn(user1);
		Assertions.assertEquals(userDto, userService.add(userDto));
	}

	@Test
	void testUpdate() {
		Mockito
				.when(mockUserMapper.toUserDto(user1))
				.thenReturn(userDto);
		Mockito
				.when(mockUserRepository.findById(1))
				.thenReturn(Optional.of(user1));
		Assertions.assertEquals(userDto, userService.update(1, userDto));
	}

	@Test
	void testGet() {
		Mockito
				.when(mockUserMapper.toUserDto(user1))
				.thenReturn(userDto);
		Mockito
				.when(mockUserRepository.findById(1))
				.thenReturn(Optional.of(user1));
		Assertions.assertEquals(userDto, userService.get(1));
	}
}