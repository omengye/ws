package io.omengye.gcs.service;

import io.netty.channel.ChannelOption;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.omengye.gcs.configure.GSearchItems;
import io.omengye.gcs.entity.GCEntity;
import io.omengye.gcs.entity.GSearchItem;
import io.omengye.gcs.entity.ItemEntity;
import io.omengye.gcs.utils.RangeRandom;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import reactor.netty.transport.ProxyProvider;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Log4j2
@Service
public class ChooseItemService {

    private List<GSearchItem> gItems = new ArrayList<>();

    private static final ConcurrentHashMap<Integer, GSearchItem> G_ERROR_ITEMS = new ConcurrentHashMap<>();

    private static final RangeRandom rangeRandom = RangeRandom.getInstance();

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    @Resource
    private GSearchItems gsItems;

    public void init() {
        this.gItems = gsItems.getItems();
    }

    public GSearchItem getItem() {
        rwLock.readLock().lock();
        if (gItems.isEmpty()) {
            return null;
        }
        int idx = rangeRandom.rand(gItems.size());
        rwLock.readLock().unlock();
        return gItems.get(idx);
    }

    public GSearchItem removeAndGetItem(GSearchItem item) {
        rwLock.writeLock().lock();
        boolean flag = gItems.remove(item);
        if (flag && !G_ERROR_ITEMS.containsKey(item.getIndex())) {
            G_ERROR_ITEMS.put(item.getIndex(), item);
        }
        rwLock.writeLock().unlock();
        return getItem();
    }

    private void rebutItem(GSearchItem item) {
        rwLock.writeLock().lock();
        if (gItems.contains(item)) {
            return;
        }
        gItems.add(item);
        rwLock.writeLock().unlock();
        G_ERROR_ITEMS.remove(item.getIndex());
    }


    @Scheduled(cron = "0 0/30 0 * * ?")
    public void loopErrorItems() {
        for (Map.Entry<Integer, GSearchItem> entity : G_ERROR_ITEMS.entrySet()) {

            String reqUrl = "https://www.googleapis.com/customsearch/v1?key="
                    +entity.getValue().getKey()+"&cx="+entity.getValue().getCx()+"&q=test&start=1&num=5";

            Mono<GCEntity> response = getResponse(reqUrl, "", new GCEntity(), GCEntity.class);
            List<ItemEntity> itemEntities = Objects.requireNonNull(response.block(Duration.ofMillis(1000))).getItems();
            if (itemEntities != null && !itemEntities.isEmpty()) {
                rebutItem(entity.getValue());
            }
        }
    }



    public <T> Mono<T> getResponse(String reqUrl, String authHeader, T obj, Class<T> clazz) {
        return getResponse(reqUrl, authHeader, obj, clazz, null);
    }

    public <T> Mono<T> getResponse(String reqUrl, String authHeader, T obj, Class<T> clazz, GSearchItem item) {
        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .option(ChannelOption.SO_KEEPALIVE, true)
//                .proxy(spec -> spec.type(ProxyProvider.Proxy.SOCKS5)
//                        .host("127.0.0.1")
//                        .port(11032)
//                        .nonProxyHosts("localhost,127.0.0.1,192.168.*"))
                .doOnConnected(connection ->
                        connection
                                .addHandlerLast(new ReadTimeoutHandler(2))
                                .addHandlerLast(new WriteTimeoutHandler(2))
                );

        WebClient.Builder webBuilder = WebClient.builder();
        if (!StringUtils.isEmpty(authHeader)) {
            webBuilder.defaultHeader("Authorization", authHeader);
        }

        WebClient webClient = webBuilder
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();

        return webClient
                .get()
                .uri(reqUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, resp -> {
                    log.error("error:{},msg:{}",resp.statusCode().value(),resp.statusCode().getReasonPhrase());
                    if (item!=null && resp.statusCode().equals(HttpStatus.FORBIDDEN)) {
                        removeAndGetItem(item);
                    }
                    return Mono.error(new RuntimeException(resp.statusCode().value() + " : " + resp.statusCode().getReasonPhrase()));
                })
                .bodyToMono(clazz)
                .doOnError(WebClientResponseException.class, err -> {
                    log.info("ERROR status:{},msg:{}",err.getRawStatusCode(),err.getResponseBodyAsString());
                    throw new RuntimeException(err.getMessage());
                })
                .doOnError(ConnectTimeoutException.class, err -> {
                    log.error(err.getMessage());
                    throw new RuntimeException(err.getMessage());
                })
                .onErrorReturn(obj);
    }

}
