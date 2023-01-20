package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class RequestMapper {

    public static ResponseDtoCreated toResponseDtoCreated(Request request) {
        return ResponseDtoCreated.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public static Request toRequest(RequestDto requestDto, User requester) {
        return Request.builder()
                .description(requestDto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    public static ResponseDto toResponseDto(Request request, List<ResponseDto.ItemOffer> items) {
        return ResponseDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }

    public static ResponseDto.ItemOffer toItemOffer(Item item) {
        return ResponseDto.ItemOffer.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }
}