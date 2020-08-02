package io.omengye.gcs.configure;

import io.omengye.gcs.entity.GSearchItem;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RefreshScope
@ConfigurationProperties(prefix ="google")
@Data
public class GSearchItems {

    private List<Map<String,String>> search;

    public List<GSearchItem> getItems() {
        List<GSearchItem> items = new ArrayList<>();
        if (search == null) {
            return items;
        }
        for (int i=0; i<search.size(); ++i) {
            Map<String, String> item = search.get(i);
            GSearchItem gSearchItem = new GSearchItem(item);
            gSearchItem.setIndex(i);
            items.add(gSearchItem);
        }
        return items;
    }
}
