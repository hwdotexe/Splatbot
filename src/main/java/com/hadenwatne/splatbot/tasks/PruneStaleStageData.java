package com.hadenwatne.splatbot.tasks;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.HTTPVerb;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.models.data.stages.*;
import com.hadenwatne.splatbot.models.gameData.schedules.GameSchedules;
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
		if(App.Splatbot.getStageData() != null) {
			GameSchedules schedules = App.Splatbot.getStageData().getRegular().data;
//			schedules.regularSchedules.nodes.removeIf(stageData -> DataService.ParseDate(stageData.endTime).before(now));
//			schedules.bankaraSchedules.nodes.removeIf(stageData -> DataService.ParseDate(stageData.endTime).before(now));
//			schedules.coopGroupingSchedule.regularSchedules.nodes.removeIf(stageData -> DataService.ParseDate(stageData.endTime).before(now));
//			schedules.eventSchedules.nodes.removeIf(stageData -> DataService.ParseDate(stageData.endTime).before(now));
//			schedules.xSchedules.nodes.removeIf(stageData -> DataService.ParseDate(stageData.endTime).before(now));

			// TODO: Splatfest ongoing stage records aren't being cleaned up.
		}

		LoggingService.Log(LogType.SYSTEM, "Cleared expired stage data.");
	}
}