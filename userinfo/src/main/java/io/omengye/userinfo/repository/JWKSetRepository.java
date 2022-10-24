package io.omengye.userinfo.repository;

import io.omengye.userinfo.entity.JWKSetEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JWKSetRepository extends CrudRepository<JWKSetEntity, String> {
}
