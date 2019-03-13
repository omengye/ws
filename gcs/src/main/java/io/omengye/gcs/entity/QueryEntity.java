package io.omengye.gcs.entity;

import lombok.Data;

@Data
public class QueryEntity {
	
	private Integer count;
	
	private String cx;
	
	private String inputEncoding;
	
	private String outputEncoding;
	
	private String safe;
	
	private String searchTerms;
	
	private int startIndex;
	
	private String title;
	
	private String totalResults;
}
