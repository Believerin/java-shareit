package ru.practicum.shareit.request.service;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    private Function<Integer, List<Item>> findItemsByRequestId;
    private Function<Integer, List<Item>> findItemsByRequesterId;
    private Function<Integer, List<Item>> findItemsWithRequests;
    Sort sortByTime = Sort.by(Sort.Direction.ASC, "created");

    public RequestServiceImpl(RequestRepository requestRepository, ItemRepository itemRepository,
                              UserRepository userRepository, UserService userService) {
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.findItemsByRequestId = requestId -> itemRepository.findAllByRequestId(requestId);
        this.findItemsByRequesterId = requesterId -> itemRepository.findAllByRequester(requesterId);
        this.findItemsWithRequests = requesterId -> itemRepository.findAllWithRequests(requesterId);
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

        List<ResponseDto.ItemOffer> itemsOffers = getItemsOffers(findItemsByRequestId, requestId);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchBodyException("Требуемый запрос"));
        return RequestMapper.toResponseDto(request, itemsOffers);
    }


    @Override
    public Collection<ResponseDto> getAllOwn(int requesterId, int from, int size) {
        userService.get(requesterId);
        Pageable page = PageRequest.of(from, size, sortByTime);

        List<ResponseDto.ItemOffer> itemsOffers = getItemsOffers(findItemsByRequesterId, requesterId);
        Page<Request> r = requestRepository.findByRequesterId(requesterId, page);

        return requestRepository.findByRequesterId(requesterId, page).stream()
                .map(request -> {
                    List<ResponseDto.ItemOffer> filteredItemsOffers = itemsOffers.stream()
                            .filter(itemOffer -> itemOffer.getRequestId() == request.getId())
                            .collect(Collectors.toList());
                    ResponseDto t = RequestMapper.toResponseDto(request, filteredItemsOffers);
                    return RequestMapper.toResponseDto(request, filteredItemsOffers);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ResponseDto> getAll(int userId, int from, int size) {
        List<ResponseDto.ItemOffer> itemsOffers = getItemsOffers(findItemsWithRequests, userId);

        Pageable page = PageRequest.of(from, size, sortByTime);
        return requestRepository.findByRequesterIdNot(userId, page).stream()
                .map(request -> {
                    List<ResponseDto.ItemOffer> filteredItemsOffers = itemsOffers.stream()
                            .filter(itemOffer -> itemOffer.getRequestId() == request.getId())
                            .collect(Collectors.toList());
                    return RequestMapper.toResponseDto(request, filteredItemsOffers);
                })
                .collect(Collectors.toList());
    }

    //--------------------------------------Служебный метод-------------------------------------------------
    private List<ResponseDto.ItemOffer> getItemsOffers(Function<Integer, List<Item>> findByScenario, Integer id) {
        return findByScenario.apply(id).stream()
                .map(RequestMapper::toItemOffer)
                .collect(Collectors.toList());
    }
}