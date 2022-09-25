package com.hadenwatne.splatbot.tasks;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.HTTPVerb;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.models.data.stages.*;
import com.hadenwatne.splatbot.services.HTTPService;
import com.hadenwatne.splatbot.services.LoggingService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FetchStageData extends TimerTask {
	public FetchStageData() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();

		c.setTime(new Date());

		// Run this now, and then again every 15 hours
		t.schedule(this, c.getTime(), 12 * (60 * 60 * 1000));
	}

	public void run() {
		String result = HTTPService.SendHTTPRequest(HTTPVerb.GET, "https://splatoon3.ink/data/schedules.json", null);
		JSONObject json = new JSONObject(result).getJSONObject("data");

		JSONObject turfWarJson = json.getJSONObject("regularSchedules");
		JSONObject rankedJson = json.getJSONObject("bankaraSchedules");
		JSONObject salmonRunJson = json.getJSONObject("coopGroupingSchedule");

		JSONArray turfWarRotation = turfWarJson.getJSONArray("nodes");
		JSONArray rankedRotation = rankedJson.getJSONArray("nodes");
		JSONArray salmonRunRotation = salmonRunJson.getJSONObject("regularSchedules").getJSONArray("nodes");

		StageData stageData = new StageData();

		// Turf War data
		for (int i = 0; i < turfWarRotation.length(); i++) {
			JSONObject turfWarObj = turfWarRotation.getJSONObject(i);
			TurfWarStages turfWarStages = new TurfWarStages(turfWarObj.getString("startTime"), turfWarObj.getString("endTime"));

			if (!turfWarObj.isNull("festMatchSetting")) {
				// Splatfest (TODO)
				/*
				 "festSchedules": {
				  "nodes": [
					{
					  "startTime": "2022-09-25T20:00:00Z",
					  "endTime": "2022-09-25T22:00:00Z",
					  "festMatchSetting": {
						"__isVsSetting": "FestMatchSetting",
						"__typename": "FestMatchSetting",
						"vsStages": [
						  {
							"id": "VnNTdGFnZS0xMA==",
							"vsStageId": 10,
							"name": "Hammerhead Bridge",
							"image": {
							  "url": "https://splatoon3.ink/assets/splatnet/stage_img/icon/low_resolution/1db8ab338b64b464df50e7f9e270e59423ff8caac6f09679a24f1b7acf3a82f3_1.png"
							}
						  },
						  {
							"id": "VnNTdGFnZS0xNg==",
							"vsStageId": 16,
							"name": "Wahoo World",
							"image": {
							  "url": "https://splatoon3.ink/assets/splatnet/stage_img/icon/low_resolution/61ea801fa4ed32360dcaf83986222ded46a72dbf56194acc6d0cf4659a92ba85_1.png"
							}
						  }
						],
						"vsRule": {
						  "name": "Turf War",
						  "rule": "TURF_WAR",
						  "id": "VnNSdWxlLTA="
						}
					  }
					},
				 */
			} else {
				JSONArray stageList = turfWarObj.getJSONObject("regularMatchSetting").getJSONArray("vsStages");

				for (int s = 0; s < stageList.length(); s++) {
					turfWarStages.getStages().add(stageList.getJSONObject(s).getString("name"));
				}

				stageData.getTurfWar().add(turfWarStages);
			}
		}

		// Ranked data
		for (int i = 0; i < rankedRotation.length(); i++) {
			JSONObject rankedObj = rankedRotation.getJSONObject(i);

			if (!rankedObj.isNull("festMatchSetting")) {
				// Splatfest (TODO)
			} else {
				JSONArray rankedModes = rankedObj.getJSONArray("bankaraMatchSettings");
				List<RankedMode> modes = new ArrayList<>();

				for (int m = 0; m < rankedModes.length(); m++) {
					JSONObject rObj = rankedModes.getJSONObject(m);
					JSONArray rObjStages = rObj.getJSONArray("vsStages");
					RankedMode rankedMode = new RankedMode(rObj.getJSONObject("vsRule").getString("name"));

					for (int s = 0; s < rObjStages.length(); s++) {
						rankedMode.getStages().add(rObjStages.getJSONObject(s).getString("name"));
					}

					modes.add(rankedMode);
				}

				RankedStages rankedStages = new RankedStages(rankedObj.getString("startTime"), rankedObj.getString("endTime"), modes);

				stageData.getRanked().add(rankedStages);
			}
		}

		// Salmon Run data
		for (int i = 0; i < salmonRunRotation.length(); i++) {
			JSONObject salmonRunObj = salmonRunRotation.getJSONObject(i);
			JSONObject salmonRunSetting = salmonRunObj.getJSONObject("setting");
			String stage = salmonRunSetting.getJSONObject("coopStage").getString("name");

			List<String> weapons = new ArrayList<>();
			JSONArray salmonRunWeapons = salmonRunSetting.getJSONArray("weapons");

			for (int m = 0; m < salmonRunWeapons.length(); m++) {
				JSONObject weapon = salmonRunWeapons.getJSONObject(m);

				weapons.add(weapon.getString("name"));
			}

			SalmonRunStages salmonRunStages = new SalmonRunStages(salmonRunObj.getString("startTime"), salmonRunObj.getString("endTime"), stage, weapons);

			stageData.getSalmonRun().add(salmonRunStages);
		}

		App.Splatbot.setStageData(stageData);

		LoggingService.Log(LogType.SYSTEM, "Stage data refresh task ran");
	}
}