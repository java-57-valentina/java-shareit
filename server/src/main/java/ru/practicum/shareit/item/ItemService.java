package ru.practicum.shareit.item;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.responce.model.ItemResponse;
import ru.practicum.shareit.responce.repository.ItemResponseRepository;
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
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemResponseRepository responseRepository;
    private final ItemRequestRepository requestRepository;

    @Transactional
    public ItemDtoOut add(ItemDto itemDto, long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        Item added = itemRepository.save(item);

        if (itemDto.getRequestId() != null) {
            log.info("RequestId: {}", itemDto.getRequestId());

            ItemRequest request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request", userId));

            ItemResponse response = new ItemResponse();
            response.setItem(item);
            response.setRequest(request);
            ItemResponse saved = responseRepository.save(response);
            log.info("ItemResponse saved: {}", saved);
        }

        return ItemMapper.toDto(added);
    }

    public Collection<ItemDtoOut> getAll() {
        return itemRepository.findAll().stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    public ItemDtoExtOut getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item", itemId));
        ItemDtoExtOut itemDto = ItemMapper.toExtDto(item);

        if (item.getOwnerId().equals(userId)) {
            Booking lastBooking =
                    bookingRepository.findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(itemId,
                            LocalDateTime.now(), Set.of(Status.APPROVED)).orElse(null);

            Booking nextBooking =
                    bookingRepository.findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(itemId,
                            LocalDateTime.now(), Set.of(Status.APPROVED)).orElse(null);

            itemDto.setLastBooking(BookingMapper.toTinyDto(lastBooking));
            itemDto.setNextBooking(BookingMapper.toTinyDto(nextBooking));
        }

        Collection<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedAsc(itemId);
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toResponseDto)
                .collect(Collectors.toList()));

        return itemDto;
    }

    @Transactional
    public ItemDtoOut update(Long itemId, long ownerId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(ownerId); // ?
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
        return ItemMapper.toDto(updated);
    }

    public Collection<ItemDtoOut> getByOwner(long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    public Collection<ItemDtoOut> search(Long ownerId, String text) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User", ownerId));
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Collection<Item> itemList = itemRepository.findByNameContainingIgnoreCaseAndOwnerIdAndAvailableTrue(text, ownerId);
        return itemList.stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Transactional
    public CommentDtoOut addComment(long userId, @Min(1) Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item", itemId));

        Collection<Booking> userBookings = bookingRepository.findApprovedPastByBookerIdAndItemId(userId, itemId);
        if (userBookings.isEmpty()) {
            throw new ValidateException("User id:" + userId + " does not have past bookings of item id:" + itemId);
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return CommentMapper.toResponseDto(saved);
    }
}
