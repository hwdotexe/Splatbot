package com.hadenwatne.splatbot.tasks;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.HTTPVerb;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.models.data.GiantSquid;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.services.HTTPService;
import com.hadenwatne.splatbot.services.LoggingService;
import com.hadenwatne.splatbot.services.RandomService;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Saves data objects to disk at a regular interval, and changes the bot's status for fun.
 */
public class SaveDataTask extends TimerTask {
	
	public SaveDataTask() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
		
    	c.setTime(new Date());
    	
    	// Run this now, and then again every 3 hours
		t.schedule(this, c.getTime(), 18000000);
	}
	
	public void run() {
		GiantSquid mb = App.Splatbot.getStorageService().getGiantSquid();
		String action = RandomService.GetRandomFromSet(mb.getStatuses().keySet());
		ActivityType t = mb.getStatuses().get(action);

		updateRandomSeed();
		App.Splatbot.getJDA().getPresence().setActivity(Activity.of(t, action));
		
		// Save all brains
		for(Squid b : App.Splatbot.getStorageService().getSquidController().getSquids()) {
			App.Splatbot.getStorageService().getSquidController().saveSquid(b);
		}

		App.Splatbot.getStorageService().getSquidController().saveGiantSquid();

		LoggingService.Log(LogType.SYSTEM, "Autosave Task Ran");
		LoggingService.Write();
	}

	private void updateRandomSeed() {
		String resp = HTTPService.SendHTTPReq(HTTPVerb.GET, "https://www.random.org/integers/?num=2&min=9999999&max=99999999&col=1&base=10&format=plain&rnd=new", null);

		if(resp != null) {
			resp = resp.trim();
			resp = resp.replaceAll("\n", "");
			long seed;

			try {
				seed = Long.parseLong(resp);
			} catch (Exception e) {
				seed = System.currentTimeMillis();
				LoggingService.LogException(e);
			}

			RandomService.GetRandomObj().setSeed(seed);
		}
	}
}