package io.omengye.gateway.configure;

import io.omengye.common.utils.Utils;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component("remoteAddrKeyResolver")
public class RemoteAddrKeyResolver implements KeyResolver {

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
    	return Mono.just(Utils.getRealIP(exchange.getRequest()));
    }

}
