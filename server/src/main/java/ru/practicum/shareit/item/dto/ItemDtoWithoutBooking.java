package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"available", "requestId", "comments"})
public class ItemDtoWithoutBooking implements ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDtoForGet> comments;
    private Integer requestId;
}
