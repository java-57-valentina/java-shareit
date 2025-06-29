package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingResponseDto {

    private Long id;

    private UserDto booker;
    private ItemDto item;
    private Status status;

    private LocalDateTime start;
    private LocalDateTime end;

    public static BookingResponseDto from(Booking booking) {
        BookingResponseDto bookingDto = new BookingResponseDto();
        bookingDto.setId(booking.getId());
        bookingDto.setItem(ItemDto.fromItem(booking.getItem()));
        bookingDto.setBooker(UserDto.fromUser(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        return bookingDto;
    }
}
