package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.MarkerForCreate;
import ru.practicum.shareit.util.MarkerForUpdate;
import ru.practicum.shareit.util.StartEndTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Data
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
