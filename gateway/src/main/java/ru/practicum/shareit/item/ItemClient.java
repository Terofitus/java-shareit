package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.CommentDtoForCreate;
import ru.practicum.dto.ItemDtoWithoutBooking;
import ru.practicum.shareit.client.BaseClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItemById(int id, Integer userId) {
        return get("/" + id, Long.valueOf(userId));
    }

    public ResponseEntity<Object> getAllItemsOfUser(Integer userId, Integer from, Integer size) {
        Map<String, String> map = new HashMap<>();
        map.put("from", String.valueOf(from));
        map.put("size", String.valueOf(size));
        return get("?from={from}&size={size}", Long.valueOf(userId), map);
    }

    public ResponseEntity<Object> searchItemsByDescription(String text, Integer from, Integer size) {
        Map<String, String> map = Map.of(
                "text", text,
                "from", String.valueOf(from),
                "size", String.valueOf(size)
        );
        return get("/search?text={text}&from={from}&size={size}", null, map);
    }

    public ResponseEntity<Object> addItem(Integer userId, ItemDtoWithoutBooking itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> addCommentToItem(Integer userId, CommentDtoForCreate comment, Integer itemId) {
        return post("/" + itemId + "/comment", userId, comment);
    }

    public ResponseEntity<Object> updateItem(Integer itemId, Integer userId, Map<String, Object> dataOfItem) {
        return patch("/" + itemId, userId, dataOfItem);
    }

    public ResponseEntity<Object> deleteAllItemsOfUser(Integer userId) {
        return delete("", userId);
    }

    public ResponseEntity<Object> deleteItemById(Integer userId, Integer itemId) {
        return delete("/" + itemId, userId);
    }
}
