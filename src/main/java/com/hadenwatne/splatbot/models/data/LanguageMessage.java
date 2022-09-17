package com.hadenwatne.splatbot.models.data;


import com.hadenwatne.splatbot.enums.LanguageKeys;

public class LanguageMessage implements Comparable<LanguageMessage> {
    private final LanguageKeys key;
    private final String[] values;

    public LanguageMessage(LanguageKeys key, String[] values) {
        this.key = key;
        this.values = values;
    }

    public LanguageKeys getKey() {
        return this.key;
    }

    public String[] getValues() {
        return this.values;
    }

    public int compareTo(LanguageMessage otherMessage) {
        return this.key.name().compareTo(otherMessage.getKey().name());
    }
}
