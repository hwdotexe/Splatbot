package com.hadenwatne.splatbot;

public class App {
    public static Splatbot Splatbot;
    public static boolean IsDebug = false;

    /**
     * The entry point for the bot.
     *
     * @param args Program launch arguments.
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("-debug")) {
            IsDebug = true;
        }

        Splatbot = new Splatbot();

        Splatbot.startup(IsDebug);
    }
}