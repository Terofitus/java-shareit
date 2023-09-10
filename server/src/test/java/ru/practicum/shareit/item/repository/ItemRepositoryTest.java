package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user1 = new User(null, "A12D", "xcv_@mail.ru");
        User user2 = new User(null, "AasfD", "xcv12xbc@mail.ru");
        testEntityManager.persist(user1);
        user1.setId(1);
        testEntityManager.persist(user2);
        user2.setId(2);
        Item item1 = new Item(null, "zxcLL", "description", true, user1, null);
        Item item2 = new Item(null, "zxc11LL", "saription", true, user2, null);
        testEntityManager.persist(item1);
        item1.setId(1);
        testEntityManager.persist(item2);
        item2.setId(2);
    }

    @Test
    void test_findAllByNameOrDescriptionContainsIgnoreCase_shouldReturnItemWithNameOrDescriptionContainText() {
        List<Item> items = itemRepository.findAllByNameOrDescriptionContainsIgnoreCase("zxc",
                PageRequest.of(0 / 20, 20, Sort.unsorted()));

        assertEquals(items.size(), 2);
    }

    @Test
    void test_findAllByOwner_whenItemsExist_shouldReturnListOfItem() {
        User user = userRepository.findById(1).get();
        List<Item> items = itemRepository.findAllByOwner(user, PageRequest.of(0 / 20, 20,
                Sort.unsorted()));

        assertEquals(items.size(), 1);
    }

    @Test
    void test_deleteAllByOwnerId_whenItemsExist_shouldDeleteItemsOfUser() {
        User user = userRepository.findById(1).get();
        List<Item> items = itemRepository.findAllByOwner(user, PageRequest.of(0 / 20, 20, Sort.unsorted()));
        assertEquals(items.size(), 1);

        itemRepository.deleteAllByOwnerId(1);

        List<Item> items2 = itemRepository.findAllByOwner(user, PageRequest.of(0 / 20, 20, Sort.unsorted()));
        assertEquals(items2.size(), 0);
    }
}