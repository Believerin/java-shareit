package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOffer;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@JsonTest
public class JsonDtoTest {

    @Autowired
    private JacksonTester<UserDto> jsonUser;
    @Autowired
    private JacksonTester<ItemDto> jsonItem;
    @Autowired
    private JacksonTester<BookingDto> jsonBooking;
    @Autowired
    private JacksonTester<CommentDto> jsonComment;
    @Autowired
    private JacksonTester<ResponseDto> jsonResponse;

    LocalDateTime localDateTime = LocalDateTime.now();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    UserDto userDto = new UserDto(1, "Иван", "ivan@test.ru");
    ItemDto itemDto = new ItemDto(1, "Чайник", "Металлический", true,1,
            ItemDto.Booking.builder()
                    .id(1)
                    .bookerId(1)
                    .build(),
            ItemDto.Booking.builder()
                    .id(2)
                    .bookerId(1)
                    .build(),
            List.of(new CommentDto(1, "Предмет исправен", 1, "Андрей", localDateTime)));
    BookingDto bookingDto = new BookingDto(2, localDateTime.minusDays(6), localDateTime.minusDays(3),
            BookingDto.Item.builder().id(2).name("Бензопила").build(),
            BookingDto.Booker.builder().id(1).name("Иван").build(), BookingStatus.APPROVED);
    CommentDto commentDto = new CommentDto(1, "Предмет исправен", 2, "Иван", localDateTime.minusHours(8));
    ResponseDto responseDto = new ResponseDto(1, "Нужна пила", localDateTime,
            List.of(ItemOffer.builder().id(2).name("Бензопила").description("Poulan").available(true).requestId(1).build()));

    @Test
    void testItemDto() throws Exception {
        JsonContent<ItemDto> result = jsonItem.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo((int)itemDto.getLastBooking().getId());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo((int)itemDto.getLastBooking().getBookerId());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo((int)itemDto.getNextBooking().getId());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo((int)itemDto.getNextBooking().getBookerId());
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(itemDto.getComments().size());
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(itemDto.getComments().get(0).getId());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(itemDto.getComments().get(0).getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo(itemDto.getComments().get(0).getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo(itemDto.getComments().get(0).getCreated().format(dateFormatter));
    }

    @Test
    void testUserDto() throws Exception {
        JsonContent<UserDto> result = jsonUser.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }

    @Test
    void testBookingDto() throws Exception {
        JsonContent<BookingDto> result = jsonBooking.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDto.getStart().format(dateFormatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().format(dateFormatter));
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(bookingDto.getItem().getId());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(bookingDto.getItem().getName());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bookingDto.getBooker().getId());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(bookingDto.getBooker().getName());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
    }

    @Test
    void testCommentDto() throws Exception {
        JsonContent<CommentDto> result = jsonComment.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(result).extractingJsonPathNumberValue("$.item").isEqualTo(commentDto.getItem());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(commentDto.getCreated().format(dateFormatter));
    }

    @Test
    void testResponseDto() throws Exception {
        JsonContent<ResponseDto> result = jsonResponse.write(responseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(responseDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(responseDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(responseDto.getCreated().format(dateFormatter));
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(responseDto.getItems().get(0).getId());
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo(responseDto.getItems().get(0).getName());
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo(responseDto.getItems().get(0).getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(responseDto.getItems().get(0).isAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(responseDto.getItems().get(0).getRequestId());
    }
}
