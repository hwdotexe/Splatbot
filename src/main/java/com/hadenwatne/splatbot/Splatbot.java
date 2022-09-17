package com.hadenwatne.splatbot;

import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.listeners.ChatListener;
import com.hadenwatne.splatbot.listeners.FirstJoinListener;
import com.hadenwatne.splatbot.listeners.SlashCommandListener;
import com.hadenwatne.splatbot.models.data.GiantSquid;
import com.hadenwatne.splatbot.services.LanguageService;
import com.hadenwatne.splatbot.services.LoggingService;
import com.hadenwatne.splatbot.services.StorageService;
import com.hadenwatne.splatbot.tasks.SaveDataTask;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Splatbot {
    private JDA jda;
    private final LanguageService languageService;
    private String botName;
    private String botAvatarUrl;
    private CommandHandler commandHandler;
    private final StorageService storageService;

    public Splatbot() {
        LoggingService.Init();

        this.languageService = new LanguageService();
        this.storageService = new StorageService();
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

        // Load Brain objects into memory.
        this.storageService.getSquidController().loadServerBrains();

        // Start automated tasks.
        new SaveDataTask();

        // Set the bot name and avatar URL.
        this.botName = getJDA().getSelfUser().getName();
        this.botAvatarUrl = getJDA().getSelfUser().getAvatarUrl();

        // Load commands.
        this.commandHandler = new CommandHandler();

        // Begin listening for events.
        this.jda.addEventListener(new ChatListener());
        this.jda.addEventListener(new SlashCommandListener());
        this.jda.addEventListener(new FirstJoinListener());
    }

    private void configureJDA(String apiKey, GiantSquid giantSquid) {
        try {
            this.jda = JDABuilder.createDefault(apiKey).build();

            this.jda.awaitReady();
        } catch (LoginException e) {
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
