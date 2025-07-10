package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.X_SHARER_USER_ID;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final Booking booking = new Booking();

    @Test
    @SneakyThrows
    void getById() {
        BookingResponseDto bookingResponse = new BookingResponseDto();
        bookingResponse.setId(1L);
        bookingResponse.setStatus(Status.APPROVED);
        bookingResponse.setStart(LocalDateTime.of(2023, 1, 1, 12, 0));
        bookingResponse.setEnd(LocalDateTime.of(2023, 1, 2, 12, 0));

        UserDto booker = new UserDto(2L, "Username", "email@gmail.com");
        bookingResponse.setBooker(booker);

        ItemDtoOut item = ItemDtoOut.builder()
                .id(3L)
                .build();
        bookingResponse.setItem(item);

        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mockMvc.perform(get("/bookings/1")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.status").value(bookingResponse.getStatus().name()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()))
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists());
    }

    @Test
    @SneakyThrows
    void getByState() {
        // Подготовка тестовых данных
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);

        UserDto booker = new UserDto(2L, "Booker Name", "booker@email.com");
        ItemDtoOut item = ItemDtoOut.builder()
                .id(3L)
                .name("Test Item")
                .build();

        BookingResponseDto booking1 = new BookingResponseDto();
        booking1.setId(1L);
        booking1.setBooker(booker);
        booking1.setItem(item);
        booking1.setStart(start);
        booking1.setEnd(end);
        booking1.setStatus(Status.APPROVED);

        BookingResponseDto booking2 = new BookingResponseDto();
        booking2.setId(2L);
        booking2.setBooker(booker);
        booking2.setItem(item);
        booking2.setStart(start.plusDays(3));
        booking2.setEnd(end.plusDays(3));
        booking2.setStatus(Status.APPROVED);

        List<BookingResponseDto> bookings = List.of(booking1, booking2);

        // Настройка мока
        when(bookingService.findByState(anyLong(), any(State.class)))
                .thenReturn(bookings);

        // Выполнение запроса и проверки
        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, "2")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].booker.id").value(2L))
                .andExpect(jsonPath("$[0].item.id").value(3L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].booker.id").value(2L))
                .andExpect(jsonPath("$[1].item.id").value(3L))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));
    }

    @Test
    @SneakyThrows
    void getByOwner() {
        // Подготовка тестовых данных
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);

        UserDto booker = new UserDto(2L, "Booker Name", "booker@email.com");
        ItemDtoOut item = ItemDtoOut.builder()
                .id(3L)
                .name("Test Item")
                .ownerId(1L)  // Владелец предмета
                .build();

        BookingResponseDto booking1 = new BookingResponseDto();
        booking1.setId(1L);
        booking1.setBooker(booker);
        booking1.setItem(item);
        booking1.setStart(start);
        booking1.setEnd(end);
        booking1.setStatus(Status.APPROVED);

        BookingResponseDto booking2 = new BookingResponseDto();
        booking2.setId(2L);
        booking2.setBooker(booker);
        booking2.setItem(item);
        booking2.setStart(start.plusDays(3));
        booking2.setEnd(end.plusDays(3));
        booking2.setStatus(Status.WAITING);

        List<BookingResponseDto> bookings = List.of(booking1, booking2);

        // Настройка мока
        when(bookingService.findByOwner(anyLong(), any(State.class)))
                .thenReturn(bookings);

        // Выполнение запроса и проверки
        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, "1")  // ID владельца
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].booker.id").value(2L))
                .andExpect(jsonPath("$[0].item.id").value(3L))
                .andExpect(jsonPath("$[0].item.ownerId").value(1L))  // Проверка владельца
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].status").value("WAITING"));
    }

    @Test
    @SneakyThrows
    void add() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);

        BookingRequestDto bookingDto = new BookingRequestDto();
        bookingDto.setItemId(3L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setStatus(Status.WAITING);

        UserDto booker = new UserDto(2L, "Username", "email@gmail.com");
        ItemDtoOut item = ItemDtoOut.builder()
                .id(3L)
                .name("Item Name")
                .build();

        BookingResponseDto expectedResponse = new BookingResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setBooker(booker);
        expectedResponse.setItem(item);
        expectedResponse.setStart(start);
        expectedResponse.setEnd(end);
        expectedResponse.setStatus(Status.WAITING);

        // Настройка мока
        when(bookingService.add(anyLong(), any(BookingRequestDto.class)))
                .thenReturn(expectedResponse);

        // Выполнение запроса и проверки
        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, "2")
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(2L))
                .andExpect(jsonPath("$.item.id").value(3L))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @SneakyThrows
    void update() {
        // Подготовка тестовых данных
        Long bookingId = 1L;
        Long ownerId = 1L;
        boolean approved = true;

        UserDto booker = new UserDto(2L, "Booker Name", "booker@email.com");
        ItemDtoOut item = ItemDtoOut.builder()
                .id(3L)
                .name("Test Item")
                .ownerId(ownerId)
                .build();

        BookingResponseDto updatedBooking = new BookingResponseDto();
        updatedBooking.setId(bookingId);
        updatedBooking.setBooker(booker);
        updatedBooking.setItem(item);
        updatedBooking.setStart(LocalDateTime.now().plusDays(1));
        updatedBooking.setEnd(LocalDateTime.now().plusDays(3));
        updatedBooking.setStatus(Status.APPROVED);

        // Настройка мока
        when(bookingService.update(eq(ownerId), eq(bookingId), eq(approved)))
                .thenReturn(updatedBooking);

        // Выполнение запроса и проверки
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(X_SHARER_USER_ID, ownerId.toString())
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.booker.id").value(2L))
                .andExpect(jsonPath("$.item.id").value(3L))
                .andExpect(jsonPath("$.item.ownerId").value(ownerId))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists());
    }
}