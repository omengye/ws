package io.omengye.gcs.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@Data
public class GCResponseEntity {


    private ContextEntity context;

    private List<ItemEntity> items;

    private SearchInformationEntity searchInformation;

    private Spelling spelling;

}
