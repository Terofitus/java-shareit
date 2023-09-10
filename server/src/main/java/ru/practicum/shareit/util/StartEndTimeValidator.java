package ru.practicum.shareit.util;

import ru.practicum.dto.BookingDtoForCreateUpdate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartEndTimeValidator implements ConstraintValidator<StartEndTime, BookingDtoForCreateUpdate> {
    @Override
    public boolean isValid(BookingDtoForCreateUpdate bookingDtoForCreateUpdate,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (bookingDtoForCreateUpdate.getStart() == null || bookingDtoForCreateUpdate.getEnd() == null) return false;
        return (bookingDtoForCreateUpdate.getStart().isEqual(LocalDateTime.now()) ||
                bookingDtoForCreateUpdate.getStart().isAfter(LocalDateTime.now())) &&
                (bookingDtoForCreateUpdate.getEnd().isAfter(LocalDateTime.now())) &&
                (bookingDtoForCreateUpdate.getStart().isBefore(bookingDtoForCreateUpdate.getEnd()));
    }
}
