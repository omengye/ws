package io.omengye.userinfo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.omengye.userinfo.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, String> {}
