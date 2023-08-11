package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.NoAccessRightsException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Item getItemById(int id) {
        return itemRepository.getItemById(id);
    }

    @Override
    public List<Item> getAllItemsOfUser(int userId) {
        return itemRepository.getAllItemsOfUser(userId);
    }

    @Override
    public Item addItem(int userId, Item item) {
        User owner = userRepository.getUserById(userId);
        item.setOwner(owner);
        return itemRepository.addItem(item);
    }

    @Override
    public Item updateItem(Item item, int userId) {
        Item itemFromRepository = itemRepository.getItemById(item.getId());
        User user = userRepository.getUserById(userId);
        checkingAccessRights(itemFromRepository, user);

        if (item.getName() != null) {
            itemFromRepository.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemFromRepository.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemFromRepository.setAvailable(item.getAvailable());
        }
        itemRepository.updateItem(itemFromRepository);
        return itemFromRepository;
    }

    @Override
    public void deleteAllItemsOfUser(int userId) {
        itemRepository.deleteAllItemsOfUser(userId);
    }

    @Override
    public void deleteItemById(int userId, int itemId) {
        Item item = itemRepository.getItemById(itemId);
        User user = userRepository.getUserById(userId);
        checkingAccessRights(item, user);
        itemRepository.deleteItemById(itemId);
    }

    private void checkingAccessRights(Item item, User user) {
        if (user.getId() != item.getOwner().getId()) {
            throw new NoAccessRightsException("Только владелец может изменять предмет.");
        }
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.searchItems(text);
    }
}
