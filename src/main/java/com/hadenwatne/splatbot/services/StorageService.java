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
        settings.add(new BotSetting(BotSettingName.ALLOW_PIN, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.APPROVAL_EMOTE, BotSettingType.EMOTE, "notset"));
        settings.add(new BotSetting(BotSettingName.APPROVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
        settings.add(new BotSetting(BotSettingName.MANAGE_MUSIC, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.PIN_CHANNEL, BotSettingType.CHANNEL, "general"));
        settings.add(new BotSetting(BotSettingName.POLL_CLOSE, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.POLL_CREATE, BotSettingType.ROLE, "everyone"));
        settings.add(new BotSetting(BotSettingName.POLL_PIN, BotSettingType.BOOLEAN, "false"));
        settings.add(new BotSetting(BotSettingName.PRUNE_FW, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.REMOVAL_EMOTE, BotSettingType.EMOTE, "notset"));
        settings.add(new BotSetting(BotSettingName.REMOVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
        settings.add(new BotSetting(BotSettingName.RESET_EMOTE_STATS, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.ROLES_CONFIGURE, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.SERVER_LANG, BotSettingType.TEXT, LanguageService.DEFAULT_LANGUAGE));
        settings.add(new BotSetting(BotSettingName.TALLY_REACTIONS, BotSettingType.BOOLEAN, "true"));

        return settings;
    }
}
