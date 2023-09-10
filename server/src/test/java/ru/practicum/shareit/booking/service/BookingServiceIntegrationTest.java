package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.dto.BookingDtoForCreateUpdate;
import ru.practicum.dto.BookingState;
import ru.practicum.dto.BookingStatus;
import ru.practicum.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ItemMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EasyRandom generator = new EasyRandom();

    @BeforeEach
    void setUp() {
        User user1 = generator.nextObject(User.class);
        user1.setId(1);
        User user2 = generator.nextObject(User.class);
        user2.setId(2);
        User user3 = generator.nextObject(User.class);
        user3.setId(3);

        Item item1 = generator.nextObject(Item.class);
        item1.setOwner(user1);
        item1.setRequest(null);
        Item item2 = generator.nextObject(Item.class);
        item2.setOwner(user2);
        item2.setRequest(null);

        BookingDtoForCreateUpdate booking1 = new BookingDtoForCreateUpdate(null, 2,
                LocalDateTime.of(2033, 1, 1, 5, 5, 5),
                LocalDateTime.of(2034, 1, 1, 5, 5, 5));
        BookingDtoForCreateUpdate booking2 = new BookingDtoForCreateUpdate(null, 1,
                LocalDateTime.of(2035, 1, 1, 5, 5, 5),
                LocalDateTime.of(2040, 1, 1, 5, 5, 5));

        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
        itemService.addItem(1, ItemMapper.toItemDtoWithoutBooking(item1, null));
        itemService.addItem(2, ItemMapper.toItemDtoWithoutBooking(item2, null));
        bookingService.addBooking(booking1, 1);
        bookingService.addBooking(booking2, 2);
    }

    @Test
    void test_getBookingByOwnerOrBookerId_whenUserIdNotOwnerOrBookerId_shouldThrowException() {
        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingByOwnerOrBookerId(1, 3));
        assertEquals("Бронирование с id=1 не найдено.", exception.getMessage());
    }

    @Test
    void test_getAllBookingsByUserIdAndState_whenStateIsFuture_shouldReturnListOfBookings() {
        BookingDtoForCreateUpdate bookingDtoForCreate = new BookingDtoForCreateUpdate(null, 2,
                LocalDateTime.of(2043, 1, 1, 5, 5, 5),
                LocalDateTime.of(2044, 1, 1, 5, 5, 5));
        bookingService.addBooking(bookingDtoForCreate, 1);

        List<Booking> bookings = bookingService.getAllBookingsByUserIdAndState(1, BookingState.FUTURE.name(),
                0, 20);

        assertArrayEquals(new int[]{bookings.get(0).getId(), bookings.get(1).getId()}, new int[]{3, 1});
    }

    @Test
    void test_getAllBookingsByItemOwnerIdAndState_whenStateIsAll_shouldReturnListWithOneItem() {
        List<Booking> bookings = bookingService.getAllBookingsByItemOwnerIdAndState(1, BookingState.ALL.name(),
                0, 20);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), 2);
        assertEquals(bookings.get(0).getBooker().getId(), 2);
    }

    @Test
    void test_updateBooking_whenApprovedIsFalse_shouldReturnBookingWithStatusRejected() {
        Booking booking = bookingService.getBookingByOwnerOrBookerId(1, 1);

        Booking bookingAfterUpdate = bookingService.updateBooking(booking, false, 2);

        assertEquals(bookingAfterUpdate.getStatus(), BookingStatus.REJECTED);
        assertEquals(bookingAfterUpdate.getId(), booking.getId());
    }
}