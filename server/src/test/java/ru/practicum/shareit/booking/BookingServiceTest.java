package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private final LocalDateTime now = LocalDateTime.now();
    private final User user = new User(1L, "user", "user@email.com");
    private final Item item = new Item(1L, "item", "description", true, 2L);
    private final Booking booking = Booking.builder()
            .id(1L)
            .start(now.plusDays(1))
            .end(now.plusDays(2))
            .item(item)
            .booker(user)
            .status(Status.WAITING)
            .build();

    // Тест для findById
    @Test
    void findById_ShouldReturnBooking_WhenUserIsOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoOut result = bookingService.findById(1L, 2L); // ownerId = 2L

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_ShouldThrow_WhenUserNotOwnerOrBooker() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.findById(1L, 3L)) // random user
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("Wrong userId");
    }

    // Тесты для findByState
    @Test
    void findByState_ShouldReturnBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStateAndBooker(anyString(), anyLong()))
                .thenReturn(List.of(booking));

        Collection<BookingDtoOut> result = bookingService.findByState(1L, State.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(1L);
    }

    @Test
    void findByState_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.findByState(1L, State.ALL))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User");
    }

    // Тесты для findByOwner
    @Test
    void findByOwner_ShouldReturnBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStateAndOwner(anyString(), anyLong()))
                .thenReturn(List.of(booking));

        Collection<BookingDtoOut> result = bookingService.findByOwner(2L, State.ALL); // ownerId = 2L

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(1L);
    }

    // Тесты для add
    @Test
    void add_ShouldCreateBooking() {
        BookingDto requestDto = new BookingDto();
        requestDto.setItemId(1L);
        requestDto.setStart(now.plusDays(1));
        requestDto.setEnd(now.plusDays(2));
        requestDto.setStatus(Status.WAITING);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoOut result = bookingService.add(1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void add_ShouldThrow_WhenInvalidDates() {
        BookingDto requestDto = new BookingDto();
        requestDto.setItemId(1L);
        requestDto.setStart(now.plusDays(2));
        requestDto.setEnd(now.plusDays(1));
        requestDto.setStatus(Status.WAITING);

        assertThatThrownBy(() -> bookingService.add(1L, requestDto))
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("Invalid booking dates");
    }

    @Test
    void add_ShouldThrow_WhenItemUnavailable() {
        BookingDto requestDto = new BookingDto();
        requestDto.setItemId(1L);
        requestDto.setStart(now.plusDays(1));
        requestDto.setEnd(now.plusDays(2));
        requestDto.setStatus(Status.WAITING);

        item.setAvailable(false);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.add(1L, requestDto))
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("unavailable");
    }

    // Тесты для update
    @Test
    void update_ShouldApproveBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoOut result = bookingService.update(2L, 1L, true); // ownerId = 2L

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void update_ShouldThrow_WhenNotOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.update(3L, 1L, true)) // random user
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("does not own");
    }

    @Test
    void update_ShouldThrow_WhenNotWaiting() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(2L, 1L, true))
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("This booking already approved/rejected");
    }
}