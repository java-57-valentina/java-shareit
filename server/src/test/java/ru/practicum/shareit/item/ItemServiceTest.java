package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.*;
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
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemResponseRepository responseRepository;
    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemService itemService;

    private ItemDto itemDto;
    private Item item;
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john@example.com");
        itemDto = new ItemDto(1L, "Item 1", "Description", true);

        item = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description")
                .available(true)
                .ownerId(1L)
                .build();
        itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setId(1L);
        itemRequest.setCreatedAt(LocalDateTime.now());
        itemRequest.setDescription("Need item");
    }

    // ==================== add() ====================
    @Test
    void add_ShouldSaveItemAndReturnDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemDtoOut result = itemService.add(itemDto, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(itemDto.getName());
        verify(itemRepository).save(any(Item.class));
        verify(responseRepository).save(any(ItemResponse.class));
    }

    @Test
    void add_ShouldThrowNotFoundException_WhenUserNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.add(itemDto, 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User", 999L);

        verify(itemRepository, never()).save(any(Item.class));
    }

    // ==================== getById() ====================
    @Test
    void getById_ShouldReturnItemWithBookingsAndComments_WhenOwner() {

        User booker = new User();
        booker.setId(2L);
        booker.setName("Booker");

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setItem(item);  // Важно установить item!
        lastBooking.setBooker(booker);
        lastBooking.setStatus(Status.APPROVED);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setItem(item);  // Важно установить item!
        nextBooking.setBooker(booker);
        nextBooking.setStatus(Status.APPROVED);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(anyLong(), any(), anySet()))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(anyLong(), any(), anySet()))
                .thenReturn(Optional.of(nextBooking));
        when(commentRepository.findAllByItemIdOrderByCreatedAsc(anyLong()))
                .thenReturn(Collections.emptyList());

        ItemDtoExtOut result = itemService.getById(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();
        assertThat(result.getComments()).isEmpty();
    }

    @Test
    void getById_ShouldReturnItemWithoutBookings_WhenNotOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemIdOrderByCreatedAsc(anyLong()))
                .thenReturn(Collections.emptyList());

        ItemDtoExtOut result = itemService.getById(1L, 2L); // userId != ownerId

        assertThat(result).isNotNull();
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenItemNotExists() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getById(999L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item", 999L);
    }

    // ==================== update() ====================
    @Test
    void update_ShouldUpdateItemFields() {
        ItemDto updateDto = new ItemDto(null, "Updated Name", "Updated Description", false);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDtoOut result = itemService.update(1L, 1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(item.getName()).isEqualTo("Updated Name");
        assertThat(item.getDescription()).isEqualTo("Updated Description");
        assertThat(item.getAvailable()).isFalse();
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenUserNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(1L, 999L, itemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User", 999L);
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenItemNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(999L, 1L, itemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item", 999L);
    }

    // ==================== getByOwner() ====================
    @Test
    void getByOwner_ShouldReturnListOfItems() {
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(List.of(item));

        Collection<ItemDtoOut> result = itemService.getByOwner(1L);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getName()).isEqualTo(item.getName());
    }

    // ==================== search() ====================
    @Test
    void search_ShouldReturnEmptyList_WhenTextIsBlank() {
        User mockUser = new User();
        mockUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        Collection<ItemDtoOut> result = itemService.search(1L, "   ");

        assertThat(result).isEmpty();
    }

    @Test
    void search_ShouldReturnMatchingItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByNameContainingIgnoreCaseAndOwnerIdAndAvailableTrue(anyString(), anyLong()))
                .thenReturn(List.of(item));

        Collection<ItemDtoOut> result = itemService.search(1L, "item");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getName()).isEqualTo(item.getName());
    }

    // ==================== addComment() ====================
    @Test
    void addComment_ShouldSaveComment() {
        CommentDto commentDto = new CommentDto("Great item!");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findApprovedPastByBookerIdAndItemId(anyLong(), anyLong()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment(1L, "Great item!", item, user, LocalDateTime.now()));

        CommentDtoOut result = itemService.addComment(1L, 1L, commentDto);

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("Great item!");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrowValidateException_WhenNoBookings() {
        CommentDto commentDto = new CommentDto("Great item!");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findApprovedPastByBookerIdAndItemId(anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> itemService.addComment(1L, 1L, commentDto))
                .isInstanceOf(ValidateException.class)
                .hasMessageContaining("does not have past bookings");
    }
}