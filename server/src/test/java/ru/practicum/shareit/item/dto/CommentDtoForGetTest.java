package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoForGetTest {
    @Autowired
    private JacksonTester<CommentDtoForGet> jacksonTester;

    @Test
    void test_dtoColumn() throws IOException {
        CommentDtoForGet dto = new CommentDtoForGet(1, "asdasd", "asdasdasd", LocalDateTime.now());

        JsonContent<CommentDtoForGet> result = jacksonTester.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");
    }

}