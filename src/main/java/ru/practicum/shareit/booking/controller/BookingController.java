package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoForCreateUpdate;
import ru.practicum.shareit.booking.dto.BookingDtoForGet;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.booking.util.MarkerForCreate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDtoForGet getBookingById(@PathVariable Integer bookingId,
                                           @RequestHeader("X-Sharer-User-Id") Integer userId) {
        Booking booking = bookingService.getBookingByOwnerOrBookerId(bookingId, userId);
        return BookingMapper.toBookingDtoForGet(booking);
    }

    @GetMapping
    public List<BookingDtoForGet> getAllBookingsByUserIdAndState(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                                 @RequestParam(required = false, defaultValue = "ALL")
                                                                 String state) {
        List<Booking> bookings = bookingService.getAllBookingsByUserIdAndState(userId, state);
        return bookings.stream().map(BookingMapper::toBookingDtoForGet).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDtoForGet> getAllBookingsByOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                                  @RequestParam(required = false, defaultValue = "ALL")
                                                                  String state) {
        List<Booking> bookings = bookingService.getAllBookingsByItemOwnerIdAndState(userId, state);
        return bookings.stream().map(BookingMapper::toBookingDtoForGet).collect(Collectors.toList());
    }

    @PostMapping
    public BookingDtoForGet addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestBody @Validated(MarkerForCreate.class)
                                       BookingDtoForCreateUpdate bookingDtoForCreateUpdate) {
        return BookingMapper.toBookingDtoForGet(bookingService.addBooking(bookingDtoForCreateUpdate, userId));
    }

    @Transactional
    @PatchMapping("/{bookingId}")
    public BookingDtoForGet updateBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @PathVariable Integer bookingId,
                                          @RequestParam Boolean approved) {
        Booking booking = bookingService.getBookingByOwnerOrBookerId(bookingId, userId);
        return BookingMapper.toBookingDtoForGet(bookingService.updateBooking(booking, approved, userId));
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBookingById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                  @PathVariable Integer bookingId) {
        bookingService.deleteBookingById(bookingId, userId);
    }

    @DeleteMapping
    public void deleteAllBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        bookingService.deleteAllBookings(ownerId);
    }
}
