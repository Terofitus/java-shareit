package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.CommentDtoForCreate;
import ru.practicum.shareit.dto.ItemDtoWithoutBooking;
import ru.practicum.shareit.util.ValidatingOfPageable;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Integer id,
                                              @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        return itemClient.getItemById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @RequestParam(required = false, defaultValue = "0",
                                                            value = "from") Integer from,
                                                    @RequestParam(required = false, defaultValue = "20",
                                                            value = "size") Integer size) {
        ValidatingOfPageable.checkParams(from, size);
        return itemClient.getAllItemsOfUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByDescription(@RequestParam(required = false, value = "text") String text,
                                                           @RequestParam(required = false, defaultValue = "0",
                                                                   value = "from") Integer from,
                                                           @RequestParam(required = false, defaultValue = "20",
                                                                   value = "size") Integer size) {
        if (text == null || text.isEmpty()) return ResponseEntity.ok().body(Collections.emptyList());
        ValidatingOfPageable.checkParams(from, size);
        return itemClient.searchItemsByDescription(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @RequestBody @Valid ItemDtoWithoutBooking itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                   @RequestBody @Valid CommentDtoForCreate comment,
                                                   @PathVariable Integer itemId) {
        return itemClient.addCommentToItem(userId, comment, itemId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable(name = "id") Integer itemId,
                                             @RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestBody Map<String, Object> dataOfItem) {
        return itemClient.updateItem(itemId, userId, dataOfItem);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.deleteAllItemsOfUser(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItemById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                 @PathVariable(name = "id") Integer itemId) {
        return itemClient.deleteItemById(userId, itemId);
    }
}
