package io.omengye.userinfo.repository;

import io.omengye.userinfo.entity.IdRegisteredClientEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdRegistrationRepository extends CrudRepository<IdRegisteredClientEntity, String> {
}
