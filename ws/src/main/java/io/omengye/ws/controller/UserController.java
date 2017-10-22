package io.omengye.ws.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.omengye.ws.entity.UserEntity;
import io.omengye.ws.service.TokenService;
import io.omengye.ws.service.UserService;
import io.omengye.ws.utils.StrUtil;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping("/getUserid")
	public UserEntity getUserId(HttpServletRequest request) {
		String userip = request.getParameter("userip");
		UserEntity entity = new UserEntity();
		if (StrUtil.snull(userip)==null) {
			return entity;
		}
		//
		try {
			if (userService.getUserByIp(userip) != null) {
				// existed visitor
			}
			else {
				// new visitor
				userService.addUser(userip);
			}
			entity = userService.getUserByIp(userip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}
	
	@PostMapping("/getToken")
	public String getToken(HttpServletRequest request) {
		String token = null;
		String userip = request.getParameter("userip");
		if (StrUtil.snull(userip)==null) {
			return null;
		} 
		try {
			if (tokenService.getToken(userip)!=null) {
				token = tokenService.getToken(userip);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	}
	
}
