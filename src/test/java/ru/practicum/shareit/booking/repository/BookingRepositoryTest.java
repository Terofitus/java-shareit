package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.PageableCreator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        User user1 = new User(null, "A12D", "xcv_@mail.ru");
        User user2 = new User(null, "AasfD", "xcv12xbc@mail.ru");
        testEntityManager.persist(user1);
        user1.setId(1);
        testEntityManager.persist(user2);
        user2.setId(2);
        Item item1 = new Item(null, "zxcLL", "description", true, user1, null);
        Item item2 = new Item(null, "zxc11LL", "saription", true, user2, null);
        testEntityManager.persist(item1);
        item1.setId(1);
        testEntityManager.persist(item2);
        item2.setId(2);
        Booking booking1 = new Booking(null,
                LocalDateTime.of(2033, 1, 1, 5, 5, 5),
                LocalDateTime.of(2034, 1, 1, 5, 5, 5),
                item1, user2, BookingStatus.WAITING);
        Booking booking2 = new Booking(null,
                LocalDateTime.of(2035, 1, 1, 5, 5, 5),
                LocalDateTime.of(2040, 1, 1, 5, 5, 5),
                item2, user1, BookingStatus.WAITING);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);
    }

    @Test
    void test_getAllBookingsByItemOwnerId_whenBookingExist_shouldReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.getAllBookingsByItemOwnerId(1,
                PageableCreator.toPageable(0, 20, Sort.by("start").descending()));

        assertFalse(bookings.isEmpty());
    }

    @Test
    void test_getAllBookingsByItemOwnerIdCurrent_whenCurrentBookingsNotExist_shouldReturnEmptyList() {
        List<Booking> bookings = bookingRepository.getAllBookingsByItemOwnerIdCurrent(1,
                LocalDateTime.now(),
                PageableCreator.toPageable(0, 20, Sort.by("start").descending()));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void test_getAllBookingsByItemOwnerIdPast_whenPastBookingsNotExist_shouldReturnEmptyList() {
        List<Booking> bookings = bookingRepository.getAllBookingsByItemOwnerIdPast(1,
                LocalDateTime.now(),
                PageableCreator.toPageable(0, 20, Sort.by("start").descending()));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void test_getAllBookingsByItemOwnerIdFuture_whenFutureBookingsExist_shouldReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.getAllBookingsByItemOwnerIdFuture(1,
                LocalDateTime.now(),
                PageableCreator.toPageable(0, 20, Sort.by("start").descending()));

        assertFalse(bookings.isEmpty());
    }

    @Test
    void test_getAllBookingsByItemOwnerIdAndByStatus_whenStatusWaiting_shouldReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.getAllBookingsByItemOwnerIdAndByStatus(1,
                BookingStatus.WAITING,
                PageableCreator.toPageable(0, 20, Sort.by("start").descending()));

        assertFalse(bookings.isEmpty());
    }

    @Test
    void test_getAllBookingsByBookerId_whenBookingsExist_shouldReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.getAllBookingsByBookerId(1,
                PageableCreator.toPageable(0, 20, Sort.by("start").descending()));

        assertEquals(bookings.size(), 1);
    }

    @Test
    void test_getAllBookingsByBookerIdCurrent_whenBookingNotExistInCurrentTime_shouldReturnEmptyList() {
        List<Booking> bookings = bookingRepository.getAllBookingsByBookerIdCurrent(1,
                LocalDateTime.now(),
                PageableCreator.toPageable(0, 20, Sort.by("start").descending()));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void getAllBookingsByBookerIdPast_whenBookingNotExistInPast_shouldReturnEmptyList() {
        List<Booking> bookings = bookingRepository.getAllBookingsByBookerIdCurrent(1,
                LocalDateTime.now(),
                PageableCreator.toPageable(0, 20, Sort.by("start").descending()));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void test_getAllBookingsByBookerIdFuture_whenBookingsExist_shouldReturnListOfBookings() {
        List<Booking> bookings = bookingRepository.getAllBookingsByBookerIdFuture(1,
                LocalDateTime.now(),
                PageableCreator.toPageable(0, 20, Sort.by("start").descending()));

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getAllBookingsByBookerIdAndByStatus() {
        List<Booking> bookings = bookingRepository.getAllBookingsByItemOwnerIdAndByStatus(1,
                BookingStatus.WAITING,
                PageableCreator.toPageable(0, 20, Sort.by("start").descending()));

        assertFalse(bookings.isEmpty());
    }
}