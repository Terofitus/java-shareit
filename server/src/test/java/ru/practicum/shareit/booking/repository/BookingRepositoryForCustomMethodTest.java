package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryForCustomMethodTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private BookingRepositoryForCustomMethod bookingRepository;

    @BeforeEach
    void setUp() {
        bookingRepository = new BookingRepositoryForCustomMethod(jdbcTemplate);
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
                item1, user2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null,
                LocalDateTime.of(2020, 1, 1, 5, 5, 5),
                LocalDateTime.of(2021, 1, 1, 5, 5, 5),
                item2, user1, BookingStatus.APPROVED);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);
    }

    @Test
    void test_getNextBooking_whenExist_shouldReturnBooking() {
        BookingShortDto bookingShortDto = bookingRepository.getNextBooking(1);

        assertNotNull(bookingShortDto, "BookingShortDto равно null");
        assertEquals(1, bookingShortDto.getId(), "Id возвращенного dto не равно 1");
    }

    @Test
    void test_getLastBooking_whenExist_shouldReturnBooking() {
        BookingShortDto bookingShortDto = bookingRepository.getLastBooking(2);

        assertNotNull(bookingShortDto, "BookingShortDto равно null");
        assertEquals(bookingShortDto.getId(), 2, "Id возвращенного dto не равно 2");
    }

    @Test
    void test_checkPastBookings_whenExist_shouldReturnTrue() {
        boolean pastBooking = bookingRepository.checkPastBookings(2, 1);

        assertTrue(pastBooking, "boolean pastBooking равно false");
    }
}