package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.PageableCreator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp() {
        User user1 = new User(null, "A12D", "xcv_@mail.ru");
        User user2 = new User(null, "AasfD", "xcv12xbc@mail.ru");
        User user3 = new User(null, "AaqwesfD", "x12cv12xbc@mail.ru");
        testEntityManager.persist(user1);
        user1.setId(1);
        testEntityManager.persist(user2);
        user2.setId(2);
        testEntityManager.persist(user3);
        user3.setId(3);
        Item item1 = new Item(null, "zxcLL", "description", true, user1, null);
        Item item2 = new Item(null, "zxc11LL", "saription", true, user2, null);
        testEntityManager.persist(item1);
        item1.setId(1);
        testEntityManager.persist(item2);
        item2.setId(2);
        ItemRequest itemRequest1 = new ItemRequest(null, "asdasd", user3, LocalDateTime.now(), null);
        ItemRequest itemRequest2 = new ItemRequest(null, "a1dasd", user2, LocalDateTime.now(), null);
        ItemRequest itemRequest3 = new ItemRequest(null, "1sd", user2, LocalDateTime.now(), null);
        testEntityManager.persist(itemRequest1);
        testEntityManager.persist(itemRequest2);
        testEntityManager.persist(itemRequest3);
    }

    @Test
    void test_getAllByRequestorId() {
        List<ItemRequest> requests = itemRequestRepository.getAllByRequestorId(2);

        assertEquals(2, requests.size());
        assertEquals(2, requests.get(0).getId());
        assertEquals(3, requests.get(1).getId());
    }

    @Test
    void test_findAllWithoutOwnerRequests() {
        List<ItemRequest> requests = itemRequestRepository.findAllWithoutOwnerRequests(2,
                PageableCreator.toPageable(0, 20, Sort.by("created").ascending()));

        assertEquals(1, requests.size());
        assertEquals(1, requests.get(0).getId());
    }
}