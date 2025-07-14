package ru.practicum.shareit.responce.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.TinyItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class ItemResponseMapperTest {

    @Test
    void mapToTinyItemDtoOut_ShouldMapCorrectly() {
        // Подготовка тестовых данных
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        Item item = new Item();
        item.setId(10L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());

        ItemResponse response = new ItemResponse();
        response.setItem(item);

        // Выполнение
        TinyItemDtoOut result = ItemResponseMapper.mapToTinyItemDtoOut(response);

        // Проверка
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getOwnerId()).isEqualTo(1L);
    }
}