package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItemRequest(Integer userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getAllItemRequestsByOwnerId(Integer userId) {
        return get("", userId.longValue());
    }

    public ResponseEntity<Object> getAllItemRequests(Integer userId, Integer from, Integer size) {
        Map<String, String> map = new HashMap<>();
        map.put("from", String.valueOf(from));
        map.put("size", String.valueOf(size));
        return get("/all?from={from}&size={size}", userId.longValue(), map);
    }

    public ResponseEntity<Object> getItemRequests(Integer userId, Integer requestId) {
        return get("/" + requestId, userId.longValue());
    }
}
