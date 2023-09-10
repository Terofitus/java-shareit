package ru.practicum.shareit.request.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ItemRequestMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    private final EasyRandom generator = new EasyRandom();

    @Test
    void test_addItemRequest_whenCalled_shouldSaveAndReturnItemRequest() {
        ItemRequestDto itemRequestDto = generator.nextObject(ItemRequestDto.class);
        User user = generator.nextObject(User.class);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);

        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(itemRequest);
        ItemRequestDto itemRequestDtoFromService = itemRequestService.addItemRequest(1, itemRequestDto);

        assertEquals(itemRequestDto, itemRequestDtoFromService);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .save(Mockito.any(ItemRequest.class));
    }

    @Test
    void test_getAllItemRequestsByOwnerId_shouldReturnListOfItemRequestsDto() {
        User user = generator.nextObject(User.class);
        ItemRequest itemRequest1 = generator.nextObject(ItemRequest.class);
        ItemRequest itemRequest2 = generator.nextObject(ItemRequest.class);
        List<ItemRequest> itemRequestList = List.of(itemRequest1, itemRequest2);
        List<ItemRequestDto> itemRequestDtoList = itemRequestList.stream().map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);
        Mockito.when(itemRequestRepository.getAllByRequestorId(Mockito.anyInt())).thenReturn(itemRequestList);
        List<ItemRequestDto> itemDtosFromService = itemRequestService.getAllItemRequestsByOwnerId(1);

        assertIterableEquals(itemRequestDtoList, itemDtosFromService);
    }

    @Test
    void test_getAllItemRequests_whenArgumentsCorrect_shouldReturnListOfItemRequestsDto() {
        User user = generator.nextObject(User.class);
        ItemRequest itemRequest1 = generator.nextObject(ItemRequest.class);
        ItemRequest itemRequest2 = generator.nextObject(ItemRequest.class);
        List<ItemRequest> itemRequestList = List.of(itemRequest1, itemRequest2);
        List<ItemRequestDto> itemRequestDtoList = itemRequestList.stream().map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);
        Mockito.when(itemRequestRepository.findAllWithoutOwnerRequests(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(itemRequestList);
        List<ItemRequestDto> itemDtosFromService = itemRequestService.getAllItemRequests(1, 0, 20);

        assertIterableEquals(itemRequestDtoList, itemDtosFromService);
    }

    @Test
    void test_getAllItemRequests_whenArgumentsIncorrect_shouldThrowException() {
        User user = generator.nextObject(User.class);

        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.getAllItemRequests(1, -1, -2));
        assertEquals("Аргумент from не может быть меньше size и 0, " +
                "аргумент size не может быть равен или меньше 0.", exception.getMessage());
        Mockito.verify(itemRequestRepository, Mockito.never()).findAllWithoutOwnerRequests(Mockito.anyInt(),
                Mockito.any(Pageable.class));
    }

    @Test
    void test_getItemRequest_whenItemRequestFounded_shouldReturnItemRequestDto() {
        User user = generator.nextObject(User.class);
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);
        Mockito.when(itemRequestRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(itemRequest));
        ItemRequestDto itemRequestDtoFromService = itemRequestService.getItemRequest(1, 1);

        assertEquals(itemRequestDto, itemRequestDtoFromService);
    }

    @Test
    void test_getItemRequest_whenItemRequestNotFound_shouldThrowException() {
        User user = generator.nextObject(User.class);

        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);
        Mockito.when(itemRequestRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequest(1, 1));
        assertEquals("Запрос предмета с id=1 не найден.", exception.getMessage());
    }
}