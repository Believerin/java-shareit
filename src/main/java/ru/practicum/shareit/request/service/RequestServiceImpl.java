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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

//@Transactional
@Service
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;

    private Function<Integer, List<Item>> findItemsByRequestId;
    private Function<Integer, List<Item>> findItemsByRequesterId;
    private Function<Integer, List<Item>> findItemsWithRequests;

    public RequestServiceImpl(RequestRepository requestRepository, ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;

        this.findItemsByRequestId = id -> itemRepository.findAllByRequestId(id);
        this.findItemsByRequesterId = id -> itemRepository.findAllByRequester(id);
        this.findItemsWithRequests = id -> itemRepository.findAllWithRequests();
    }

/*    @Autowired
    private TransactionTemplate template;*/

    @Override
    public ResponseDtoCreated add(RequestDto requestDto, int userId) {
        Request request = requestRepository.save(RequestMapper.toRequest(requestDto, userId));
        return RequestMapper.toResponseDtoCreated(request);
    }

    @Override
    public ResponseDto get(int userId, int requestId) {
        List<ResponseDto.ItemOffer> itemsOffers = getItemsOffers(findItemsByRequestId, requestId);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchBodyException("Требуемый запрос"));
        return RequestMapper.toResponseDto(request, itemsOffers);
    }


    @Override
    public Collection<ResponseDto> getAllOwn(int requesterId, int from, int size) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(from, size, sortById);

        List<ResponseDto.ItemOffer> itemsOffers = getItemsOffers(findItemsByRequesterId, requesterId);

        return requestRepository.findByRequester(requesterId, page).stream()
                .map(request -> {
                    List<ResponseDto.ItemOffer> filteredItemsOffers = itemsOffers.stream()
                            .filter(itemOffer -> itemOffer.getRequestId() == request.getId())
                            .collect(Collectors.toList());
                    return RequestMapper.toResponseDto(request, filteredItemsOffers);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ResponseDto> getAll(int from, int size) {
        List<ResponseDto.ItemOffer> itemsOffers = getItemsOffers(findItemsWithRequests, null);

        Sort sortById = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(from, size, sortById);
        return requestRepository.findAll(page).stream()
                .map(request -> {
                    List<ResponseDto.ItemOffer> filteredItemsOffers = itemsOffers.stream()
                            .filter(itemOffer -> itemOffer.getRequestId() == request.getId())
                            .collect(Collectors.toList());
                    return RequestMapper.toResponseDto(request, filteredItemsOffers);
                })
                .collect(Collectors.toList());
    }

    //--------------------------------------Служебный метод-------------------------------------------------


    private List<ResponseDto.ItemOffer> getItemsOffers (Function<Integer, List<Item>> findByScenario, Integer id) {
        return findByScenario.apply(id).stream()
                .map(RequestMapper::toItemOffer)
                .collect(Collectors.toList());
    }

    public static User toUser(int userId, User updatingUser, UserDto userDto) {
        updatingUser.setId(userId);
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            updatingUser.setName(userDto.getName());
        } else if (userDto.getName() == null) {
            updatingUser.setName(updatingUser.getName());
        } else {
            throw new ValidationException("имя пусто либо состоит из пробелов");
        }
        updatingUser.setEmail(userDto.getEmail() != null ? userDto.getEmail() : updatingUser.getEmail());
        return updatingUser;
    }
}