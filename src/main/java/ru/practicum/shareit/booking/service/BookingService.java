package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoForCreateUpdate;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking getBookingByOwnerOrBookerId(int bookingId, int ownerId);

    List<Booking> getAllBookingsByUserIdAndState(int userId, String state);

    List<Booking> getAllBookingsByItemOwnerIdAndState(int userId, String state);

    Booking addBooking(BookingDtoForCreateUpdate bookingDtoForCreateUpdate, int userId);

    Booking updateBooking(Booking booking, boolean approved, int userId);

    void deleteBookingById(int bookingId, int ownerId);

    void deleteAllBookings(int ownerId);
}
