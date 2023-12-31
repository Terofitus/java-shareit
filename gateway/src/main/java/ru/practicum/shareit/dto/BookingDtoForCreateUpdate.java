package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@StartEndTime(groups = MarkerForCreate.class)
public class BookingDtoForCreateUpdate {
    @Null(groups = MarkerForCreate.class)
    @NotNull(groups = MarkerForUpdate.class)
    private Integer id;
    @NotNull(groups = MarkerForCreate.class)
    private int itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
