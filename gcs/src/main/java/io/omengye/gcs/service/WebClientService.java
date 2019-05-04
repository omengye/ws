package io.omengye.gcs.service;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import io.netty.channel.ConnectTimeoutException;
import io.omengye.common.utils.Utils;
import io.omengye.gcs.entity.GCEntity;
import io.omengye.gcs.entity.GSearchItem;
import io.omengye.gcs.entity.SearchEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.netflix.client.ClientException;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Log4j2
@Service
public class WebClientService {

    @Autowired
    private ChooseItemService chooseItemService;

    public Mono<GCEntity> getSearchReponse(SearchEntity searchEntity) {
        GSearchItem item = chooseItemService.getItem();
        String url = genSearchUrl(searchEntity, item);
        return getReponse(url, "", new GCEntity(), GCEntity.class, item);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(WebClientResponseException ex) {
        log.error("Error from WebClient - Status {}, Body {}", ex.getRawStatusCode(), ex.getResponseBodyAsString(), ex);
        return ResponseEntity.status(ex.getRawStatusCode()).body(ex.getResponseBodyAsString());
    }


    private String genSearchUrl(SearchEntity searchEntity, GSearchItem item) {
        String url = "https://www.googleapis.com/customsearch/v1?" +
                "key="+item.getKey()
                +"&cx="+item.getCx()
                +"&q="+searchEntity.getQ()
                +"&start="+searchEntity.getStart()
                +"&num="+searchEntity.getNum();
        if (Utils.isNotEmpty(searchEntity.getSort())) {
            url+="&sort="+searchEntity.getSort();
        }
        return url;
    }

    private Mono<? extends Throwable> mapCommonError(final ClientResponse response) {
        final String message = response.statusCode().getReasonPhrase();
        return Mono.error(new ClientException(message));
    }

    public <T> Mono<T> getReponse(String reqUrl, String authHeader, T obj, Class<T> clazz) {
        return getReponse(reqUrl, authHeader, obj, clazz, null);
    }

    public <T> Mono<T> getReponse(String reqUrl, String authHeader, T obj, Class<T> clazz, GSearchItem item) {
        TcpClient tcpClient = TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .doOnConnected(connection ->
                connection
                .addHandlerLast(new ReadTimeoutHandler(2))
                .addHandlerLast(new WriteTimeoutHandler(2))
        );


        Builder webBuilder = WebClient.builder();
        if (authHeader!=null && !"".equals(authHeader)) {
        	webBuilder.defaultHeader("Authorization", authHeader);
        }
        
        WebClient webClient = webBuilder 
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();

        Mono<T> result = webClient
        		.get()
        		.uri(reqUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(e -> e.isError(),resp -> {
                    log.error("error:{},msg:{}",resp.statusCode().value(),resp.statusCode().getReasonPhrase());
                    if (item!=null && resp.statusCode().value() == 403) {
                        chooseItemService.removeAndGetItem(item);
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


        return result;
    }

}
