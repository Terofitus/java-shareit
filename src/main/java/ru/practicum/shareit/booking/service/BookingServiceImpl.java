package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoForCreateUpdate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemService itemService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    @Override
    public Booking getBookingByOwnerOrBookerId(int bookingId, int ownerId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new BookingNotFoundException(String.format("Бронирование с id=%d не найдено.", bookingId));
        }
        Booking booking = optionalBooking.get();
        if (!(booking.getBooker().getId() == ownerId || booking.getItem().getOwner().getId() == ownerId)) {
            throw new BookingNotFoundException(String.format("Бронирование с id=%d не найдено.", bookingId));
        }
        log.info("Запрошено бронирование с id={} от лица user с id={}", bookingId, ownerId);
        return booking;
    }

    @Override
    public List<Booking> getAllBookingsByUserIdAndState(int userId, String state) {
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.getAllBookingsByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.getAllBookingsByBookerIdCurrent(userId, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.getAllBookingsByBookerIdPast(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.getAllBookingsByBookerIdFuture(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.getAllBookingsByBookerIdWaiting(userId);
                break;
            case "REJECTED":
                bookings = bookingRepository.getAllBookingsByBookerIdRejected(userId);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        if (bookings.isEmpty()) {
            throw new BookingNotFoundException("По заданным параметрам не найдено ни 1 бронирования.");
        }
        log.info("Получен запрос собственных бронирований от user с id={} и state={}", userId, state);
        return bookings;
    }

    @Override
    public List<Booking> getAllBookingsByItemOwnerIdAndState(int userId, String state) {
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.getAllBookingsByItemOwnerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.getAllBookingsByItemOwnerIdCurrent(userId, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.getAllBookingsByItemOwnerIdPast(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.getAllBookingsByItemOwnerIdFuture(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.getAllBookingsByItemOwnerIdWaiting(userId);
                break;
            case "REJECTED":
                bookings = bookingRepository.getAllBookingsByItemOwnerIdRejected(userId);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        if (bookings.isEmpty()) {
            throw new BookingNotFoundException("По заданным параметрам не найдено ни 1 бронирования.");
        }
        log.info("Получен запрос бронирований своих вещей от user с id={} и state={}", userId, state);
        return bookings;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public Booking addBooking(BookingDtoForCreateUpdate bookingDtoForCreateUpdate, int userId) {
        User user = userService.getUserById(userId);
        Item item = itemService.getItemById(bookingDtoForCreateUpdate.getItemId());
        if (Objects.equals(item.getOwner().getId(), user.getId())) {
            throw new ItemNotFoundException("Нельзя создать бронирование на собственную вещь.");
        }
        Booking booking = BookingMapper.toBookingFromDtoCreate(bookingDtoForCreateUpdate, user, item);
        Booking bookingFromDb = bookingRepository.save(booking);
        log.info("Добавлено бронирование от user с id={} на item с id={}", userId, booking.getItem().getId());
        return bookingFromDb;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public Booking updateBooking(Booking booking, boolean approved, int userId) {
        if (booking.getItem().getOwner().getId() != userId) {
            log.info("Попытка изменения статуса чужого бронирования с id={} от user id={}", booking.getId(), userId);
            throw new BookingNotFoundException(String.format("Бронирование с id=%d не найдено.", booking.getId()));
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.info("Попытка изменения статуса бронирования с id={}, отличного от \"WAITING\"", booking.getId());
            throw new IllegalArgumentException("Статус бронирования можно изменить" +
                    " только у бронирования со статусом \"WAITING\".");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking bookingFromDb = bookingRepository.save(booking);
        log.info("Статус бронирования с id={} был изменен на {}", booking.getId(), booking.getStatus());
        return bookingFromDb;
    }

    @Override
    public void deleteBookingById(int bookingId, int ownerId) {
        bookingRepository.deleteBookingById(bookingId, ownerId);
        log.info("Запрос на удаление бронирования с id={} от user с id={}", bookingId, ownerId);
    }

    @Override
    public void deleteAllBookings(int ownerId) {
        bookingRepository.deleteAllByBookerId(ownerId);
        log.info("Запрос на удаление всех бронирований от user с id={}", ownerId);
    }
}
