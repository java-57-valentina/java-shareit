package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.item.dto.TinyItemDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestExtDtoOut;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.X_SHARER_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService requestService;

    @Test
    @SneakyThrows
    void getAll() {
        // Подготовка тестовых данных
        LocalDateTime now = LocalDateTime.now();
        ItemRequestDtoOut request1 = new ItemRequestDtoOut(1L, "Need item 1", 2L, now);
        ItemRequestDtoOut request2 = new ItemRequestDtoOut(2L, "Need item 2", 3L, now.minusHours(1));

        List<ItemRequestDtoOut> requests = List.of(request1, request2);

        // Настройка мока
        when(requestService.getAll()).thenReturn(requests);

        // Выполнение запроса и проверки
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Need item 1"))
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Need item 2"));
    }

    @Test
    @SneakyThrows
    void getByRequester() {
        // Подготовка тестовых данных
        Long requesterId = 1L;
        LocalDateTime now = LocalDateTime.now();

        ItemRequestExtDtoOut request1 = new ItemRequestExtDtoOut();
        request1.setId(1L);
        request1.setDescription("First request");
        request1.setCreatedAt(now);
        request1.setItems(Collections.emptyList());

        ItemRequestExtDtoOut request2 = new ItemRequestExtDtoOut();
        request2.setId(2L);
        request2.setDescription("Second request");
        request2.setCreatedAt(now.minusHours(1));
        request2.setItems(Collections.emptyList());

        List<ItemRequestExtDtoOut> requests = List.of(request1, request2);

        // Настройка мока
        when(requestService.getByRequester(requesterId)).thenReturn(requests);

        // Выполнение запроса и проверки
        mockMvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("First request"))
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Second request"));
    }

    @Test
    @SneakyThrows
    void getById() {
        // Подготовка тестовых данных
        Long requestId = 1L;
        LocalDateTime now = LocalDateTime.now();

        ItemRequestExtDtoOut request = new ItemRequestExtDtoOut();
        request.setId(requestId);
        request.setDescription("Test request");
        request.setCreatedAt(now);

        TinyItemDtoOut item = TinyItemDtoOut.builder()
                .id(1L)
                .name("Test item")
                .ownerId(3L)
                .build();

        request.setItems(List.of(item));

        when(requestService.getById(requestId)).thenReturn(request);

        // Выполнение запроса и проверки
        mockMvc.perform(get("/requests/{id}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Test request"))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.items[0].id").value(1L))
                .andExpect(jsonPath("$.items[0].name").value("Test item"))
                .andExpect(jsonPath("$.items[0].ownerId").value(3L));
    }


    @Test
    @SneakyThrows
    void add_ShouldReturnNonEmptyBody() {
        // Подготовка тестовых данных
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need new item");

        ItemRequestDtoOut expectedDto = new ItemRequestDtoOut(1L, "Need new item", userId, LocalDateTime.now());

        // 1. Проверка сериализации DTO
        String expectedJson = mapper.writeValueAsString(expectedDto);
        System.out.println("Ожидаемый JSON: " + expectedJson); // Диагностика

        // 2. Настройка мока сервиса
        when(requestService.add(eq(userId), any(ItemRequestDto.class)))
                .thenReturn(expectedDto);

        // 3. Выполнение запроса с полным логгированием
        MvcResult result = mockMvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, userId.toString())
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) // Важно для диагностики!
                .andExpect(status().isOk())
                .andReturn();

        // 4. Анализ ответа
        MockHttpServletResponse response = result.getResponse();
        String responseBody = response.getContentAsString();

        System.out.println("Actual Content-Type: " + response.getContentType());
        System.out.println("Actual Body: " + responseBody);

        // 5. Жёсткие проверки
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(responseBody).isNotBlank();

        // 6. Проверка структуры JSON
        JsonNode jsonNode = mapper.readTree(responseBody);
        assertThat(jsonNode.has("id")).isTrue();
        assertThat(jsonNode.has("description")).isTrue();
    }

    @Test
    @SneakyThrows
    void add() {
        // Подготовка тестовых данных
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need new item");

        ItemRequestDtoOut expectedDto = new ItemRequestDtoOut(1L, "Need new item", userId, LocalDateTime.now());

        // 2. Проверка сериализации DTO
        String expectedJson = mapper.writeValueAsString(expectedDto);
        System.out.println("Expected JSON: " + expectedJson);

        // Настройка мока
        when(requestService.add(eq(userId), any(ItemRequestDto.class)))
                .thenReturn(expectedDto);

        // Выполнение запроса и проверки
        mockMvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, userId.toString())
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need new item"))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.created").exists());
    }
}