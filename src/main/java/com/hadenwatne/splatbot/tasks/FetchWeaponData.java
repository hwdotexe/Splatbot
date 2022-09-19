package com.hadenwatne.splatbot.tasks;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.HTTPVerb;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.models.data.Weapon;
import com.hadenwatne.splatbot.services.HTTPService;
import com.hadenwatne.splatbot.services.LoggingService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class FetchWeaponData extends TimerTask {
	public FetchWeaponData() {
		run();
	}

	public void run() {
		List<Weapon> weapons = new ArrayList<>();
		String result = HTTPService.SendHTTPRequest(HTTPVerb.GET, "https://stat.ink/api/v3/weapon", null);
		JSONArray json = new JSONArray(result);

		for(int i=0; i<json.length(); i++) {
			JSONObject weapon = json.getJSONObject(i);

			String name = weapon.getJSONObject("name").getString("en_US");
			String category = weapon.getJSONObject("type").getJSONObject("name").getString("en_US");
			String subWeapon = weapon.getJSONObject("sub").getJSONObject("name").getString("en_US");
			String specialWeapon = weapon.getJSONObject("special").getJSONObject("name").getString("en_US");

			weapons.add(new Weapon(name, category, subWeapon, specialWeapon));
		}

		App.Splatbot.setWeapons(weapons);

		LoggingService.Log(LogType.SYSTEM, "Weapon data retrieved.");
	}
}