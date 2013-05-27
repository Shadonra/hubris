package com.bpodgursky.hubris.notification;

import org.json.JSONException;
import org.json.JSONObject;

public class AIAdmin extends GameNotification {

	public final Integer player;
	
	public AIAdmin(String key, Long at, Integer player) {
		super(key, at);
		
		this.player = player;
	}

	public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("player", player);
			json.put("key", key);
			json.put("timestamp", at);
			
		}catch (JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}
}
