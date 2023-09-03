package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDtoWithoutBooking;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        List<ItemDtoWithoutBooking> itemsDto = null;
        if (itemRequest.getItems() != null) {
            itemsDto = itemRequest.getItems().stream()
                    .map(item -> ItemMapper.toItemDtoWithoutBooking(item, null)).collect(Collectors.toList());
        }

        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemsDto
        );
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return new ItemRequest(
                itemRequestDto.getId() == null ? 0 : itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                user,
                itemRequestDto.getCreated() == null ? LocalDateTime.now() : itemRequestDto.getCreated(),
                null
        );
    }
}
