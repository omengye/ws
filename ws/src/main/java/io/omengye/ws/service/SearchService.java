package io.omengye.ws.service;

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
		List<ItemEntity> items = (List<ItemEntity>)map.get("items");
		
		res.put("info", searchInformation);
		res.put("item", items);
		
		return res;
	}
	
}
