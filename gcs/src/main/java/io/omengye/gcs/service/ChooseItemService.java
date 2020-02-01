package io.omengye.gcs.service;

import io.omengye.gcs.configure.GSearchItems;
import io.omengye.gcs.entity.GCEntity;
import io.omengye.gcs.entity.GSearchItem;
import io.omengye.gcs.entity.ItemEntity;
import io.omengye.gcs.utils.RangeRandom;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    private static RangeRandom rangeRandom = RangeRandom.getInstance();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    @Resource
    private GSearchItems gsItems;

    @Autowired
    private WebClientService webClientService;

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

    private void reputItem(GSearchItem item) {
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

            Mono<GCEntity> response = webClientService.getResponse(reqUrl, "", new GCEntity(), GCEntity.class);
            List<ItemEntity> itemEntities = Objects.requireNonNull(response.block(Duration.ofMillis(1000))).getItems();
            if (itemEntities != null && !itemEntities.isEmpty()) {
                reputItem(entity.getValue());
            }
        }
    }



    public void print() {
        log.info("=== success ===");
        for ( GSearchItem item : gItems) {
            log.info(item);
        }

        log.info("=== error ===");
        for (Map.Entry<Integer, GSearchItem> entity : G_ERROR_ITEMS.entrySet()) {
            log.info(entity.getValue());
        }

    }


}
