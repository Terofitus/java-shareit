package ru.practicum.shareit.booking.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.BookingShortDto;

import java.time.LocalDateTime;
import java.util.Map;

@Repository
public class BookingRepositoryForCustomMethod {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookingRepositoryForCustomMethod(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public BookingShortDto getNextBooking(int itemId) {
        String sqlQuery = "select b.id, b.booker_id from bookings as b where b.item_id=? and (b.start_date > ?)" +
                " and b.status='APPROVED' order by (extract(epoch from b.start_date) - extract(epoch from ?))" +
                " limit 1";
        Map<String, Object> mapOfColumns;
        try {
            mapOfColumns = jdbcTemplate.queryForMap(sqlQuery, itemId, LocalDateTime.now(), LocalDateTime.now());
        } catch (DataAccessException e) {
            return null;
        }
        return new BookingShortDto((int) mapOfColumns.get("id"), (int) mapOfColumns.get("booker_id"));
    }

    public BookingShortDto getLastBooking(int itemId) {
        String sqlQuery = "select b.id, b.booker_id from bookings as b where b.item_id=?" +
                " and b.status='APPROVED' order by abs(extract(epoch from b.end_date) - extract(epoch from ?)) limit 1";
        Map<String, Object> mapOfColumns;
        try {
            mapOfColumns = jdbcTemplate.queryForMap(sqlQuery, itemId, LocalDateTime.now());
        } catch (DataAccessException e) {
            return null;
        }
        return new BookingShortDto((int) mapOfColumns.get("id"), (int) mapOfColumns.get("booker_id"));
    }


    public boolean checkPastBookings(int itemId, int userId) {
        String sqlQuery = "select count(*) from bookings as b where b.item_id=? and b.booker_id=?" +
                "and b.end_date < ? and b.status='APPROVED'";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, itemId, userId, LocalDateTime.now());
        return (count != null ? count : 0) > 0;
    }
}
