package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.TinyBookingDtoOut;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.X_SHARER_USER_ID;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private final Item item = new Item(1L, "Hummer", "Description", true, 2L, null);

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @SneakyThrows
    void add() {
        ItemDto itemDto = new ItemDto(null, "Hummer", "Description", true);
        Long userId = 2L;

        Item item = ItemMapper.toItem(itemDto);
        item.setId(1L);
        item.setOwnerId(userId);

        ItemDtoOut itemDtoOut = ItemMapper.toDto(item);

        when(itemService.add(any(), anyLong()))
                .thenReturn(itemDtoOut);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, String.valueOf(userId))
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(itemDtoOut.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoOut.getDescription()))
                .andExpect(jsonPath("$.ownerId").value(itemDtoOut.getOwnerId()))
                .andExpect(jsonPath("$.available").value(itemDtoOut.getAvailable()));
    }

    @Test
    @SneakyThrows
    void getAll() {
        ItemDtoOut itemDtoExtOut = ItemMapper.toDto(item);

        when(itemService.getByOwner(anyLong()))
                .thenReturn(Collections.singletonList(itemDtoExtOut));

        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, String.valueOf(itemDtoExtOut.getOwnerId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDtoExtOut.getId()));
    }

    @Test
    void getAllItemsWithoutOwner() throws Exception {
        ItemDtoOut itemDtoOut = ItemMapper.toDto(item);

        when(itemService.getAll())
                .thenReturn(Collections.singletonList(itemDtoOut));

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDtoOut.getId()));
    }


    @Test
    @SneakyThrows
    void getById() {
        ItemDtoExtOut itemDtoExtOut = ItemMapper.toExtDto(item);

        TinyBookingDtoOut bookingDto = new TinyBookingDtoOut();
        bookingDto.setId(1L);
        bookingDto.setBookerId(2L);
        bookingDto.setItemId(itemDtoExtOut.getId());
        bookingDto.setStatus(Status.APPROVED);
        bookingDto.setStart(LocalDateTime.of(2025,1,1, 12, 0, 0));
        bookingDto.setEnd(LocalDateTime.of(2025,1,2, 12, 0, 0));
        itemDtoExtOut.setLastBooking(bookingDto);

        when(itemService.getById(anyLong(), any())).thenReturn(itemDtoExtOut);

        mockMvc.perform(get("/items/1")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoExtOut.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoExtOut.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoExtOut.getDescription()))
                .andExpect(jsonPath("$.ownerId").value(itemDtoExtOut.getOwnerId()))
                .andExpect(jsonPath("$.available").value(itemDtoExtOut.getAvailable()))
                // Проверка наличия и полей lastBooking
                .andExpect(jsonPath("$.lastBooking").exists())
                .andExpect(jsonPath("$.lastBooking.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.lastBooking.bookerId").value(bookingDto.getBookerId()))
                .andExpect(jsonPath("$.lastBooking.itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.lastBooking.status").value(bookingDto.getStatus().name()))
                .andExpect(jsonPath("$.lastBooking.start").exists())
                .andExpect(jsonPath("$.lastBooking.end").exists());
    }

    @Test
    @SneakyThrows
    void update() {
        ItemDto itemDto = new ItemDto(null, "Hummer", "Description", true);
        itemDto.setName("Updated");

        Item item = ItemMapper.toItem(itemDto);
        item.setId(1L);

        ItemDtoOut itemDtoOut = ItemMapper.toDto(item);

        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(itemDtoOut);

        mockMvc.perform(patch("/items/1")
                        .header(X_SHARER_USER_ID, String.valueOf(1L))
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @SneakyThrows
    void searchItems() {
        ItemDtoOut itemDtoOut = ItemMapper.toDto(item);
        String searchText = "Hum";

        when(itemService.search(anyLong(), anyString()))
                .thenReturn(Collections.singletonList(itemDtoOut));

        mockMvc.perform(get("/items/search")
                        .header(X_SHARER_USER_ID, "1")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDtoOut.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDtoOut.getName()));
    }

    @Test
    @SneakyThrows
    void addComment() {
        CommentDto commentRequest = new CommentDto("Great item!");
        Long userId = 1L;
        Long itemId = 1L;

        LocalDateTime created = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
        CommentDtoOut commentDtoOut = CommentDtoOut.builder()
            .id(1L)
            .text("Great item!")
            .created(created)
            .itemId(3L)
            .authorName("test")
            .build();

        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDtoOut);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(X_SHARER_USER_ID, String.valueOf(userId))
                        .content(mapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDtoOut.getId()))
                .andExpect(jsonPath("$.text").value(commentDtoOut.getText()))
                .andExpect(jsonPath("$.itemId").value(commentDtoOut.getItemId()))
                .andExpect(jsonPath("$.authorName").value(commentDtoOut.getAuthorName()))
                .andExpect(jsonPath("$.created").value(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }
}