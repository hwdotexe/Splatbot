package com.hadenwatne.splatbot.tasks;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.HTTPVerb;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.models.data.stages.*;
import com.hadenwatne.splatbot.models.gameData.SplatoonStageData;
import com.hadenwatne.splatbot.models.gameData.schedules.GameData;
import com.hadenwatne.splatbot.models.gameData.splatfests.USData;
import com.hadenwatne.splatbot.models.gameData.splatfests.USFestival;
import com.hadenwatne.splatbot.services.HTTPService;
import com.hadenwatne.splatbot.services.LoggingService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class FetchStageData extends TimerTask {
	public FetchStageData() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();

		c.setTime(new Date());

		// Run this now, and then again every 8 hours
		t.schedule(this, c.getTime(), 8 * (60 * 60 * 1000));
	}

	public void run() {
		GameData regular = loadStageSchedules();
		USFestival splatfests = loadSplatfestData();

		if(regular != null && splatfests != null) {
			SplatoonStageData stageData = new SplatoonStageData(regular, splatfests);

			App.Splatbot.setStageData(stageData);
		}

		LoggingService.Log(LogType.SYSTEM, "Stage data refresh task ran.");
	}

	private GameData loadStageSchedules() {
		String result = HTTPService.SendHTTPRequest(HTTPVerb.GET, "https://splatoon3.ink/data/schedules.json", null);

        if(result == null || result.isEmpty()) {
			return null;
		}

		return App.gson.fromJson(result, GameData.class);
	}

	private USFestival loadSplatfestData() {
		String result = HTTPService.SendHTTPRequest(HTTPVerb.GET, "https://splatoon3.ink/data/festivals.json", null);

		if(result == null || result.isEmpty()) {
			return null;
		}

		return App.gson.fromJson(result, USFestival.class);
	}
}