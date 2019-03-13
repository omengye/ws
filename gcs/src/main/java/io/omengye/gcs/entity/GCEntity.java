package io.omengye.gcs.entity;

import java.util.List;

import lombok.Data;

@Data
public class GCEntity {

	private ContextEntity context;
	
	private List<ItemEntity> items;
	
	private String kind;
	
	private QueriesEntity queries;
	
	private SearchInformationEntity searchInformation;
	
	private UrlEntity url;

	public GCResponseEntity getGCResponseEntity() {
		GCResponseEntity entity = new GCResponseEntity();
		entity.setContext(context);
		entity.setItems(items);
		entity.setSearchInformation(searchInformation);
		return entity;
	}

}
