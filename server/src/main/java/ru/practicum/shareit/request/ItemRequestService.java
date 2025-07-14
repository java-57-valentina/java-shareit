package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestExtDtoOut;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {

     private final UserRepository userRepository;
     private final ItemRequestRepository requestRepository;

    public ItemRequestDtoOut add(Long userId, ItemRequestDto requestDto) {
        log.info("try to add request: '{}' from user id:{}", requestDto.getDescription(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        ItemRequest request = new ItemRequest();
        request.setRequester(user);
        request.setDescription(requestDto.getDescription());
        request.setCreatedAt(LocalDateTime.now());

        ItemRequest saved = requestRepository.save(request);
        log.info("request saved: {}", saved);
        return RequestMapper.toResponseDto(saved);
    }

    public Collection<ItemRequestExtDtoOut> getByRequester(Long requesterId) {
        Collection<ItemRequest> list = requestRepository.findAllByRequesterIdOrderByCreatedAtDesc(requesterId);
        return list.stream().map(RequestMapper::toResponseExtDto).toList();
    }

    public Collection<ItemRequestDtoOut> getAll() {
        return requestRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(RequestMapper::toResponseDto)
                .toList();
    }

    public ItemRequestExtDtoOut getById(Long id) {
        ItemRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request", id));
        return RequestMapper.toResponseExtDto(request);
    }
}
