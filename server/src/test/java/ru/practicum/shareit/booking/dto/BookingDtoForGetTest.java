package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoForGetTest {
    @Autowired
    private JacksonTester<BookingDtoForGet> jacksonTester;

    @Test
    void test_dtoColumn() throws IOException {
        BookingDtoForGet dto = new BookingDtoForGet(1,
                LocalDateTime.of(2033, 1, 1, 5, 5, 5),
                LocalDateTime.of(2034, 1, 1, 5, 5, 5),
                new BookingDtoForGet.ItemShortBookingDto(1, "ASD"),
                new BookingDtoForGet.UserShortDto(1),
                BookingStatus.WAITING);

        JsonContent<BookingDtoForGet> result = jacksonTester.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");
    }
}