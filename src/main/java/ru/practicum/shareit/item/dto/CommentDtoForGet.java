package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDtoForGet {
    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
