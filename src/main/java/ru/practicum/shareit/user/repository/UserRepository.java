package ru.practicum.shareit.user.repository;


import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User,Integer> {
}
