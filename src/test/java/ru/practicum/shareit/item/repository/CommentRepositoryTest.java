package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private CommentRepository commentRepository;

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
        Booking booking1 = new Booking(null,
                LocalDateTime.of(2012, 1, 1, 5, 5, 5),
                LocalDateTime.of(2013, 1, 1, 5, 5, 5),
                item1, user2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null,
                LocalDateTime.of(2010, 1, 1, 5, 5, 5),
                LocalDateTime.of(2011, 1, 1, 5, 5, 5),
                item1, user3, BookingStatus.APPROVED);
        testEntityManager.persist(booking1);
        testEntityManager.persist(booking2);
        testEntityManager.persist(new Comment(null, "Adasdzxzz", item1, user2, LocalDateTime.now()));
        testEntityManager.persist(new Comment(null, "asdasdzxzz", item1, user3, LocalDateTime.now()));
    }

    @Test
    void test_findAllByItemId_shouldReturnListOfComments() {
        List<Comment> comments = commentRepository.findAllByItemId(1);

        assertEquals(comments.size(), 2);
    }
}