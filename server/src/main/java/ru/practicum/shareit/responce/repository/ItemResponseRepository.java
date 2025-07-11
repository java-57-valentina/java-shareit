package ru.practicum.shareit.responce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.responce.model.ItemResponse;

import java.util.List;

public interface ItemResponseRepository extends JpaRepository<ItemResponse, Long> {

    @Query("SELECT r.item FROM ItemResponse r WHERE r.request.id = :requestId")
    List<Item> findItemsByRequestId(@Param("requestId") Long requestId);
}
