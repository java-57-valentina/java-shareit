package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.TinyBookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(2);

    @Test
    void toBooking_ShouldMapBasicFields() {
        BookingDto dto = new BookingDto();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setStatus(Status.WAITING);

        Booking result = BookingMapper.toBooking(dto);

        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    void toResponseDto_ShouldMapRequiredFields() {
        User booker = new User(1L, "user", "user@email.com");
        Item item = new Item(1L, "item", "desc", true, 2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(Status.APPROVED);
        booking.setItem(item);
        booking.setBooker(booker);

        BookingDtoOut result = BookingMapper.toResponseDto(booking);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertNotNull(result.getItem());
        assertNotNull(result.getBooker());
    }

    @Test
    void toTinyDto_ShouldReturnNullForNullInput() {
        assertNull(BookingMapper.toTinyDto(null));
    }

    @Test
    void toTinyDto_ShouldMapBasicFields() {
        User booker = new User(1L, "user", "user@email.com");
        Item item = new Item(1L, "item", "desc", true, 2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);

        TinyBookingDtoOut result = BookingMapper.toTinyDto(booking);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getItemId());
        assertEquals(1L, result.getBookerId());
    }
}