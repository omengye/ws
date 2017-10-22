package io.omengye.ws.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class UserEntity implements Serializable{

	private String userip;
	
	private String username;
	
	private String password;
	
	public UserEntity() {};
	
	public UserEntity(String userip, String username, String password) {
		this.userip = userip;
		this.username = username;
		this.password = password;
	}

	public String getUserip() {
		return userip;
	}

	public void setUserip(String userip) {
		this.userip = userip;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public List<String> getRoles() {
		return Arrays.asList(new String[]{"USERS"});
	}
	
}
