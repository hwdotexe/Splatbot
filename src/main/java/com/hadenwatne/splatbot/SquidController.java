package com.hadenwatne.splatbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hadenwatne.splatbot.enums.BotSettingName;
import com.hadenwatne.splatbot.enums.BotSettingType;
import com.hadenwatne.splatbot.models.data.BotSetting;
import com.hadenwatne.splatbot.models.data.GiantSquid;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.services.FileService;
import net.dv8tion.jda.api.entities.Role;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SquidController {
	private final Gson gson;
	private final List<Squid> squids;

	private GiantSquid giantSquid;

	private final String BRAIN_PARENT_DIRECTORY = "squids";
	private final String BRAIN_SERVER_DIRECTORY = BRAIN_PARENT_DIRECTORY + File.separator + "servers";
	private final String MOTHER_BRAIN_FILE = "giantSquid.json";

	public SquidController() {
		gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		squids = new ArrayList<>();

		loadMotherBrain();
	}

	public void loadMotherBrain() {
		File motherBrainFile = new File(BRAIN_PARENT_DIRECTORY + File.separator + MOTHER_BRAIN_FILE);
		String motherBrainData = FileService.LoadFileAsString(motherBrainFile);

		if (motherBrainData.length() > 0) {
			giantSquid = gson.fromJson(motherBrainData, GiantSquid.class);
		} else {
			giantSquid = new GiantSquid();

			giantSquid.loadDefaults();
			saveGiantSquid();
		}
	}

	public void loadServerBrains(){
		// Load server settings files.
		File[] brainFiles = FileService.ListFilesInDirectory(BRAIN_SERVER_DIRECTORY, new JSONFileFilter());

		for(File brainFile : brainFiles) {
			Squid squid = gson.fromJson(FileService.LoadFileAsString(brainFile), Squid.class);

			// If this squid belongs to a deleted server, remove it and continue.
			if(App.Splatbot.getJDA().getGuildById(squid.getGuildID()) != null) {
				squids.add(squid);
			} else {
				brainFile.delete();

				continue;
			}

			// Ensure new settings are made available for the user to change.
			for(BotSetting defaultSetting : App.Splatbot.getStorageService().getDefaultSettings()) {
				boolean exists = false;

				for(BotSetting botSetting : squid.getSettings()) {
					if(botSetting.getName() == defaultSetting.getName()) {
						exists = true;
						break;
					}
				}

				if(!exists) {
					BotSetting newSetting = new BotSetting(defaultSetting.getName(), defaultSetting.getType(), defaultSetting.getAsString());

					// Before adding a new ROLE setting with the "everyone" default, set its ID to this server's public role.
					if(newSetting.getType() == BotSettingType.ROLE && newSetting.getAsString().equalsIgnoreCase("everyone")) {
						Role everyone = App.Splatbot.getJDA().getGuildById(squid.getGuildID()).getPublicRole();

						newSetting.setValue(everyone.getId(), squid);
					}

					squid.getSettings().add(newSetting);
				}
			}

			// Remove any settings that are no longer supported.
			for(BotSetting bs : new ArrayList<>(squid.getSettings())) {
				boolean contains = false;

				for(BotSettingName s : BotSettingName.values()) {
					if(bs.getName()==s) {
						contains = true;
						break;
					}
				}

				if(!contains)
					squid.getSettings().remove(bs);
			}
		}
	}

	public GiantSquid getGiantSquid() {
		return giantSquid;
	}

	public List<Squid> getSquids() {
		return squids;
	}

	public Squid getSquid(String guildID) {
		for (Squid b : squids) {
			if (b.getGuildID().equals(guildID)) {
				return b;
			}
		}

		Squid b = new Squid(guildID);
		squids.add(b);

		return b;
	}

	public void saveGiantSquid() {
		FileService.SaveBytesToFile(BRAIN_PARENT_DIRECTORY, MOTHER_BRAIN_FILE , gson.toJson(giantSquid).getBytes());
	}

	public void saveSquid(Squid s) {
		FileService.SaveBytesToFile(BRAIN_SERVER_DIRECTORY, s.getGuildID()+ ".json", gson.toJson(s).getBytes());
	}
}
