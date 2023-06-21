package com.hadenwatne.splatbot.tasks;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.HTTPVerb;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.models.data.stages.*;
import com.hadenwatne.splatbot.services.HTTPService;
import com.hadenwatne.splatbot.services.LoggingService;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.rmi.runtime.Log;

import java.util.*;

public class FetchStageData extends TimerTask {
	public FetchStageData() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();

		c.setTime(new Date());

		// Run this now, and then again every 12 hours
		t.schedule(this, c.getTime(), 12 * (60 * 60 * 1000));
	}

	// TODO refactor this and clean it up.
	public void run() {
		StageData stageData = new StageData();

		loadStageSchedules(stageData);
		loadSplatfestData(stageData, "US");

		App.Splatbot.setStageData(stageData);

		LoggingService.Log(LogType.SYSTEM, "Stage data refresh task ran.");
	}

	private void loadStageSchedules(StageData stageData) {
		String result = HTTPService.SendHTTPRequest(HTTPVerb.GET, "https://splatoon3.ink/data/schedules.json", null);
		JSONObject json = new JSONObject(result).getJSONObject("data");

		JSONObject turfWarJson = json.getJSONObject("regularSchedules");
		JSONObject rankedJson = json.getJSONObject("bankaraSchedules");
		JSONObject xRankedJson = json.getJSONObject("xSchedules");
		JSONObject salmonRunJson = json.getJSONObject("coopGroupingSchedule");
		JSONObject splatfestJson = json.getJSONObject("festSchedules");
		JSONObject challengeJson = json.getJSONObject("eventSchedules");

		JSONArray turfWarRotation = turfWarJson.getJSONArray("nodes");
		JSONArray rankedRotation = rankedJson.getJSONArray("nodes");
		JSONArray xRankedRotation = xRankedJson.getJSONArray("nodes");
		JSONArray salmonRunRotation = salmonRunJson.getJSONObject("regularSchedules").getJSONArray("nodes");
		JSONArray splatfestRotation = splatfestJson.getJSONArray("nodes");
		JSONArray challengeRotation = challengeJson.getJSONArray("nodes");

		// Turf War data
		for (int i = 0; i < turfWarRotation.length(); i++) {
			JSONObject turfWarObj = turfWarRotation.getJSONObject(i);
			TurfWarStages turfWarStages = new TurfWarStages(turfWarObj.getString("startTime"), turfWarObj.getString("endTime"));

			if (!turfWarObj.isNull("regularMatchSetting")) {
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

			if (!rankedObj.isNull("bankaraMatchSettings")) {
				JSONArray rankedModes = rankedObj.getJSONArray("bankaraMatchSettings");
				List<RankedMode> modes = new ArrayList<>();

				for (int m = 0; m < rankedModes.length(); m++) {
					JSONObject rObj = rankedModes.getJSONObject(m);
					JSONArray rObjStages = rObj.getJSONArray("vsStages");
					RankedMode rankedMode = new RankedMode(rObj.getJSONObject("vsRule").getString("name"), rObj.getString("mode"));

					for (int s = 0; s < rObjStages.length(); s++) {
						rankedMode.getStages().add(rObjStages.getJSONObject(s).getString("name"));
					}

					modes.add(rankedMode);
				}

				RankedStages rankedStages = new RankedStages(rankedObj.getString("startTime"), rankedObj.getString("endTime"), modes);

				stageData.getRanked().add(rankedStages);
			}
		}

		// X Rank data
		for (int i = 0; i < xRankedRotation.length(); i++) {
			JSONObject xRankedObj = xRankedRotation.getJSONObject(i);

			if (!xRankedObj.isNull("xMatchSetting")) {
				JSONObject xRankSettings = xRankedObj.getJSONObject("xMatchSetting");
				XStages xStages = new XStages(xRankedObj.getString("startTime"), xRankedObj.getString("endTime"), xRankSettings.getJSONObject("vsRule").getString("name"));

				JSONArray rObjStages = xRankSettings.getJSONArray("vsStages");

				for (int s = 0; s < rObjStages.length(); s++) {
					xStages.getStages().add(rObjStages.getJSONObject(s).getString("name"));
				}

				stageData.getXRanked().add(xStages);
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

		// Splatfest data
		for (int i = 0; i < splatfestRotation.length(); i++) {
			JSONObject splatfestObj = splatfestRotation.getJSONObject(i);
			SplatfestStages splatfestStages = new SplatfestStages(splatfestObj.getString("startTime"), splatfestObj.getString("endTime"));

			if (!splatfestObj.isNull("festMatchSetting")) {
				JSONArray stageList = splatfestObj.getJSONObject("festMatchSetting").getJSONArray("vsStages");

				for (int s = 0; s < stageList.length(); s++) {
					splatfestStages.getStages().add(stageList.getJSONObject(s).getString("name"));
				}

				stageData.getSplatfestStages().add(splatfestStages);
			}
		}

		// Challenge data
		for (int i = 0; i < challengeRotation.length(); i++) {
			JSONObject challengeObj = challengeRotation.getJSONObject(i);
			JSONObject challengeSettings = challengeObj.getJSONObject("leagueMatchSetting");
			JSONObject challengeEvent = challengeSettings.getJSONObject("leagueMatchEvent");
			JSONArray challengeTimes = challengeObj.getJSONArray("timePeriods");

			String title = challengeEvent.getString("name");
			String description = challengeEvent.getString("desc");
			String regulation = challengeEvent.getString("regulation");
			String mode = challengeSettings.getJSONObject("vsRule").getString("name");

			ChallengeEvent challenge = new ChallengeEvent(title, description, regulation, mode);

			for (int t = 0; t < challengeTimes.length(); t++) {
				JSONObject timeObj = challengeTimes.getJSONObject(t);

				ChallengeEventTime time = new ChallengeEventTime(timeObj.getString("startTime"), timeObj.getString("endTime"));

				challenge.getTimes().add(time);
			}

			stageData.getChallengeEvents().add(challenge);
		}
	}

	private void loadSplatfestData(StageData stageData, String region) {
		String result = HTTPService.SendHTTPRequest(HTTPVerb.GET, "https://splatoon3.ink/data/festivals.json", null);
		JSONObject json = new JSONObject(result).getJSONObject(region).getJSONObject("data").getJSONObject("festRecords");
		JSONArray splatfestRecords = json.getJSONArray("nodes");

		for (int i = 0; i < splatfestRecords.length(); i++) {
			JSONObject splatfestObj = splatfestRecords.getJSONObject(i);
			Splatfest splatfest = new Splatfest(splatfestObj.getString("startTime"), splatfestObj.getString("endTime"), splatfestObj.getString("title"), splatfestObj.getString("state"), splatfestObj.getJSONObject("image").getString("url"));
			JSONArray splatfestTeams = splatfestObj.getJSONArray("teams");

			for (int t = 0; t < splatfestTeams.length(); t++) {
				JSONObject teamObj = splatfestTeams.getJSONObject(t);

				splatfest.getTeams().add(teamObj.getString("teamName"));
			}

			stageData.getSplatFests().add(splatfest);
		}
	}
}