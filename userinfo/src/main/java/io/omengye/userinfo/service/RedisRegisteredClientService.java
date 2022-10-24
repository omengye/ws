package io.omengye.userinfo.service;

import io.omengye.userinfo.entity.ClientIdRegisteredClientEntity;
import io.omengye.userinfo.entity.IdRegisteredClientEntity;
import io.omengye.userinfo.repository.ClientIdRegistrationRepository;
import io.omengye.userinfo.repository.IdRegistrationRepository;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;

@Service
public class RedisRegisteredClientService implements RegisteredClientRepository {

    @Resource
    private IdRegistrationRepository idRegistrationRepository;

    @Resource
    private ClientIdRegistrationRepository clientIdRegistrationRepository;

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        assertUniqueIdentifiers(registeredClient);
        idRegistrationRepository.save(new IdRegisteredClientEntity(registeredClient.getId(), registeredClient));
        clientIdRegistrationRepository.save(new ClientIdRegisteredClientEntity(registeredClient.getClientId(), registeredClient));
    }

    @Nullable
    @Override
    public RegisteredClient findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        IdRegisteredClientEntity registeredClientEntity = idRegistrationRepository.findById(id).orElse(null);
        return registeredClientEntity == null ? null : registeredClientEntity.getRegisteredClient();
    }

    @Nullable
    @Override
    public RegisteredClient findByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");
        ClientIdRegisteredClientEntity registeredClientEntity = clientIdRegistrationRepository.findById(clientId).orElse(null);
        return registeredClientEntity == null ? null : registeredClientEntity.getRegisteredClient();
    }

    private void assertUniqueIdentifiers(RegisteredClient registeredClient) {
        if (idRegistrationRepository.existsById(registeredClient.getId())) {
            throw new IllegalArgumentException("Registered client must be unique. " +
                    "Found duplicate identifier: " + registeredClient.getId());
        }
        if (clientIdRegistrationRepository.existsById(registeredClient.getClientId())) {
            throw new IllegalArgumentException("Registered client must be unique. " +
                    "Found duplicate client identifier: " + registeredClient.getClientId());
        }
    }

}
