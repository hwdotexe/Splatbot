package com.hadenwatne.splatbot.services;

import com.hadenwatne.splatbot.SquidController;
import com.hadenwatne.splatbot.enums.BotSettingName;
import com.hadenwatne.splatbot.enums.BotSettingType;
import com.hadenwatne.splatbot.models.data.BotSetting;
import com.hadenwatne.splatbot.models.data.GiantSquid;
import com.hadenwatne.splatbot.models.data.Squid;

import java.util.ArrayList;
import java.util.List;

public class StorageService {
    private final SquidController squidController;
    private final List<BotSetting> defaultBotSettings;

    public StorageService() {
        this.squidController = new SquidController();
        this.defaultBotSettings = this.createDefaultSettings();
    }

    public SquidController getSquidController() {
        return this.squidController;
    }

    public GiantSquid getGiantSquid() {
        return this.squidController.getGiantSquid();
    }

    public Squid getSquid(String guildID) {
        return this.squidController.getSquid(guildID);
    }

    public List<BotSetting> getDefaultSettings() {
        return this.defaultBotSettings;
    }

    private List<BotSetting> createDefaultSettings() {
        List<BotSetting> settings = new ArrayList<>();
        settings.add(new BotSetting(BotSettingName.ALLOW_MODIFY, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.SERVER_LANG, BotSettingType.TEXT, LanguageService.DEFAULT_LANGUAGE));

        return settings;
    }
}
