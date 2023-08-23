package ru.practicum.shareit.user.util;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static User toUserFromMap(Integer id, Map<String, Object> dataOfUser) {
        return new User(id,
                (String) dataOfUser.get("name"),
                (String) dataOfUser.get("email"));
    }
}
