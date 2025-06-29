package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
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

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingResponseDto findById(Long id, long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking", id));

        Long bookerId = booking.getBooker().getId();
        Item item = booking.getItem();
        Long ownerId = item.getOwnerId();

        if (userId != ownerId && userId != bookerId)
            throw new ValidateException("Wrong userId");

        return BookingResponseDto.from(booking);
    }

    public Collection<BookingResponseDto> findByState(Long userId, State state) {

        log.info("find by state: {} and booker:{}", state.name(), userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        return bookingRepository.findByStateAndBooker(state.name(), userId).stream()
                .map(BookingResponseDto::from)
                .collect(Collectors.toList());
    }

    public Collection<BookingResponseDto> findByOwner(Long ownerId, State state) {
        log.info("find by state: {} and owner:{} ", state.name(), ownerId);
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User", ownerId));

        return bookingRepository.findByStateAndOwner(state.name(), ownerId).stream()
                .map(BookingResponseDto::from)
                .collect(Collectors.toList());
    }

    public BookingResponseDto add(Long bookerId, BookingRequestDto bookingDto) {

        if (!bookingDto.getStart().isBefore(bookingDto.getEnd()))
            throw new ValidateException("Invalid booking dates");

        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User", bookerId));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item", bookingDto.getItemId()));

        if (!item.getAvailable())
            throw new ValidateException("Item id:" + bookingDto.getItemId() + " is unavailable");

        Booking booking = bookingDto.toBooking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        log.info("try add booking {}", booking);
        Booking saved = bookingRepository.save(booking);
        return BookingResponseDto.from(saved);
    }

    public BookingResponseDto update(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking", bookingId));

        if (booking.getStatus() != Status.WAITING)
            throw new ValidateException("This booking already approved/rejected");

        Long itemId = booking.getItem().getId();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item", itemId));

        if (!item.getOwnerId().equals(userId))
            throw new ValidateException("This user does not own the item");

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking saved = bookingRepository.save(booking);
        return BookingResponseDto.from(saved);
    }
}
