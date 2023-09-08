package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Integer> {
    @Query("select i from Item i where i.available=true and (lower(i.name) like lower(concat('%', :text, '%'))" +
            " or lower(i.description) like lower(concat('%', :text, '%')))")
    List<Item> findAllByNameOrDescriptionContainsIgnoreCase(String text, Pageable pageable);

    List<Item> findAllByOwner(User user, Pageable pageable);

    void deleteAllByOwnerId(Integer userId);
}
