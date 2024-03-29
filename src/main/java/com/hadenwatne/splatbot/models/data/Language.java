package com.hadenwatne.splatbot.models.data;

import com.hadenwatne.splatbot.enums.ErrorKeys;
import com.hadenwatne.splatbot.enums.LanguageKeys;
import com.hadenwatne.splatbot.services.RandomService;

import java.util.ArrayList;
import java.util.List;

public class Language {
    public List<LanguageMessage> messages;
    public List<LanguageError> errors;
    public final String wildcard = "%WC%";
    public final String linebreak = "%BR%";
    private String langName;
    private transient String fileName;

    public Language(String name) {
        langName = name;
        messages = new ArrayList<>();
        errors = new ArrayList<>();

        this.fileName = this.langName + ".json";

        populateDefaultValues();
    }

    public String getLangName() {
        return langName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMsg(LanguageKeys key) {
        LanguageMessage message = getLanguageMessage(key);

        if (message != null) {
            String[] messageArray = message.getValues();

            if (messageArray.length > 1) {
                return processBreaks(messageArray[RandomService.GetRandom(messageArray.length)]);
            }

            return processBreaks(messageArray[0]);
        }

        return null;
    }

    public String getMsg(LanguageKeys key, String[] replacements) {
        String msg = getMsg(key);

        for (String r : replacements) {
            // $ characters need to be dynamically escaped, otherwise Java will think it's an improper regex anchor.
            msg = msg.replaceFirst(wildcard, r.replaceAll("\\$", "__DOLLARSIGN__"));
        }

        msg = msg.replaceAll("__DOLLARSIGN__", "\\$");

        return msg;
    }

    public String getError(ErrorKeys key) {
        LanguageError error = getLanguageError(key);

        if (error != null) {
            String[] messageArray = error.getValues();

            if (messageArray.length > 1) {
                return processBreaks(messageArray[RandomService.GetRandom(messageArray.length)]);
            }

            return processBreaks(messageArray[0]);
        }

        return null;
    }

    public LanguageMessage getLanguageMessage(LanguageKeys key) {
        for (LanguageMessage message : messages) {
            if (message.getKey() == key) {
                return message;
            }
        }

        return null;
    }

    public LanguageError getLanguageError(ErrorKeys key) {
        for (LanguageError error : errors) {
            if (error.getKey() == key) {
                return error;
            }
        }

        return null;
    }

    public String getError(ErrorKeys key, String[] replacements) {
        String msg = getError(key);

        for (String r : replacements) {
            msg = msg.replaceFirst(wildcard, r);
        }

        return msg;
    }

    private String processBreaks(String msg) {
        msg = msg.replaceAll(linebreak, System.lineSeparator());

        return msg;
    }

    private void populateDefaultValues() {
        errors.add(new LanguageError(ErrorKeys.BOT_ERROR, new String[]{"There was an internal error, and your request did not complete."}));
        errors.add(new LanguageError(ErrorKeys.CANNOT_DELETE, new String[]{"Sorry, I can't let you delete that. It's very precious to me."}));
        errors.add(new LanguageError(ErrorKeys.CHANNEL_NOT_FOUND, new String[]{"I can't find the correct channel for that."}));
        errors.add(new LanguageError(ErrorKeys.COMMAND_NOT_FOUND, new String[]{"That command hasn't been invented yet!"}));
        errors.add(new LanguageError(ErrorKeys.HEY_THERE, new String[]{"Hey there! Try using `" + wildcard + " help`!"}));
        errors.add(new LanguageError(ErrorKeys.NO_PERMISSION_BOT, new String[]{"I don't have permission to do that on this server."}));
        errors.add(new LanguageError(ErrorKeys.NO_PERMISSION_USER, new String[]{"I'm afraid I can't let you do that."}));
        errors.add(new LanguageError(ErrorKeys.NOT_FOUND, new String[]{"There were no results."}));
        errors.add(new LanguageError(ErrorKeys.PAGE_NOT_FOUND, new String[]{"The page you requested is empty or does not exist."}));
        errors.add(new LanguageError(ErrorKeys.PERMISSION_MISSING, new String[]{wildcard+" requires the following permissions in order to process that command: "+linebreak+wildcard}));
        errors.add(new LanguageError(ErrorKeys.RESERVED_WORD, new String[]{"It looks like you tried to use a reserved word. Try a different one!"}));
        errors.add(new LanguageError(ErrorKeys.SETTING_VALUE_INVALID, new String[]{"The value you provided is invalid. Please try again."}));
        errors.add(new LanguageError(ErrorKeys.WRONG_USAGE, new String[]{"The command syntax you used is incorrect."}));

        messages.add(new LanguageMessage(LanguageKeys.ANARCHY_HEADING, new String[]{"Climb your way through the ranks in these upcoming matches!"}));
        messages.add(new LanguageMessage(LanguageKeys.CHALLENGE_HEADING, new String[]{"What crazy battles await you in Challenge Mode?"}));
        messages.add(new LanguageMessage(LanguageKeys.GENERIC_SUCCESS, new String[]{"Success!"}));
        messages.add(new LanguageMessage(LanguageKeys.SALMON_RUN_HEADING, new String[]{"Mr. Grizz needs Salmon Eggs! These are your options."}));
        messages.add(new LanguageMessage(LanguageKeys.SETTING_LIST_TITLE, new String[]{"Available settings"}));
        messages.add(new LanguageMessage(LanguageKeys.SETTING_UPDATED_SUCCESS, new String[]{"Setting was updated successfully!"}));
        messages.add(new LanguageMessage(LanguageKeys.SPLATFEST_HEADING, new String[]{"It's time to collect the Clout!"}));
        messages.add(new LanguageMessage(LanguageKeys.TIMEZONE_SET, new String[]{"Your timezone is now `"+wildcard+"`! Commands you run will be shown in this timezone."}));
        messages.add(new LanguageMessage(LanguageKeys.TURF_WAR_HEADING, new String[]{"Here are the deets for Turf War battles!"}));
        messages.add(new LanguageMessage(LanguageKeys.WEAPON_SUGGESTIONS, new String[]{"I'm feeling like recommending the " + wildcard + " today.", "You could use more practice with the " + wildcard + ".", "Want to make it rain (with salt)? Try the " + wildcard + "", "Use the " + wildcard + ". I dare you.", "Callie would tell you that you suck at the " + wildcard + ".", "" + wildcard + " - The Great Callie has spoken!", "" + wildcard + ". Hands down.", "Nothing's fresher than the " + wildcard + "", "" + wildcard + " is love, " + wildcard + " is life.", "I mean, you could try the " + wildcard + ", but I'd be careful...", "Sheldon paid me to answer " + wildcard + ".", "Bada bada bada bada... " + wildcard + "!", "You think you've got what it takes to wield the " + wildcard + "?"}));
        messages.add(new LanguageMessage(LanguageKeys.XBATTLES_HEADING, new String[]{"Raise your X Power in this challenging mode!"}));
    }
}