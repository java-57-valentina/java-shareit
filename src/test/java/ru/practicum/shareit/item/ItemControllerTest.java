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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private final Item item = new Item(1L, "Hummer", "Description", true, 2L);

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @SneakyThrows
    void add() {
        ItemDto itemDto = new ItemDto(null, null, "Hummer", "Description", true);
        Long userId = 2L;

        Item item = ItemMapper.toItem(itemDto);
        item.setId(1L);
        item.setOwnerId(userId);

        ItemDtoOut itemDtoOut = ItemMapper.toResponceDto(item);

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
        ItemDtoOut itemDtoOut = ItemMapper.toResponceDto(item);

        when(itemService.getByOwner(anyLong()))
                .thenReturn(Collections.singletonList(itemDtoOut));

        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, String.valueOf(itemDtoOut.getOwnerId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDtoOut.getId()));
    }

    @Test
    void getById() {
    }

    @Test
    void update() {
    }

    @Test
    void searchItems() {
    }

    @Test
    void addComment() {
    }

    @Test
    void testAdd() {
    }

    @Test
    void testGetAll() {
    }

    @Test
    void testGetById() {
    }

    @Test
    void testUpdate() {
    }

    @Test
    void testSearchItems() {
    }

    @Test
    void testAddComment() {
    }
}