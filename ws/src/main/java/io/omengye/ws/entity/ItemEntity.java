package io.omengye.ws.entity;

import java.util.HashMap;

import io.omengye.ws.utils.StrUtil;

public class ItemEntity {
	private String snippet;
	private String htmlFormattedUrl;
	private String htmlTitle;
	private String kind;
	private String title;
	private String formattedUrl;
	
	public ItemEntity() {}
	
	public ItemEntity(HashMap<String, Object> map) {
		this.snippet = StrUtil.getStr(map.get("snippet"));
		this.htmlFormattedUrl = StrUtil.getStr(map.get("htmlFormattedUrl"));
		this.htmlTitle = StrUtil.getStr(map.get("htmlTitle"));
		this.kind = StrUtil.getStr(map.get("kind"));
		this.title = StrUtil.getStr(map.get("title"));
		this.formattedUrl = StrUtil.getStr(map.get("formattedUrl"));
	}
	
	public String getSnippet() {
		return snippet;
	}
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	public String getHtmlFormattedUrl() {
		return htmlFormattedUrl;
	}
	public void setHtmlFormattedUrl(String htmlFormattedUrl) {
		this.htmlFormattedUrl = htmlFormattedUrl;
	}
	public String getHtmlTitle() {
		return htmlTitle;
	}
	public void setHtmlTitle(String htmlTitle) {
		this.htmlTitle = htmlTitle;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFormattedUrl() {
		return formattedUrl;
	}
	public void setFormattedUrl(String formattedUrl) {
		this.formattedUrl = formattedUrl;
	}
	

	
}
