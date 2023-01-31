package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.item.ItemOfferRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemOffer;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class RequestJunitTest {

	LocalDateTime localDateTime = LocalDateTime.now();
	@InjectMocks
	RequestServiceImpl requestService;
	@Mock
	ItemRepository mockItemRepository;
	@Mock
	ItemOfferRepository mockItemOfferRepository;
	@Mock
	RequestRepository mockRequestRepository;
	@Mock
	UserRepository mockUserRepository;
	@Mock
	UserService mockUserService;

	User user1 = new User(1, "Иван", "ivan@test.ru");
	User user2 = new User(2, "Андрей", "andrey@test.ru");
	Request request1 = new Request(1, "Нужна пила", user1, localDateTime.minusDays(10));
	Request request2 = new Request(2, "Нужен чайник", user2, localDateTime.minusDays(8));
	Item item2 = new Item(2, "Бензопила", "Poulan", true, 2, request1);
	ItemOffer itemOffer = ItemOffer.builder().id(2).name("Бензопила").description("Poulan").available(true).requestId(1).build();


	int from = 0;
	int size = 2;
	Pageable pageableSorted = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "created"));
	Page page = new PageImpl<Request>(List.of(request1), pageableSorted, size);

	@Test
	void testAdd() {
		RequestDto requestDto = new RequestDto("Нужна пила");
		ResponseDtoCreated responseDtoCreated = new ResponseDtoCreated(1, "Нужна пила", request1.getCreated());
		Mockito
				.when(mockUserRepository.findById(1))
				.thenReturn(Optional.of(user1));
		Mockito
				.when(mockRequestRepository.save(any()))
				.thenReturn(request1);
		Assertions.assertEquals(responseDtoCreated, requestService.add(requestDto, 1));
	}

	@Test
	void testGet() {
		ResponseDto responseDto = new ResponseDto(1, "Нужна пила", request1.getCreated(),
				List.of(ItemOffer.builder().id(2).name("Бензопила").description("Poulan").available(true).requestId(1).build()));
		Mockito
				.when(mockUserRepository.findById(1))
				.thenReturn(Optional.of(user1));
		Mockito
				.when(mockItemOfferRepository.findAllByRequestId(1))
				.thenReturn(List.of(itemOffer));
		Mockito
				.when(mockRequestRepository.findById(any()))
				.thenReturn(Optional.of(request1));
		Assertions.assertEquals(responseDto, requestService.get(1, 1));
	}

	@Test
	void testFindAllOwn() {
		UserDto userDto = new UserDto(1, "Иван", "ivan@test.ru");
		ResponseDto responseDto = new ResponseDto(1, "Нужна пила", request1.getCreated(),
				List.of(itemOffer));
		Mockito
				.when(mockItemOfferRepository.findAllByRequester(1))
				.thenReturn(List.of(itemOffer));
		Mockito
				.when(mockRequestRepository.findByRequesterId(1, pageableSorted))
				.thenReturn(page);
		Mockito
				.when(mockUserService.get(1))
				.thenReturn(userDto);
		Assertions.assertEquals(List.of(responseDto), requestService.getAllOwn(1, from, size));
	}

	@Test
	void testGetAll() {
		ResponseDto responseDto = new ResponseDto(1, "Нужна пила", request1.getCreated(),
				List.of(ItemOffer.builder().id(2).name("Бензопила").description("Poulan").available(true).requestId(1).build()));
		Mockito
				.when(mockItemOfferRepository.findAllWithRequests(2))
				.thenReturn(List.of(itemOffer));
		Mockito
				.when(mockRequestRepository.findByRequesterIdNot(2, pageableSorted))
				.thenReturn(page);
		Assertions.assertEquals(List.of(responseDto), requestService.getAll(2, from, size));

	}
}