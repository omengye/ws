package io.omengye.ws.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class UserEntity implements Serializable{

	private String username;
	
	private String password;
	
	public UserEntity() {};
	
	public UserEntity(String username, String password) {
		this.username = username;
		this.password = password;
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
