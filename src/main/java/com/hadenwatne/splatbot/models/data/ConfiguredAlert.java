package com.hadenwatne.splatbot.models.data;

import com.hadenwatne.splatbot.enums.AlertType;

public class ConfiguredAlert {
    private AlertType type;
    private long channel;
    private String timezone;

    public ConfiguredAlert(AlertType type, long channel, String timezone) {
        this.type = type;
        this.channel = channel;
        this.timezone = timezone;
    }

    public AlertType getType() {
        return type;
    }

    public long getChannel() {
        return channel;
    }

    public String getTimezone() {
        return timezone;
    }
}
