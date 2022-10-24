package io.omengye.userinfo.repository;

import io.omengye.userinfo.entity.ClientIdRegisteredClientEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientIdRegistrationRepository extends CrudRepository<ClientIdRegisteredClientEntity, String> {
}
