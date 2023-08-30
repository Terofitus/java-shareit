package ru.practicum.shareit.booking.util;

import ru.practicum.shareit.booking.model.BookingStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class BookingStatusConverter implements AttributeConverter<BookingStatus, String> {
    @Override
    public String convertToDatabaseColumn(BookingStatus bookingStatus) {
        if (bookingStatus == null) {
            return null;
        }
        return bookingStatus.name();
    }

    @Override
    public BookingStatus convertToEntityAttribute(String s) {
        if (s == null) return null;

        return Stream.of(BookingStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}

