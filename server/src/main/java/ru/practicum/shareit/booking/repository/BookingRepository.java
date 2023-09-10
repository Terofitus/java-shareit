package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Integer>,
        PagingAndSortingRepository<Booking, Integer> {

    List<Booking> getAllBookingsByItemOwnerId(int userId, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.start<?2 and b.end>?2")
    List<Booking> getAllBookingsByItemOwnerIdCurrent(int userId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.end<?2")
    List<Booking> getAllBookingsByItemOwnerIdPast(int userId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.start>?2")
    List<Booking> getAllBookingsByItemOwnerIdFuture(int userId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.status=?2")
    List<Booking> getAllBookingsByItemOwnerIdAndByStatus(int userId, BookingStatus status, Pageable pageable);

    List<Booking> getAllBookingsByBookerId(int userId, Pageable pageable);

    @Query("select b from Booking b where b.booker.id=?1 and b.start<?2 and b.end>?2")
    List<Booking> getAllBookingsByBookerIdCurrent(int userId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.booker.id=?1 and b.end<?2")
    List<Booking> getAllBookingsByBookerIdPast(int userId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.booker.id=?1 and b.start>?2")
    List<Booking> getAllBookingsByBookerIdFuture(int userId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.booker.id=?1 and b.status=?2")
    List<Booking> getAllBookingsByBookerIdAndByStatus(int userId, BookingStatus status, Pageable pageable);

    void deleteAllByBookerId(int ownerId);

    @Query("delete from Booking b where b.id=?1 and b.item.owner.id=?2")
    void deleteBookingById(int bookingId, int ownerId);
}
