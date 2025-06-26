package com.hadenwatne.splatbot.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.JSONFileFilter;
import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.enums.LanguageKeys;
import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.models.data.Language;
import com.hadenwatne.splatbot.models.data.LanguageError;
import com.hadenwatne.splatbot.models.data.LanguageMessage;
import com.hadenwatne.splatbot.models.data.Squid;
import net.dv8tion.jda.api.entities.Guild;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LanguageService {
	public static final String DEFAULT_LANGUAGE = "en_default";

	private final Gson gson;
	private final List<Language> languages;
	private final String LANG_DIRECTORY = "langs";
	private final Language defaultLanguage = new Language(DEFAULT_LANGUAGE);

	public LanguageService() {
		LoggingService.Log(LogType.SYSTEM, "Loading Language packs...");

		gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		languages = new ArrayList<>();

		// Load language files.
		loadLangs();

		// Make sure the language is up to date.
		pruneOldLangKeys();
		addNewLangKeys();

		// Update lang files on disk with changes.
		for(Language language : languages) {
			FileService.SaveBytesToFile(LANG_DIRECTORY, language.getFileName(), gson.toJson(language).getBytes());
		}

		LoggingService.Log(LogType.SYSTEM, "Language loading complete!");
	}

	public Language getDefaultLang() {
		return this.defaultLanguage;
	}

	public Language getLangFor(Squid squid) {
		if(squid != null){
			Language l = defaultLanguage; //getLang(squid.getSettingFor(BotSettingName.SERVER_LANG).getAsString());

			if(l == null){
				return getDefaultLang();
			}

			return l;
		} else {
			return getDefaultLang();
		}
	}

	public Language getLangFor(Guild guild) {
		if(guild != null){
			return getLangFor(App.Splatbot.getStorageService().getSquid(guild.getId()));
		}

		return getDefaultLang();
	}

	public Language getLang(String name) {
		for (Language l : this.languages) {
			if (l.getLangName().equalsIgnoreCase(name)) {
				return l;
			}
		}

		return null;
	}

	public List<Language> getAllLangs() {
		return languages;
	}

	private void loadLangs() {
		File[] langFiles = FileService.ListFilesInDirectory(LANG_DIRECTORY, new JSONFileFilter());

		for(File l : langFiles) {
			Language language = this.gson.fromJson(FileService.LoadFileAsString(l), Language.class);

			language.setFileName(l.getName());

			if(!language.getLangName().equals(DEFAULT_LANGUAGE)) {
				LoggingService.Log(LogType.SYSTEM, "\tLoaded " + language.getLangName());

				this.languages.add(language);
			}
		}

		this.languages.add(defaultLanguage);
	}

	private void pruneOldLangKeys() {
		for(Language language : languages) {
			language.messages.removeIf(message -> message.getKey() == null);
			language.errors.removeIf(error -> error.getKey() == null);
		}
	}

	private void addNewLangKeys() {
		for(Language language : languages) {
			for(LanguageKeys key : LanguageKeys.values()) {
				if(language.getMsg(key) == null) {
					LanguageMessage message = defaultLanguage.getLanguageMessage(key);

					language.messages.add(new LanguageMessage(key, message.getValues()));
				}
			}

			for(ErrorKeys key : ErrorKeys.values()) {
				if(language.getError(key) == null) {
					LanguageError message = defaultLanguage.getLanguageError(key);

					language.errors.add(new LanguageError(key, message.getValues()));
				}
			}

			Collections.sort(language.messages);
			Collections.sort(language.errors);
		}
	}
}
