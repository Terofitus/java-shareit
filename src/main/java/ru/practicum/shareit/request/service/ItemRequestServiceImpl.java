package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ItemRequestMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    public ItemRequestDto addItemRequest(int userId, ItemRequestDto itemRequestDto) {
        User user = userService.getUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        ItemRequest itemRequestFromDb = itemRequestRepository.save(itemRequest);
        log.info("Добавлен запрос предмета, id запроса={}", itemRequestFromDb.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequestFromDb);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsByOwnerId(int userId) {
        userService.getUserById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.getAllByRequestorId(userId);
        log.info("Запрошены все запросы пользователя с id={}", userId);
        return itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(int userId, int from, int size) {
        userService.getUserById(userId);
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Аргумент from не может быть меньше size и 0, " +
                    "аргумент size не может быть равен или меньше 0.");
        }
        log.info("Запрошены все запросы предметов");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllWithoutOwnerRequests(userId,
                PageRequest.of(from / size, size, Sort.by("created").ascending()));
        return itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequest(int userId, int requestId) {
        userService.getUserById(userId);
        Optional<ItemRequest> itemRequestOpt = itemRequestRepository.findById(requestId);
        log.info("Запрошен запрос предмета с id={}", requestId);
        return ItemRequestMapper.toItemRequestDto(itemRequestOpt.orElseThrow(() -> new ItemRequestNotFoundException(
                String.format("Запрос предмета с id=%d не найден.", requestId))));
    }
}
