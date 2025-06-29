package ru.practicum.shareit.item;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.TinyBookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto add(ItemDto itemDto, long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        Item item = itemDto.toItem();
        item.setOwnerId(userId);
        Item added = itemRepository.save(item);
        return ItemDto.fromItem(added);
    }

    public Collection<ItemDto> getAll() {
        return itemRepository.findAll().stream()
                .map(ItemDto::fromItem)
                .toList();
    }

    public ItemResponseDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item", itemId));
        ItemResponseDto itemDto = ItemResponseDto.fromItem(item);

        if (item.getOwnerId().equals(userId)) {
            Booking lastBooking =
                    bookingRepository.findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(itemId,
                            LocalDateTime.now(), Set.of(Status.APPROVED)).orElse(null);

            Booking nextBooking =
                    bookingRepository.findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(itemId,
                            LocalDateTime.now(), Set.of(Status.APPROVED)).orElse(null);

            itemDto.setLastBooking(TinyBookingResponseDto.from(lastBooking));
            itemDto.setNextBooking(TinyBookingResponseDto.from(nextBooking));
        }

        Collection<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedAsc(itemId);
        itemDto.setComments(comments.stream()
                .map(CommentResponseDto::from)
                .collect(Collectors.toList()));

        return itemDto;
    }

    public ItemDto update(Long itemId, long ownerId, ItemDto itemDto) {
        Item item = itemDto.toItem();
        item.setId(itemId);

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User", ownerId));

        Item origin = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item", itemId));

        if (item.getName() != null && !item.getName().equals(origin.getName()))
            origin.setName(item.getName());

        if (item.getDescription() != null && !item.getDescription().equals(origin.getDescription()))
            origin.setDescription(item.getDescription());

        if (item.getAvailable() != null)
            origin.setAvailable(item.getAvailable());

        item.setOwnerId(ownerId);
        Item updated = itemRepository.save(origin);
        return ItemDto.fromItem(updated);
    }

    public Collection<ItemDto> getByOwner(long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(ItemDto::fromItem)
                .toList();
    }

    public Collection<ItemDto> search(Long ownerId, String text) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User", ownerId));
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Collection<Item> itemList = itemRepository.findByNameContainingIgnoreCaseAndOwnerIdAndAvailableTrue(text, ownerId);
        return itemList.stream()
                .map(ItemDto::fromItem)
                .toList();
    }

    public CommentResponseDto addComment(long userId, @Min(1) Long itemId, CommentRequestDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item", itemId));

        Collection<Booking> userBookings = bookingRepository.findApprovedPastAndCurrentByBookerIdAndItemId(userId, itemId);
        if (userBookings.isEmpty()) {
            throw new ValidateException("User id:" + userId + " does not have current or past bookings of item id:" + itemId);
        }

        Comment comment = commentDto.toComment();
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return CommentResponseDto.from(saved);
    }
}
