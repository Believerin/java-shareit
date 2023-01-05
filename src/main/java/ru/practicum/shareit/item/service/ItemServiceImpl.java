package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> findAllOwn(int userId) {
        return itemRepository.findByOwner(userId).stream()
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> {
                    Map<String, BookingDto> o = getLastAndNextBooking(itemDto.getId());
                    itemDto.setNextBooking(o.get("next"));
                    itemDto.setLastBooking(o.get("last"));
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(int userId, ItemDto itemDto) {
        try {
            userService.getUser(userId);
            Item item = itemRepository.save(ItemMapper.toItem(userId, itemDto));
            return ItemMapper.toItemDto(item);
        } catch (NoSuchBodyException e) {
            throw new NoSuchBodyException("Владелец данного предмета");
        }
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        Item modifyingItem;
        Optional<Item> o = itemRepository.findById(itemId);
        if (o.isPresent()) {
            modifyingItem = o.get();
            if (userId != modifyingItem.getOwner()) {
                throw new NoAccessException("попытка редактировать чужой предмет");
            }
        } else {
            throw new NoSuchBodyException("Запрашиваемый предмет");
        }
        Item item = ItemMapper.toItem(userId, modifyingItem, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(int userId, int itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            ItemDto itemDto = ItemMapper.toItemDto(item.get());
            List<CommentDto> comments = commentRepository.findAllByItem(itemId, CommentDto.class).stream()
                    .peek(commentDto -> commentDto.setAuthorName(commentDto.getAuthor().getName()))
                    .collect(Collectors.toList());
            itemDto.setComments(comments);
            if (item.get().getOwner() == userId) {
                Map<String, BookingDto> o = getLastAndNextBooking(itemDto.getId());
                itemDto.setLastBooking(o.get("last"));
                itemDto.setNextBooking(o.get("next"));
            }
            return itemDto;
        } else {
            throw new NoSuchBodyException("Запрашиваемый предмет");
        }
    }

    @Override
    public Collection<ItemDto> searchByKeyWord(String text) {
        return text.isBlank() ? new ArrayList<>() : itemRepository.findAllByText(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(int authorId, int itemId, CommentDto commentDto) {
        List<Integer> pastBookings = bookingService.getAllBookingsByBooker(authorId, "PAST").stream()
                .mapToInt(bookingDto -> bookingDto.getItem().getId())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (!pastBookings.contains(itemId)) {
            throw new ValidationException("предмет не был взят пользователем в аренду");
        }
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NoSuchBodyException("Запрашиваемый для комментария предмет");
        }
        Optional<User> user = userRepository.findById(authorId);
        if (user.isEmpty()) {
            throw new NoSuchBodyException("Владелец комментируемого предмета");
        }
        Comment comment = CommentMapper.toComment(user.get(), itemId, commentDto);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    //--------------------------------------Служебный метод-------------------------------------------------

    private Map<String, BookingDto> getLastAndNextBooking(int itemId) {
        Map<String, BookingDto> nearestBookings = new HashMap<>();
        List<Booking> o = bookingRepository.findByItemIdOrderByStartAsc(itemId);
        Booking next = o.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
        Booking last = o.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
        nearestBookings.put("next", next != null ? BookingMapper.toBookingDto(next) : null);
        nearestBookings.put("last", last != null ? BookingMapper.toBookingDto(last) : null);
        return nearestBookings;
    }
}