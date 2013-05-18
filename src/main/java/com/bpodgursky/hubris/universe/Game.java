package com.bpodgursky.hubris.universe;
import org.json.JSONException;
import org.json.JSONObject;


public class Game {

	public final long gameNumber;
	public final String gameName;
	
	//	not sure what these are 
	public final String aa;
	public final String go;
	public final String hs;
	public final String lt;
	public final String mid;
	public final String np;
	public final String sfv;
	public final String tr;
	public final String tt;
	
	public Game(long gameNumber, String gameName, String aa, String go, String hs, String lt, String mid, String np, String sfv, String tr, String tt){
	
		this.gameNumber = gameNumber;
		this.gameName = gameName;
		this.aa = aa;
		this.go = go;
		this.hs = hs;
		this.lt = lt;
		this.mid = mid;
		this.np = np;
		this.sfv = sfv;
		this.tr = tr;
		this.tt = tt;
	}

  public String getMid() {
    return mid;
  }

  public String toString(){
		
		JSONObject json = new JSONObject();
		try{
			json.put("gameNumber", gameNumber);
			json.put("gameName", gameName);
			json.put("aa", aa);
			json.put("go", go);
			json.put("hs", hs);
			json.put("lt", lt);
			json.put("mid", mid);
			json.put("np", np);
			json.put("sfv", sfv);
			json.put("tr", tr);
			json.put("tt", tt);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		return json.toString();
	}
	
}
