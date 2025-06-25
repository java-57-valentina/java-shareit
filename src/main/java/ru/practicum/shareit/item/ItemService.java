package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto add(ItemDto itemDto, long userId) {
        userStorage.getUser(userId); // check exists
        Item item = ItemMapper.mapToItem(itemDto);
        item.setOwnerId(userId);
        Item added = itemStorage.add(item);
        return ItemMapper.mapToItemDto(added);
    }

    public Collection<ItemDto> getAll() {
        return itemStorage.getAll().stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto getById(Long itemId) {
        return ItemMapper.mapToItemDto(itemStorage.getById(itemId));
    }

    public ItemDto update(Long itemId, long ownerId, ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(itemDto);
        item.setId(itemId);
        item.setOwnerId(ownerId);
        Item updated = itemStorage.update(item);
        return ItemMapper.mapToItemDto(updated);
    }

    public Collection<ItemDto> getByOwner(long ownerId) {
        return itemStorage.getByOwner(ownerId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public Collection<ItemDto> search(Long ownerId, String text) {
        userStorage.getUser(ownerId); // check exists
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Collection<Item> itemList = itemStorage.search(text, ownerId);
        return itemList.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
