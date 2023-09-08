package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PageableCreator {
    public Pageable toPageable(int from, int size, Sort sort) {
        if (sort == null) sort = Sort.unsorted();
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Аргумент from не может быть меньше size и 0, " +
                    "аргумент size не может быть равен или меньше 0.");
        }
        return PageRequest.of(from / size, size, sort);
    }
}
