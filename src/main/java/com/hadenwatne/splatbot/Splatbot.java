package com.hadenwatne.splatbot;

import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.listeners.DMChatListener;
import com.hadenwatne.splatbot.listeners.FirstJoinListener;
import com.hadenwatne.splatbot.listeners.SlashCommandListener;
import com.hadenwatne.splatbot.models.data.GiantSquid;
import com.hadenwatne.splatbot.models.data.Weapon;
import com.hadenwatne.splatbot.models.data.stages.StageData;
import com.hadenwatne.splatbot.services.LanguageService;
import com.hadenwatne.splatbot.services.LoggingService;
import com.hadenwatne.splatbot.services.RandomService;
import com.hadenwatne.splatbot.services.StorageService;
import com.hadenwatne.splatbot.tasks.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class Splatbot {
    private JDA jda;
    private final LanguageService languageService;
    private String botName;
    private String botAvatarUrl;
    private CommandHandler commandHandler;
    private final StorageService storageService;
    private StageData stageData;
    private List<Weapon> weapons;

    public Splatbot() {
        RandomService.Init();
        LoggingService.Init();

        this.languageService = new LanguageService();
        this.storageService = new StorageService();

        this.weapons = new ArrayList<>();
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public StageData getStageData() {
        return stageData;
    }

    public void setWeapons(List<Weapon> weapons) {
        this.weapons = weapons;
    }

    public void setStageData(StageData stageData) {
        this.stageData = stageData;
    }

    public String getBotName() {
        return this.botName;
    }

    public String getBotAvatarUrl() {
        return this.botAvatarUrl;
    }

    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    public LanguageService getLanguageService() {
        return this.languageService;
    }

    public StorageService getStorageService() {
        return this.storageService;
    }

    public JDA getJDA() {
        return this.jda;
    }

    public void startup(boolean isDebug) {
        GiantSquid giantSquid = this.storageService.getGiantSquid();

        configureJDA(isDebug ? giantSquid.getBotAPIKeySecondary() : giantSquid.getBotAPIKey(), giantSquid);

        // Load squid objects into memory.
        this.storageService.getSquidController().loadServerBrains();

        // Start automated tasks.
        new SaveDataTask();
        new FetchStageData();
        new FetchWeaponData();
        new PruneStaleStageData();
        new RefreshStickyPosts();

        // Set the bot name and avatar URL.
        this.botName = getJDA().getSelfUser().getName();
        this.botAvatarUrl = getJDA().getSelfUser().getAvatarUrl();

        // Load commands.
        this.commandHandler = new CommandHandler();

        // Begin listening for events.
        this.jda.addEventListener(new DMChatListener());
        this.jda.addEventListener(new SlashCommandListener());
        this.jda.addEventListener(new FirstJoinListener());
    }

    private void configureJDA(String apiKey, GiantSquid giantSquid) {
        try {
            this.jda = JDABuilder.createDefault(apiKey).build();

            this.jda.awaitReady();
        } catch (InvalidTokenException e) {
            if(apiKey.equals("API_KEY_HERE")) {
                giantSquid.getBotAPIKey();
                giantSquid.getBotAPIKeySecondary();

                // Save the file to disk.
                this.storageService.getSquidController().saveGiantSquid();

                LoggingService.Log(LogType.ERROR, "Could not read bot API key. Please ensure the value \"botAPIKey\" in \"/squids/giantSquid.json\" has a correct bot token from Discord.");
            } else {
                LoggingService.LogException(e);
            }
        } catch (InterruptedException e) {
            LoggingService.LogException(e);
        }
    }
}
