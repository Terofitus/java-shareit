package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Integer> {

    List<Booking> getAllBookingsByItemOwnerIdOrderByStartDesc(int userId);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.start<?2 and b.end>?2 order by b.start desc")
    List<Booking> getAllBookingsByItemOwnerIdCurrent(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.end<?2 order by b.start desc")
    List<Booking> getAllBookingsByItemOwnerIdPast(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.start>?2 order by b.start desc")
    List<Booking> getAllBookingsByItemOwnerIdFuture(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.status='WAITING' order by b.start desc")
    List<Booking> getAllBookingsByItemOwnerIdWaiting(int userId);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.status='REJECTED' order by b.start desc")
    List<Booking> getAllBookingsByItemOwnerIdRejected(int userId);

    List<Booking> getAllBookingsByBookerIdOrderByStartDesc(int userId);

    @Query("select b from Booking b where b.booker.id=?1 and b.start<?2 and b.end>?2 order by b.start desc")
    List<Booking> getAllBookingsByBookerIdCurrent(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.booker.id=?1 and b.end<?2 order by b.start desc")
    List<Booking> getAllBookingsByBookerIdPast(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.booker.id=?1 and b.start>?2 order by b.start desc")
    List<Booking> getAllBookingsByBookerIdFuture(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.booker.id=?1 and b.status='WAITING' order by b.start desc")
    List<Booking> getAllBookingsByBookerIdWaiting(int userId);

    @Query("select b from Booking b where b.booker.id=?1 and b.status='REJECTED' order by b.start desc")
    List<Booking> getAllBookingsByBookerIdRejected(int userId);

    void deleteAllByBookerId(int ownerId);

    @Query("delete from Booking b where b.id=?1 and b.item.owner.id=?2")
    void deleteBookingById(int bookingId, int ownerId);
}
