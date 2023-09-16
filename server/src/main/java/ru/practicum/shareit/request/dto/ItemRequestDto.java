package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.dto.ItemDtoWithoutBooking;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id", "items"})
public class ItemRequestDto {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoWithoutBooking> items;
}
