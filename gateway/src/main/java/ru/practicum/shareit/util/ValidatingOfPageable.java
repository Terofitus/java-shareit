package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidatingOfPageable {
    public void checkParams(Integer from, Integer size) {
        if ((from == null || size == null) || (from < 0 || size <= 0)) {
            throw new IllegalArgumentException("Аргумент from не может быть меньше size и 0, " +
                    "аргумент size не может быть равен или меньше 0.");
        }
    }
}
