package io.omengye.userinfo.repository;

import io.omengye.userinfo.entity.OAuth2AuthorizationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuth2AuthorizationRepository extends CrudRepository<OAuth2AuthorizationEntity, String> {
}
