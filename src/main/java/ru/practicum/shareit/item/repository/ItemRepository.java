package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerId(Long ownerId);
    
    Collection<Item> findByNameContainingIgnoreCaseAndOwnerIdAndAvailableTrue(String nameSearch, Long ownerId);
}
