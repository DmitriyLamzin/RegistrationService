package com.github.dmitriylamzin.repository;

import com.github.dmitriylamzin.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
