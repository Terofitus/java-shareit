package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public User toUserFromMap(Integer id, Map<String, Object> dataOfUser) {
        return new User(id,
                (String) dataOfUser.get("name"),
                (String) dataOfUser.get("email"));
    }
}
