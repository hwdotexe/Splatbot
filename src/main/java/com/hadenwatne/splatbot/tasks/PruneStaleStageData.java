package com.hadenwatne.splatbot.tasks;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.HTTPVerb;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.models.data.stages.*;
import com.hadenwatne.splatbot.services.DataService;
import com.hadenwatne.splatbot.services.HTTPService;
import com.hadenwatne.splatbot.services.LoggingService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class PruneStaleStageData extends TimerTask {
	public PruneStaleStageData() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();

		c.setTime(new Date());

		// Run this now, and then again every 1 hour
		t.schedule(this, c.getTime(), (60 * 60 * 1000));
	}

	public void run() {
		Date now = new Date();

		// Remove Stage objects that are in the past.
		App.Splatbot.getStageData().getTurfWar().removeIf(stageData -> DataService.ParseDate(stageData.getEndTime()).before(now));
		App.Splatbot.getStageData().getSalmonRun().removeIf(stageData -> DataService.ParseDate(stageData.getEndTime()).before(now));
		App.Splatbot.getStageData().getRanked().removeIf(stageData -> DataService.ParseDate(stageData.getEndTime()).before(now));
		App.Splatbot.getStageData().getChallengeEvents().removeIf(stageData -> DataService.ParseDate(stageData.getTimes().get(stageData.getTimes().size()-1).getEndTime()).before(now));
		App.Splatbot.getStageData().getSplatfestStages().removeIf(stageData -> DataService.ParseDate(stageData.getEndTime()).before(now));
		App.Splatbot.getStageData().getXRanked().removeIf(stageData -> DataService.ParseDate(stageData.getEndTime()).before(now));

		LoggingService.Log(LogType.SYSTEM, "Cleared expired stage data.");
	}
}