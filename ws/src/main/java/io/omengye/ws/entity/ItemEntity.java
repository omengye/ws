package io.omengye.ws.entity;

import java.util.HashMap;

import io.omengye.ws.utils.StrUtil;

public class ItemEntity {
	private String snippet;
	private String htmlFormattedUrl;
	private String htmlTitle;
	private String displayLink;
    private String link;
    private String htmlSnippet;
	private String title;
	private String formattedUrl;
	
	public ItemEntity() {}
	
	public ItemEntity(HashMap<String, Object> map) {
		this.snippet = StrUtil.getStr(map.get("snippet"));
		this.htmlFormattedUrl = StrUtil.getStr(map.get("htmlFormattedUrl"));
		this.htmlTitle = StrUtil.getStr(map.get("htmlTitle"));
		this.displayLink = StrUtil.getStr(map.get("displayLink"));
		this.link = StrUtil.getStr(map.get("link"));
		this.htmlSnippet = StrUtil.getStr(map.get("htmlSnippet"));
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
	public String getDisplayLink() {
		return displayLink;
	}
	public void setDisplayLink(String displayLink) {
		this.displayLink = displayLink;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getHtmlSnippet() {
		return htmlSnippet;
	}
	public void setHtmlSnippet(String htmlSnippet) {
		this.htmlSnippet = htmlSnippet;
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
