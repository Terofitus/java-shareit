package ru.practicum.shareit.booking.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoForCreateUpdate;
import ru.practicum.shareit.booking.dto.BookingDtoForGet;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public BookingDtoForGet toBookingDtoForGet(Booking booking) {
        return new BookingDtoForGet(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new ItemShortDto(booking.getItem().getId(), booking.getItem().getName()),
                new UserShortDto(booking.getBooker().getId()),
                booking.getStatus()
        );
    }

    public Booking toBookingFromDtoCreate(BookingDtoForCreateUpdate bookingDto, User booker, Item item) {
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Предмет с id=" + item.getId() + " не доступен для бронирования.");
        }
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                BookingStatus.WAITING
        );
    }
}
