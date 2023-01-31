package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NoSuchBodyException;
import ru.practicum.shareit.item.ItemOfferRepository;
import ru.practicum.shareit.item.dto.ItemOffer;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.ResponseDtoCreated;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemOfferRepository itemOfferRepository;


    Sort sortByTime = Sort.by(Sort.Direction.ASC, "created");

    public RequestServiceImpl(RequestRepository requestRepository, ItemRepository itemRepository,
                              UserRepository userRepository, UserService userService, ItemOfferRepository itemOfferRepository) {
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.itemOfferRepository = itemOfferRepository;
    }

    @Override
    public ResponseDtoCreated add(RequestDto requestDto, int requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NoSuchBodyException("Владелец предмета для бронирования"));
        Request request = requestRepository.save(RequestMapper.toRequest(requestDto, requester));
        return RequestMapper.toResponseDtoCreated(request);
    }

    @Override
    public ResponseDto get(int userId, int requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchBodyException("Пользователь, пытающийся просмотреть запрос,"));

        List<ItemOffer> itemsOffers =  itemOfferRepository.findAllByRequestId(requestId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchBodyException("Требуемый запрос"));
        return RequestMapper.toResponseDto(request, itemsOffers);
    }


    @Override
    public Collection<ResponseDto> getAllOwn(int requesterId, int from, int size) {
        userService.get(requesterId);
        Pageable page = PageRequest.of(from, size, sortByTime);

        List<ItemOffer> itemsOffers = itemOfferRepository.findAllByRequester(requesterId);

        return requestRepository.findByRequesterId(requesterId, page).stream()
                .map(request -> {
                    List<ItemOffer> filteredItemsOffers = itemsOffers.stream()
                            .filter(itemOffer -> itemOffer.getRequestId() == request.getId())
                            .collect(Collectors.toList());
                    return RequestMapper.toResponseDto(request, filteredItemsOffers);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ResponseDto> getAll(int userId, int from, int size) {
        List<ItemOffer> itemsOffers = itemOfferRepository.findAllWithRequests(userId);

        Pageable page = PageRequest.of(from, size, sortByTime);
        return requestRepository.findByRequesterIdNot(userId, page).stream()
                .map(request -> {
                    List<ItemOffer> filteredItemsOffers = itemsOffers.stream()
                            .filter(itemOffer -> itemOffer.getRequestId() == request.getId())
                            .collect(Collectors.toList());
                    return RequestMapper.toResponseDto(request, filteredItemsOffers);
                })
                .collect(Collectors.toList());
    }
}