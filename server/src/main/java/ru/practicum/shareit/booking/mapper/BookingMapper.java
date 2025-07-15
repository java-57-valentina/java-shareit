package ru.practicum.shareit.booking.mapper;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.TinyBookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@UtilityClass
public class BookingMapper {

    public Booking toBooking(BookingDto dto) {
        Booking booking = new Booking();
        booking.setStatus(dto.getStatus());
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        return booking;
    }

    public BookingDtoOut toResponseDto(Booking booking) {
        BookingDtoOut bookingDto = new BookingDtoOut();
        bookingDto.setId(booking.getId());
        bookingDto.setItem(ItemMapper.toDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        return bookingDto;
    }

    public TinyBookingDtoOut toTinyDto(@Nullable Booking booking) {
        if (booking == null)
            return null;

        TinyBookingDtoOut bookingDto = new TinyBookingDtoOut();
        bookingDto.setId(booking.getId());
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setBookerId(booking.getBooker().getId());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        return bookingDto;
    }
}
