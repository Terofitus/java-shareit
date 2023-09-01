package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Integer>,
        PagingAndSortingRepository<Booking, Integer> {

    List<Booking> getAllBookingsByItemOwnerId(int userId, Sort sort);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.start<?2 and b.end>?2 order by b.start desc")
    List<Booking> getAllBookingsByItemOwnerIdCurrent(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.end<?2 order by b.start desc")
    List<Booking> getAllBookingsByItemOwnerIdPast(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.start>?2 order by b.start desc")
    List<Booking> getAllBookingsByItemOwnerIdFuture(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.status=?2 order by b.start desc")
    List<Booking> getAllBookingsByItemOwnerIdAndByStatus(int userId, BookingStatus status);

    List<Booking> getAllBookingsByBookerId(int userId, Sort sort);

    @Query("select b from Booking b where b.booker.id=?1 and b.start<?2 and b.end>?2 order by b.start desc")
    List<Booking> getAllBookingsByBookerIdCurrent(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.booker.id=?1 and b.end<?2 order by b.start desc")
    List<Booking> getAllBookingsByBookerIdPast(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.booker.id=?1 and b.start>?2 order by b.start desc")
    List<Booking> getAllBookingsByBookerIdFuture(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.booker.id=?1 and b.status=?2 order by b.start desc")
    List<Booking> getAllBookingsByBookerIdAndByStatus(int userId, BookingStatus status);

    void deleteAllByBookerId(int ownerId);

    @Query("delete from Booking b where b.id=?1 and b.item.owner.id=?2")
    void deleteBookingById(int bookingId, int ownerId);
}
