package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.AppealStatus;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.comment.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreated;
import ru.practicum.shareit.item.dto.ItemDtoToGet;
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
    public Collection<ItemDto> findAllOwn(int userId, int from, int size) {
        Pageable page = PageRequest.of(from, size);
        Page<Item> itemsWithoutComments = itemRepository.findByOwner(userId, page);

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
        Map<Integer, Map<String, Booking>> nearestBookingsByItemIds = getLastAndNextBookings(itemIds);
        return itemsWithComments.stream()
                .peek(itemDto -> {
                    Map<String, Booking> nearestBookingsByItemId = nearestBookingsByItemIds.get(itemDto.getId());
                    itemDto.setNextBooking(nearestBookingsByItemId.get("next") != null
                            ? ItemMapper.toBooking(nearestBookingsByItemId.get("next")) : null);
                    itemDto.setLastBooking(nearestBookingsByItemId.get("last") != null
                            ? ItemMapper.toBooking(nearestBookingsByItemId.get("last")) : null);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoCreated add(int userId, ItemDtoToGet itemDtoToGet) {
        try {
            userService.get(userId);
            Item item = itemRepository.save(ItemMapper.toItem(userId, itemDtoToGet));
            return ItemMapper.toItemDtoCreated(item);
        } catch (NoSuchBodyException e) {
            throw new NoSuchBodyException("Владелец данного предмета");
        }
    }

    @Transactional
    @Override
    public ItemDtoCreated update(int userId, int itemId, ItemDtoToGet itemDtoToGet) {
        Item actualItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchBodyException("Запрашиваемый предмет"));
         if (userId != actualItem.getOwner()) {
            throw new NoAccessException("попытка редактировать чужой предмет");
        }
        return ItemMapper.toItemDtoCreated(toItem(userId, actualItem, itemDtoToGet));
    }

    @Override
    public ItemDto get(int userId, int itemId) {
       Item item = itemRepository.findById(itemId)
               .orElseThrow(() -> new NoSuchBodyException("Запрашиваемый предмет"));
       ItemDto itemDto = ItemMapper.toItemDto(item);
       List<Comment> y = commentRepository.findAllByItem(itemId);
       List<CommentDto> comments = commentRepository.findAllByItem(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
       itemDto.setComments(comments);
       if (item.getOwner() == userId) {
            Map<String, Booking> nearestBookingsByItemId = getLastAndNextBooking(itemDto.getId());
            itemDto.setLastBooking(nearestBookingsByItemId.get("last") != null
                    ? ItemMapper.toBooking(nearestBookingsByItemId.get("last")) : null);
            itemDto.setNextBooking(nearestBookingsByItemId.get("next") != null
                    ? ItemMapper.toBooking(nearestBookingsByItemId.get("next")) : null);
       }
       return itemDto;
    }

    @Override
    public Collection<ItemDto> searchByKeyWord(String text, int from, int size) {
        Pageable page = PageRequest.of(from, size);
        return text.isBlank() ? new ArrayList<>() : itemRepository.findAllByText(text.toLowerCase(), page).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(int authorId, int itemId, CommentDto commentDto) {
        int status = AppealStatus.valueOf("PAST").getAppealId();
        List<Integer> pastBookings = bookingRepository.getAllByBookerOrOwner(authorId, status,  false).stream()
                .mapToInt(bookingDto -> bookingDto.getItem().getId())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (!pastBookings.contains(itemId)) {
            throw new ValidationException("предмет не был взят пользователем в аренду");
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchBodyException("Запрашиваемый для комментария предмет"));
        Optional<User> user = userRepository.findById(authorId);
        if (user.isEmpty()) {
            throw new NoSuchBodyException("Владелец комментируемого предмета");
        }
        Comment comment = CommentMapper.toComment(user.get(), itemId, commentDto);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    //--------------------------------------Служебные методы-------------------------------------------------

    private Map<String, Booking> getLastAndNextBooking(int itemId) {
        Map<String, Booking> nearestBookings = new HashMap<>();
        List<Booking> bookings = bookingRepository.findByItemIdAndStatusOrderByStartAsc(itemId, BookingStatus.APPROVED);
        fillMapOfNearestBookingsWithValues(bookings, nearestBookings);
        return nearestBookings;
    }

    private Map<Integer, Map<String, Booking>> getLastAndNextBookings(List<Integer> itemIds) {
        Map<Integer, Map<String, Booking>> nearestBookingsByItemId = new HashMap<>();
        List<Booking> bookingsByItemIds = bookingRepository.findByItemIdInAndStatusOrderByStartAsc(itemIds, BookingStatus.APPROVED);
        itemIds.stream()
                .forEach(integer -> {
                    List<Booking> bookings = bookingsByItemIds.stream()
                            .filter(booking -> booking.getItem().getId() == integer)
                            .collect(Collectors.toList());
                    Map<String, Booking> nearestBookings = new HashMap<>();
                    fillMapOfNearestBookingsWithValues(bookings, nearestBookings);
                    nearestBookingsByItemId.put(integer, nearestBookings);
                });
        return nearestBookingsByItemId;
    }

    private void fillMapOfNearestBookingsWithValues(List<Booking> bookings, Map<String, Booking> nearestBookings) {
        Booking next = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
        Booking last = bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                        || booking.getStart().equals(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
        nearestBookings.put("next", next != null ? next : null);
        nearestBookings.put("last", last != null ? last : null);
    }

    public static Item toItem(int userId, Item updatingItem, ItemDtoToGet itemDtoToGet) {
        updatingItem.setId(itemDtoToGet.getId() != null ? itemDtoToGet.getId() : updatingItem.getId());
        if (itemDtoToGet.getName() != null && !itemDtoToGet.getName().isBlank()) {
            updatingItem.setName(itemDtoToGet.getName());
        } else if (itemDtoToGet.getName() == null) {
            updatingItem.setName(updatingItem.getName());
        } else {
            throw new ValidationException("имя пусто либо состоит из пробелов");
        }
        if (itemDtoToGet.getDescription() != null && !itemDtoToGet.getDescription().isBlank()) {
            updatingItem.setDescription(itemDtoToGet.getDescription());
        } else if (itemDtoToGet.getDescription() == null) {
            updatingItem.setDescription(updatingItem.getDescription());
        } else {
            throw new ValidationException("имя пусто либо состоит из пробелов");
        }
        updatingItem.setAvailable(itemDtoToGet.getAvailable() != null ? itemDtoToGet.getAvailable() : updatingItem.getAvailable());
        updatingItem.setOwner(userId);
        updatingItem.setRequest(updatingItem.getRequest());
        return updatingItem;
    }
}