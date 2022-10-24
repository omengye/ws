package io.omengye.gcs.service;

import io.omengye.gcs.entity.GCEntity;
import io.omengye.gcs.entity.GSearchItem;
import io.omengye.gcs.entity.ReqEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class WebClientService {

    private final ChooseItemService chooseItemService;

    public WebClientService(ChooseItemService chooseItemService) {
        this.chooseItemService = chooseItemService;
    }

    public Mono<GCEntity> getSearchResponse(ReqEntity req) {
        GSearchItem item = chooseItemService.getItem();
        String url = genSearchUrl(req, item);
        return chooseItemService.getResponse(url, "", new GCEntity(), GCEntity.class, item);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(WebClientResponseException ex) {
        log.error("Error from WebClient - Status {}, Body {}", ex.getRawStatusCode(), ex.getResponseBodyAsString(), ex);
        return ResponseEntity.status(ex.getRawStatusCode()).body(ex.getResponseBodyAsString());
    }


    private String genSearchUrl(ReqEntity req, GSearchItem item) {
        return  "https://www.googleapis.com/customsearch/v1?" +
                "key="+item.getKey()
                +"&cx="+item.getCx()
                + req.toString();
    }


}
