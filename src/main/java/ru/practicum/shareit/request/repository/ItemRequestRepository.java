package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    // Collection<ItemRequest> findAllByRequesterIdOrderByCreatedAtDesc(Long userId);

    // проверить, ленивая тут загрузка или нет
    @Query("SELECT DISTINCT r FROM ItemRequest r LEFT JOIN FETCH r.responses WHERE r.requester.id = :requesterId ORDER BY r.createdAt DESC")
    Collection<ItemRequest> findAllByRequesterId(@Param("requesterId") Long requesterId);
}
