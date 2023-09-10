package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.dto.BookingDtoForCreateUpdate;
import ru.practicum.shareit.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, String> parameters = Map.of(
                "state", state.name(),
                "from", String.valueOf(from),
                "size", String.valueOf(size)
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }


    public ResponseEntity<Object> bookItem(long userId, BookingDtoForCreateUpdate requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingsByOwnerIdAndState(Integer userId, BookingState state, Integer from,
                                                                  Integer size) {
        Map<String, String> parameters = Map.of(
                "state", state.name(),
                "from", String.valueOf(from),
                "size", String.valueOf(size)
        );
        return get("/owner?state={state}&from={from}&size={size}", Long.valueOf(userId), parameters);
    }

    public ResponseEntity<Object> updateBooking(Integer userId, Integer bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId, null);
    }

    public ResponseEntity<Object> deleteBookingById(Integer userId, Integer bookingId) {
        return delete("/" + bookingId, userId);
    }

    public ResponseEntity<Object> deleteAllBookingsByOwnerId(Integer ownerId) {
        return delete("/", ownerId);
    }
}
