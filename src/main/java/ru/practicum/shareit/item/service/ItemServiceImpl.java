package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> findAllOwn(int userId) {
        List<Item> itemsWithoutComments = itemRepository.findByOwner(userId);
        List<Integer> itemIds = itemsWithoutComments.stream()
                .mapToInt(item -> item.getId())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        List<CommentDto> commentsOfItems = commentRepository.getCommentByItemIn(itemIds).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        List<ItemDto> itemsWithComments = itemsWithoutComments.stream()
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> {
                    List<CommentDto> comments = commentsOfItems.stream()
                    .filter(comment -> comment.getItem() == itemDto.getId())
                    .collect(Collectors.toList());
                    itemDto.setComments(comments);
                }).collect(Collectors.toList());
        return itemsWithComments.stream().peek(itemDto -> {
                    Map<String, BookingDto> o = getLastAndNextBookings(itemIds).get(itemDto.getId());
                    itemDto.setNextBooking(o.get("next"));
                    itemDto.setLastBooking(o.get("last"));
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto add(int userId, ItemDto itemDto) {
        try {
            userService.get(userId);
            Item item = itemRepository.save(ItemMapper.toItem(userId, itemDto));
            return ItemMapper.toItemDto(item);
        } catch (NoSuchBodyException e) {
            throw new NoSuchBodyException("Владелец данного предмета");
        }
    }

    @Transactional
    @Override
    public ItemDto update(int userId, int itemId, ItemDto itemDto) {
        Item actualItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchBodyException("Запрашиваемый предмет"));
         if (userId != actualItem.getOwner()) {
            throw new NoAccessException("попытка редактировать чужой предмет");
        }
        return ItemMapper.toItemDto(toItem(userId, actualItem, itemDto));
    }

    @Override
    public ItemDto get(int userId, int itemId) {
       Item item = itemRepository.findById(itemId)
               .orElseThrow(() -> new NoSuchBodyException("Запрашиваемый предмет"));
       ItemDto itemDto = ItemMapper.toItemDto(item);
       List<Comment> y = commentRepository.findAllByItem(itemId);
       List<CommentDto> comments = commentRepository.findAllByItem(itemId).stream()
                .map(CommentMapper::toCommentDto)
            //    .peek(commentDto -> commentDto.setAuthorName(commentDto.getAuthor().getName()))
                .collect(Collectors.toList());
       itemDto.setComments(comments);
       if (item.getOwner() == userId) {
            Map<String, BookingDto> o = getLastAndNextBooking(itemDto.getId());
            itemDto.setLastBooking(o.get("last"));
            itemDto.setNextBooking(o.get("next"));
       }
       return itemDto;
    }

    @Override
    public Collection<ItemDto> searchByKeyWord(String text) {
        return text.isBlank() ? new ArrayList<>() : itemRepository.findAllByText(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(int authorId, int itemId, CommentDto commentDto) {
        List<Integer> pastBookings = bookingService.getAllByBooker(authorId, "PAST").stream()
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
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartAsc(itemId);
        fillMapOfNearestBookingsWithValues(bookings, nearestBookings);
        return nearestBookings;
    }

    private Map<Integer, Map<String, BookingDto>> getLastAndNextBookings(List<Integer> itemIds) {
        Map<Integer, Map<String, BookingDto>> nearestBookingsByItemId = new HashMap<>();
        List<Booking> bookingsByItemIds = bookingRepository.findByItemIdInOrderByStartAsc(itemIds);
        itemIds.stream()
                .forEach(integer -> {
                    List<Booking> bookings = bookingsByItemIds.stream()
                            .filter(booking -> booking.getItem().getId() == integer)
                            .collect(Collectors.toList());
                    Map<String, BookingDto> nearestBookings = new HashMap<>();
                    fillMapOfNearestBookingsWithValues(bookings, nearestBookings);
                    nearestBookingsByItemId.put(integer, nearestBookings);
                });
        return nearestBookingsByItemId;
    }

    private void fillMapOfNearestBookingsWithValues(List<Booking> bookings, Map<String, BookingDto> nearestBookings) {
        Booking next = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
        Booking last = bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
        nearestBookings.put("next", next != null ? BookingMapper.toBookingDto(next) : null);
        nearestBookings.put("last", last != null ? BookingMapper.toBookingDto(last) : null);
    }

    public static Item toItem(int userId, Item updatingItem, ItemDto itemDto) {
        updatingItem.setId(itemDto.getId() != null ? itemDto.getId() : updatingItem.getId());
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            updatingItem.setName(itemDto.getName());
        } else if (itemDto.getName() == null) {
            updatingItem.setName(updatingItem.getName());
        } else {
            throw new ValidationException("имя пусто либо состоит из пробелов");
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            updatingItem.setDescription(itemDto.getDescription());
        } else if (itemDto.getDescription() == null) {
            updatingItem.setDescription(updatingItem.getDescription());
        } else {
            throw new ValidationException("имя пусто либо состоит из пробелов");
        }
        updatingItem.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : updatingItem.getAvailable());
        updatingItem.setOwner(userId);
        updatingItem.setRequest(updatingItem.getRequest());
        return updatingItem;
    }
}