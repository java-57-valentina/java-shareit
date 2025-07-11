package ru.practicum.shareit.request;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestExtDtoOut;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.responce.repository.ItemResponseRepository;
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
     private final ItemResponseRepository responseRepository;

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
        Collection<ItemRequest> list = requestRepository.findAllByRequesterId(requesterId);
        return list.stream().map(RequestMapper::toResponseExtDto).toList();
    }

    public Collection<ItemRequestDtoOut> getAll() {
        return requestRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(RequestMapper::toResponseDto)
                .toList();
    }

    public ItemRequestExtDtoOut getById(@Min(1) Long id) {
        ItemRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item request", id));
        ItemRequestExtDtoOut responseExtDto = RequestMapper.toResponseExtDto(request);
        Collection<Item> items = responseRepository.findItemsByRequestId(request.getId());
        responseExtDto.setItems(items.stream().map(ItemMapper::toTinyDto).toList());
        return responseExtDto;
    }
}
