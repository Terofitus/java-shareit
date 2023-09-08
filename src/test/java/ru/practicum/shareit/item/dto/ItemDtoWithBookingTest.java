package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoWithBookingTest {
    @Autowired
    private JacksonTester<ItemDtoWithBooking> jacksonTester;

    @Test
    void test_dtoColumn() throws IOException {
        ItemDtoWithBooking dto = new ItemDtoWithBooking(1, "asdasd", "asdasdasd", true,
                new BookingShortDto(1, 2), new BookingShortDto(2, 1), null);

        JsonContent<ItemDtoWithBooking> result = jacksonTester.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.comments");
    }
}