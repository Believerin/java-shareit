package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemJunitTest {

	@InjectMocks
	ItemServiceImpl itemService;

	LocalDateTime localDateTime = LocalDateTime.now();
	User user1 = new User(1, "Иван", "ivan@test.ru");
	User user2 = new User(2, "Андрей", "andrey@test.ru");
	UserDto userDto1 = new UserDto(1, "Иван", "ivan@test.ru");
	Request request1 = new Request(1, "Нужна пила", user1, localDateTime.minusDays(10));
	Request request2 = new Request(2, "Нужен чайник", user2, localDateTime.minusDays(8));
	Item item1 = new Item(1, "Чайник", "Металлический", true, 1, request1);
	Item item2 = new Item(2, "Бензопила", "Poulan", true, 2, request2);
	Comment comment1 = new Comment(1, "text of comment 1", 2, user1, localDateTime.minusHours(8));
	Comment comment2 = new Comment(2, "text of comment 2", 1, user2, localDateTime.minusHours(3));
	Booking booking1 = new Booking(1, localDateTime.minusDays(4), localDateTime.minusDays(2), item1, user2, BookingStatus.APPROVED);
	Booking booking2 = new Booking(2, localDateTime.minusDays(6), localDateTime.minusDays(3), item2, user1, BookingStatus.APPROVED);

	int from = 0;
	int size = 2;
	Pageable pageable = PageRequest.of(from, size);
	Page<Item> page = new PageImpl<Item>(List.of(item1), pageable, size);

	@Mock
    CommentRepository mockCommentRepository;
	@Mock
	ItemRepository mockItemRepository;
	@Mock
	BookingRepository mockBookingRepository;
	@Mock
	RequestRepository mockRequestRepository;
	@Mock
	UserRepository mockUserRepository;
	@Mock
	UserService mockUserService;
	@Mock
    ItemOfferRepository mockItemOfferRepository;

	@Test
	void testAdd() {
		ItemDtoCreated itemDtoCreated = new ItemDtoCreated(1, "Чайник", "Металлический", true, 1);
		Mockito
				.when(mockRequestRepository.findById(1))
				.thenReturn(Optional.of(request1));
		Mockito
				.when(mockItemRepository.save(ItemMapper.toItem(1, itemDtoCreated, request1)))
				.thenReturn(item1);
		Mockito
				.when(mockUserService.get(1))
				.thenReturn(userDto1);

		Assertions.assertEquals(itemDtoCreated, itemService.add(1, itemDtoCreated));
	}

	@Test
	void testGet() {
		ItemDto itemDto = new ItemDto(1, "Чайник", "Металлический", true, 1, null, null,
				List.of(CommentMapper.toCommentDto(comment2)));
		Mockito
				.when(mockCommentRepository.findAllByItem(1))
				.thenReturn(List.of(comment2));
		Mockito
				.when(mockItemRepository.findById(1))
				.thenReturn(Optional.of(item1));
		Mockito
				.when(mockBookingRepository.findByItemIdAndStatusOrderByStartAsc(1, BookingStatus.APPROVED))
				.thenReturn(List.of(booking1));
		Assertions.assertEquals(itemDto, itemService.get(2, 1));
		itemDto.setLastBooking(ItemMapper.toBooking(booking1));
		Assertions.assertEquals(itemDto, itemService.get(1, 1));
	}

	@Test
	void testFindAllOwn() {
		Mockito
				.when(mockCommentRepository.getCommentByItemIn(List.of(1)))
				.thenReturn(List.of(comment2));
		Mockito
				.when(mockItemRepository.findByOwnerOrderByIdAsc(1, pageable))
				.thenReturn(page);
		Mockito
				.when(mockBookingRepository.findByItemIdInAndStatusOrderByStartAsc(List.of(1), BookingStatus.APPROVED))
				.thenReturn(List.of(booking1));

		ItemDto itemDtoTest = ItemMapper.toItemDto(item1, List.of(CommentMapper.toCommentDto(comment2)));
		itemDtoTest.setLastBooking(ItemMapper.toBooking(booking1));

		Assertions.assertEquals(List.of(itemDtoTest), itemService.findAllOwn(1, from, size));
	}

	@Test
	void testSearchByKeyWord() {
		ItemDto itemDto = new ItemDto(1, "Чайник", "Металлический", true, 1, null, null, null);
		Mockito
				.when(mockItemRepository.findAllByText("пила", pageable))
				.thenReturn(page);
		Assertions.assertEquals(List.of(itemDto), itemService.searchByKeyWord("Пила", 0, 2));
	}

	@Test
	void testAddComment() {
		CommentDto commentDto = new CommentDto(1, "text of comment 1", 2, "Иван", localDateTime.minusHours(8));
		Mockito
				.when(mockBookingRepository.getAllByBookerOrOwner(anyInt(), anyInt(),anyBoolean(), any(LocalDateTime.class)))
				.thenReturn(List.of(booking2));
		Mockito
				.when(mockItemRepository.findById(2))
				.thenReturn(Optional.of(item2));
		Mockito
				.when(mockCommentRepository.save(any()))
				.thenReturn(comment1);
		Mockito
				.when(mockUserRepository.findById(1))
				.thenReturn(Optional.of(user1));
		Assertions.assertEquals(commentDto, itemService.addComment(1, 2, commentDto));
	}
}