package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestExtDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemResponseRepository responseRepository;

    @InjectMocks
    private ItemRequestService requestService;

    private final User user = new User(1L, "user", "user@email.com");
    private final ItemRequest request = new ItemRequest(1L, "description", user, LocalDateTime.now(), List.of());
    private final Item item = new Item(1L, "item", "description", true, 2L);

    @Test
    void add_ShouldCreateRequest() {
        // Подготовка
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need item");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        // Выполнение
        ItemRequestDtoOut result = requestService.add(1L, requestDto);

        // Проверка
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("description");
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    void add_ShouldThrow_WhenUserNotFound() {
        // Подготовка
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need item");

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Проверка
        assertThatThrownBy(() -> requestService.add(1L, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User");
    }

    @Test
    void getByRequester_ShouldReturnRequests() {
        // Подготовка
        when(requestRepository.findAllByRequesterId(anyLong()))
                .thenReturn(List.of(request));

        // Выполнение
        Collection<ItemRequestExtDtoOut> result = requestService.getByRequester(1L);

        // Проверка
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(1L);
    }

    @Test
    void getByRequester_ShouldReturnEmptyList_WhenNoRequests() {
        // Подготовка
        when(requestRepository.findAllByRequesterId(anyLong()))
                .thenReturn(Collections.emptyList());

        // Выполнение
        Collection<ItemRequestExtDtoOut> result = requestService.getByRequester(1L);

        // Проверка
        assertThat(result).isEmpty();
    }

    @Test
    void getAll_ShouldReturnAllRequests() {
        // Подготовка
        when(requestRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(request));

        // Выполнение
        Collection<ItemRequestDtoOut> result = requestService.getAll();

        // Проверка
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(1L);
    }

    @Test
    void getById_ShouldReturnRequestWithItems() {
        // Подготовка
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(responseRepository.findItemsByRequestId(anyLong()))
                .thenReturn(List.of(item));

        // Выполнение
        ItemRequestExtDtoOut result = requestService.getById(1L);

        // Проверка
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().iterator().next().getId()).isEqualTo(1L);
    }

    @Test
    void getById_ShouldThrow_WhenRequestNotFound() {
        // Подготовка
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // Проверка
        assertThatThrownBy(() -> requestService.getById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item request");
    }

    @Test
    void getById_ShouldReturnEmptyItems_WhenNoResponses() {
        // Подготовка
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(responseRepository.findItemsByRequestId(anyLong()))
                .thenReturn(Collections.emptyList());

        // Выполнение
        ItemRequestExtDtoOut result = requestService.getById(1L);

        // Проверка
        assertThat(result.getItems()).isEmpty();
    }
}