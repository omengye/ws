package io.omengye.ws.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jsoniter.JsonIterator;

import io.omengye.ws.entity.ItemEntity;

@Service
public class SearchService {

	public HashMap<String, Object> parse(String str) {
		HashMap<String, Object> res = new HashMap<>();
		HashMap<String, Object> map = JsonIterator.deserialize(str, HashMap.class);
		HashMap<String, Object> searchInformation = (HashMap<String, Object>)map.get("searchInformation");
		List<HashMap<String, Object>> items = (List<HashMap<String, Object>>)map.get("items");
		List<ItemEntity> list = new ArrayList<>();
		for (HashMap<String, Object> item : items) {
			list.add(new ItemEntity(item));
		}
		res.put("info", searchInformation);
		res.put("items", list);
		
		return res;
	}
}
