package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookingDtoForCreateUpdate;
import ru.practicum.shareit.booking.dto.BookingDtoForGet;
import ru.practicum.shareit.booking.exception.ItemNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.BookingMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class})
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    private final EasyRandom generator = new EasyRandom();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void test_getBookingById_whenIdCorrect_shouldReturnJsonAndStatus200() throws Exception {
        Booking booking = generator.nextObject(Booking.class);
        booking.setId(1);

        Mockito.when(bookingService.getBookingByOwnerOrBookerId(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(booking);

        MvcResult result = mockMvc.perform(get("/bookings/1")
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        BookingDtoForGet bookingDto = mapper.readValue(result.getResponse().getContentAsString(),
                BookingDtoForGet.class);

        assertEquals(booking.getId(), bookingDto.getId(), "Возвращенное бронирование в результате метода get " +
                " имеет отличный от ожидаемого id");
    }

    @Test
    void test_getAllBookingsByUserIdAndState_whenStatAll_shouldReturnJsonAndStatus200() throws Exception {
        Booking booking1 = generator.nextObject(Booking.class);
        booking1.setId(1);
        Booking booking2 = generator.nextObject(Booking.class);
        booking2.setId(2);
        List<Booking> bookings = List.of(booking1, booking2);

        Mockito.when(bookingService.getAllBookingsByUserIdAndState(Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(bookings);
        MvcResult result = mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        List<BookingDtoForGet> bookingsDto = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<BookingDtoForGet>>() {
                });
        assertEquals( 2, bookingsDto.size(), "Размер возвращенного списка не равен 2");
    }

    @Test
    void test_getAllBookingsByOwnerIdAndState_whenNoHeaderWithUserId_shouldReturnErrorMessageAndStatus500()
            throws Exception {
        mockMvc.perform(get("/bookings/owner")).andExpect(status().isInternalServerError());
    }

    @Test
    void test_getAllBookingsByOwnerIdAndState_whenArgumentsCorrect_shouldReturnStatus200()
            throws Exception {
        mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void test_addBooking_whenBookingNotValid_shouldReturnStatus500AndErrorMessage() throws Exception {
        BookingDtoForCreateUpdate bookingDto = generator.nextObject(BookingDtoForCreateUpdate.class);
        bookingDto.setId(1);
        String json = mapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1).content(json))
                .andExpectAll(status().isInternalServerError(), content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_addBooking_whenItemNotAvailable_shouldReturnStatus404AndErrorMessage() throws Exception {
        BookingDtoForCreateUpdate bookingDto = generator.nextObject(BookingDtoForCreateUpdate.class);
        bookingDto.setId(1);
        String json = mapper.writeValueAsString(bookingDto);

        Mockito.when(bookingService.addBooking(Mockito.any(BookingDtoForCreateUpdate.class), Mockito.anyInt()))
                .thenThrow(new ItemNotFoundException("Нельзя создать бронирование на собственную вещь."));

        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1).content(json))
                .andExpectAll(status().isNotFound(), content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_addBooking_whenArgumentsCorrect_shouldReturnStatus200() throws Exception {
        BookingDtoForCreateUpdate bookingDto = new BookingDtoForCreateUpdate(null, 2,
                LocalDateTime.of(2023, 10, 10, 10, 20, 20),
                LocalDateTime.of(2024, 10, 10, 10, 20, 20));
        User user = new User(1, "zxv", "cvb41@maill.ru");
        User user2 = new User(2, "zxv", "cvbasd41@maill.ru");
        Item item = new Item(2, "asd", "azxc", true, user2, null);
        Booking booking = BookingMapper.toBookingFromDtoCreate(bookingDto, user, item);
        String json = mapper.writeValueAsString(bookingDto);
        booking.setId(1);

        Mockito.when(bookingService.addBooking(Mockito.any(BookingDtoForCreateUpdate.class), Mockito.anyInt()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1).content(json))
                .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_updateBooking_whenCalled_shouldReturnBookingDto() throws Exception {
        BookingDtoForCreateUpdate bookingDto = generator.nextObject(BookingDtoForCreateUpdate.class);
        Item item = generator.nextObject(Item.class);
        item.setAvailable(true);
        User user = generator.nextObject(User.class);
        Booking booking = BookingMapper.toBookingFromDtoCreate(bookingDto, user, item);

        Mockito.when(bookingService.getBookingByOwnerOrBookerId(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(booking);
        booking.setId(1);
        Mockito.when(bookingService.updateBooking(Mockito.any(Booking.class), Mockito.anyBoolean(), Mockito.anyInt()))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/1?approved=true").header("X-Sharer-User-Id", 1))
                .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_deleteBookingById_whenCalled_shouldReturnStatus200() throws Exception {
        mockMvc.perform(delete("/bookings/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void test_deleteAllBookingsByOwnerId_whenCalled_shouldReturnStatus200() throws Exception {
        mockMvc.perform(delete("/bookings").header("X-Sharer-User-Id", 1))
                .andExpectAll(status().isOk());
    }
}