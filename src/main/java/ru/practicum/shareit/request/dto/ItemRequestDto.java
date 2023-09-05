package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.dto.ItemDtoWithoutBooking;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id", "items"})
public class ItemRequestDto {
    @Null
    private Integer id;
    @NotNull
    @NotBlank
    private String description;
    private LocalDateTime created;
    @Null
    private List<ItemDtoWithoutBooking> items;
}
