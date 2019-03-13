package io.omengye.gcs.service;

import io.omengye.gcs.configure.GSearchItems;
import io.omengye.gcs.entity.GCEntity;
import io.omengye.gcs.entity.GSearchItem;
import io.omengye.gcs.entity.ItemEntity;
import io.omengye.gcs.entity.SearchInformationEntity;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Log4j2
@Service
public class ChooseItemService {

    private static volatile List<GSearchItem> GItems = new ArrayList<>();

    private static ConcurrentHashMap<Integer, GSearchItem> GErrorItems = new ConcurrentHashMap<>();

    private static RangeRandom rangeRandom = RangeRandom.getInstance();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    @Resource
    private GSearchItems gSItems;

    @Autowired
    private WebClientService webClientService;

    public void init() {
        ChooseItemService.GItems = gSItems.getItems();
    }

    public GSearchItem getItem() {
        rwLock.readLock().lock();
        if (GItems.isEmpty()) {
            return null;
        }
        int idx = rangeRandom.rand(GItems.size());
        rwLock.readLock().unlock();
        return GItems.get(idx);
    }

    public GSearchItem removeAndGetItem(GSearchItem item) {
        rwLock.writeLock().lock();
        boolean flag = GItems.remove(item);
        if (flag && !GErrorItems.keySet().contains(item.getIndex())) {
            GErrorItems.put(item.getIndex(), item);
        }
        rwLock.writeLock().unlock();
        return getItem();
    }

    private void reputItem(GSearchItem item) {
        rwLock.writeLock().lock();
        if (GItems.contains(item)) {
            return;
        }
        GItems.add(item);
        rwLock.writeLock().unlock();
        GErrorItems.remove(item.getIndex());
    }


    @Scheduled(cron = "0 0/30 0 * * ?")
    public void loopErrorItems() {
        for (Map.Entry<Integer, GSearchItem> entity : GErrorItems.entrySet()) {

            String reqUrl = "https://www.googleapis.com/customsearch/v1?key="
                    +entity.getValue().getKey()+"&cx="+entity.getValue().getCx()+"&q=test&start=1&num=5";

            Mono<GCEntity> reponse = webClientService.getReponse(reqUrl, "", new GCEntity(), GCEntity.class);
            List<ItemEntity> itemEntities = reponse.block(Duration.ofMillis(1000)).getItems();
            if (itemEntities != null && itemEntities.size()>0) {
                reputItem(entity.getValue());
            }
        }
    }



    public void print() {
        System.out.println("=== success ===");
        for ( GSearchItem item : GItems) {
            System.out.println(item);
        }

        System.out.println("=== error ===");
        for (Map.Entry<Integer, GSearchItem> entity : GErrorItems.entrySet()) {
            System.out.println(entity.getValue());
        }

    }


}
