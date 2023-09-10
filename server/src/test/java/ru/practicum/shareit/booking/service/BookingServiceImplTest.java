package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.BookingDtoForCreateUpdate;
import ru.practicum.dto.BookingState;
import ru.practicum.dto.BookingStatus;
import ru.practicum.exception.BookingNotFoundException;
import ru.practicum.exception.ItemNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.BookingMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    private final EasyRandom generator = new EasyRandom();

    @Test
    void test_getBookingByOwnerOrBookerId_whenCalledMethod_shouldReturnBookingAfterSave() {
        Booking booking = generator.nextObject(Booking.class);
        booking.setId(1);
        booking.getBooker().setId(1);
        Mockito.when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(booking));

        Booking bookingFromRepository = bookingService.getBookingByOwnerOrBookerId(1, 1);

        assertEquals(bookingFromRepository, booking);
    }

    @Test
    void test_getBookingByOwnerOrBookerId_whenCalledWithNotExistingBookingId_shouldThrowException() {
        Mockito.when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingByOwnerOrBookerId(1, 1));

        assertEquals("Бронирование с id=1 не найдено.", exception.getMessage());
    }

    @Test
    void test_getAllBookingsByUserIdAndState_whenCalledMethod_shouldReturnListOfBookings() {
        Booking booking1 = generator.nextObject(Booking.class);
        booking1.setId(1);
        booking1.getBooker().setId(1);
        Booking booking2 = generator.nextObject(Booking.class);
        booking2.setId(2);
        booking2.getBooker().setId(1);

        Mockito.when(bookingRepository.getAllBookingsByBookerId(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));

        List<Booking> bookings = bookingService.getAllBookingsByUserIdAndState(2, BookingState.ALL.name(),
                0, 20);

        assertEquals(2, bookings.size());
        assertEquals(booking1, bookings.get(0));
    }

    @Test
    void test_getAllBookingsByUserIdAndState_whenStateIncorrect_shouldThrowException() {
        Booking booking1 = generator.nextObject(Booking.class);
        booking1.setId(1);
        booking1.getBooker().setId(1);
        Booking booking2 = generator.nextObject(Booking.class);
        booking2.setId(2);
        booking2.getBooker().setId(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByUserIdAndState(1, "asdasd", 0, 20));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void test_getAllBookingsByUserIdAndState_whenCallMethodWithIllegalState_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByItemOwnerIdAndState(1, "asd", 0, 20));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void test_getAllBookingsByItemOwnerIdAndState_whenCalledMethod_shouldReturnListOfBookings() {
        Booking booking1 = generator.nextObject(Booking.class);
        booking1.setId(1);
        booking1.getBooker().setId(1);
        Booking booking2 = generator.nextObject(Booking.class);
        booking2.setId(1);
        booking2.getBooker().setId(1);

        Mockito.when(bookingRepository.getAllBookingsByBookerId(Mockito.anyInt(), Mockito.any()))
                .thenReturn(List.of(booking1, booking2));

        List<Booking> bookings = bookingService.getAllBookingsByUserIdAndState(2, BookingState.ALL.name(),
                0, 20);

        assertEquals(2, bookings.size());
        assertEquals(booking1, bookings.get(0));
    }

    @Test
    void test_getAllBookingsByItemOwnerIdAndState_whenCallMethodWithIllegalState_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByItemOwnerIdAndState(1, "asd", 0, 20));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }


    @Test
    void test_addBooking_whenCallMethod_shouldReturnAddedBooking() {
        BookingDtoForCreateUpdate bookingDto = generator.nextObject(BookingDtoForCreateUpdate.class);
        bookingDto.setId(null);
        User user = generator.nextObject(User.class);
        user.setId(1);
        Item item = generator.nextObject(Item.class);
        Booking booking = BookingMapper.toBookingFromDtoCreate(bookingDto, user, item);
        item.getOwner().setId(2);

        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);
        Mockito.when(itemService.getItemById(Mockito.anyInt())).thenReturn(item);
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking);

        Booking bookingFromService = bookingService.addBooking(bookingDto, user.getId());

        assertEquals(bookingFromService, booking);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
    }

    @Test
    void test_addBooking_whenBookerIdEqualsItemOwnerId_shouldThrowException() {
        BookingDtoForCreateUpdate bookingDto = generator.nextObject(BookingDtoForCreateUpdate.class);
        bookingDto.setId(1);
        User user = generator.nextObject(User.class);
        user.setId(1);
        Item item = generator.nextObject(Item.class);
        item.getOwner().setId(1);
        Booking booking = generator.nextObject(Booking.class);
        booking.setId(1);

        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);
        Mockito.when(itemService.getItemById(Mockito.anyInt())).thenReturn(item);

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.addBooking(bookingDto, 1));
        assertEquals("Нельзя создать бронирование на собственную вещь.", exception.getMessage());
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void test_updateBooking_whenCalledMethod_shouldReturnUpdatedBooking() {
        Booking booking = generator.nextObject(Booking.class);
        booking.setId(1);
        booking.getItem().getOwner().setId(1);
        booking.setStatus(BookingStatus.WAITING);

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        Booking bookingFromService = bookingService.updateBooking(booking, true, 1);

        assertEquals(booking.getId(), bookingFromService.getId());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
    }

    @Test
    void test_updateBooking_whenItemOwnerEqualsUser_shouldThrowException() {
        Booking booking = generator.nextObject(Booking.class);
        booking.getItem().getOwner().setId(2);
        booking.setId(2);

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateBooking(booking, true, 1));
        assertEquals("Бронирование с id=2 не найдено.", exception.getMessage());
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void test_updateBooking_whenBookingStatusWaiting_shouldThrowException() {
        Booking booking = generator.nextObject(Booking.class);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setId(2);
        booking.getItem().getOwner().setId(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.updateBooking(booking, true, 1));
        assertEquals("Статус бронирования можно изменить" +
                " только у бронирования со статусом \"WAITING\".", exception.getMessage());
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void test_deleteBookingById_shouldCallMethodDeleteBookingByIdOfBookingRepository() {
        bookingService.deleteBookingById(1, 2);

        Mockito.verify(bookingRepository, Mockito.times(1)).deleteBookingById(1, 2);
    }

    @Test
    void test_deleteAllBookings_shouldCallMethodDeleteAllBookingOfBookingRepository() {
        bookingService.deleteAllBookings(1);

        Mockito.verify(bookingRepository, Mockito.times(1)).deleteAllByBookerId(1);
    }
}