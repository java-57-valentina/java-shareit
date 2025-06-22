package ru.practicum.shareit.item.dao;

import jakarta.validation.constraints.Min;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item add(Item item);

    Collection<Item> getAll();

    Item getById(@Min(1) Long itemId);

    Item update(Item item);

    Collection<Item> getByOwner(long ownerId);

    Collection<Item> search(String text, Long ownerId);
}
