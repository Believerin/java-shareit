package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookingJunitTest {

	LocalDateTime localDateTime = LocalDateTime.now();
	@InjectMocks
	BookingServiceImpl bookingService;
	@Mock
	ItemRepository mockItemRepository;
	@Mock
    BookingRepository mockBookingRepository;
	@Mock
	UserRepository mockUserRepository;
	@Mock
	UserService mockUserService;

	User user1 = new User(1, "Иван", "ivan@test.ru");
	User user2 = new User(2, "Андрей", "andrey@test.ru");
	Request request2 = new Request(2, "Нужен чайник", user2, localDateTime.minusDays(8));
	Item item2 = new Item(2, "Бензопила", "Poulan", true, 2, request2);
	Booking booking2 = new Booking(2, localDateTime.minusDays(6), localDateTime.minusDays(3), item2, user1, BookingStatus.APPROVED);
	int from = 0;
	int size = 2;
	Pageable pageable = PageRequest.of(from, size);
	Page<Booking> page = new PageImpl<>(List.of(booking2), pageable, size);


	@Test
	void testAdd() {
		BookingCreatedDto bookingCreatedDto = new BookingCreatedDto(localDateTime.minusDays(6), localDateTime.minusDays(3), 2);
		BookingDto bookingDto = new BookingDto(2, localDateTime.minusDays(6), localDateTime.minusDays(3),
				BookingDto.Item.builder().id(2).name("Бензопила").build(),
				BookingDto.Booker.builder().id(1).name("Иван").build(), BookingStatus.APPROVED);
		Booking booking = BookingMapper.toBooking(bookingCreatedDto, item2, user1);
		booking.setStatus(BookingStatus.WAITING);
		Mockito
				.when(mockItemRepository.findById(2))
				.thenReturn(Optional.of(item2));
		Mockito
				.when(mockUserRepository.findById(1))
				.thenReturn(Optional.of(user1));
		Mockito
				.when(mockBookingRepository.save(booking))
				.thenReturn(booking2);
		Assertions.assertEquals(bookingDto, bookingService.add(bookingCreatedDto, 1));
	}

	@Test
	void testAcceptOrDeny() {
		BookingDto bookingDto = new BookingDto(2, localDateTime.minusDays(6), localDateTime.minusDays(3),
				BookingDto.Item.builder().id(2).name("Бензопила").build(),
				BookingDto.Booker.builder().id(1).name("Иван").build(), BookingStatus.APPROVED);
		Mockito
				.when(mockBookingRepository.findByOwner(1, 2))
				.thenReturn(Optional.of(booking2.toBuilder().status(BookingStatus.WAITING).build()));

		Mockito
				.when(mockBookingRepository.find(1, 2))
				.thenReturn(Optional.of(booking2));

		Assertions.assertEquals(bookingDto, bookingService.acceptOrDeny(2, true, 1));
	}

	@Test
	void testGetAllByBooker() {
		BookingDto bookingDto = new BookingDto(2, localDateTime.minusDays(6), localDateTime.minusDays(3),
				BookingDto.Item.builder().id(2).name("Бензопила").build(),
				BookingDto.Booker.builder().id(1).name("Иван").build(), BookingStatus.APPROVED);
		UserDto bookerDto = new UserDto(1, "Иван", "ivan@test.ru");
		Mockito
				.when(mockUserService.get(1))
				.thenReturn(bookerDto);
		Mockito
				.when(mockBookingRepository.getAllByBookerOrOwner(1, 3, false, pageable))
				.thenReturn(page);
		Assertions.assertEquals(List.of(bookingDto), bookingService.getAllByBooker(1, "PAST", from, size));
	}

	@Test
	void testGetAllByOwner() {
		BookingDto bookingDto = new BookingDto(2, localDateTime.minusDays(6), localDateTime.minusDays(3),
				BookingDto.Item.builder().id(2).name("Бензопила").build(),
				BookingDto.Booker.builder().id(1).name("Иван").build(), BookingStatus.APPROVED);
		UserDto ownerDto = new UserDto(1, "Иван", "ivan@test.ru");
		Mockito
				.when(mockUserService.get(2))
				.thenReturn(ownerDto);
		Mockito
				.when(mockBookingRepository.getAllByBookerOrOwner(2, 3, true, pageable))
				.thenReturn(page);
		Assertions.assertEquals(List.of(bookingDto), bookingService.getAllByOwner(2, "PAST", from, size));
	}
}