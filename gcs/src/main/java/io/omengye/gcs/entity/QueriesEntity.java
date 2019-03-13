package io.omengye.gcs.entity;

import java.util.List;

import lombok.Data;

@Data
public class QueriesEntity {

	private List<QueryEntity> nextPage;
	
	private List<QueryEntity> request;
	
}
