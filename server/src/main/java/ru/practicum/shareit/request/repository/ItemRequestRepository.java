package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findAllByRequesterIdOrderByCreatedAtDesc(@Param("requesterId") Long requesterId);
}
