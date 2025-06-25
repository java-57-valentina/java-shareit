package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerNotMatchException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemStorageInMemory implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private Long generatedId = 0L;

    @Override
    public Item add(Item item) {
        item.setId(++generatedId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> getAll() {
        return items.values();
    }

    @Override
    public Item getById(Long itemId) {
        Item item = items.get(itemId);
        if (item == null)
            throw new NotFoundException("Item", itemId);
        return item;
    }

    @Override
    public Item update(Item item) {
        Item origin = getById(item.getId());

        if (!item.getOwnerId().equals(origin.getOwnerId()))
            throw new OwnerNotMatchException("Cannot change owner of item");

        if (item.getName() != null && !item.getName().equals(origin.getName()))
            origin.setName(item.getName());

        if (item.getDescription() != null && !item.getDescription().equals(origin.getDescription()))
            origin.setDescription(item.getDescription());

        if (item.getAvailable() != null)
            origin.setAvailable(item.getAvailable());

        return origin;
    }

    @Override
    public Collection<Item> getByOwner(long ownerId) {
        return items.values().stream()
                .filter(i -> i.getOwnerId().equals(ownerId))
                .toList();
    }

    @Override
    public Collection<Item> search(String text, Long ownerId) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(i -> i.getOwnerId().equals(ownerId))
                .filter(i -> i.getName().equalsIgnoreCase(text))
                .toList();
    }
}
