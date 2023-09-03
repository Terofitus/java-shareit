package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(int userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllItemRequestsByOwnerId(int userId);

    List<ItemRequestDto> getAllItemRequests(int userId, int from, int size);

    ItemRequestDto getItemRequest(int userId, int requestId);
}
