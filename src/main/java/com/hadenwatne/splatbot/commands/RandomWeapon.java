package com.hadenwatne.splatbot.commands;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.commandbuilder.CommandBuilder;
import com.hadenwatne.splatbot.commandbuilder.CommandStructure;
import com.hadenwatne.splatbot.enums.EmbedType;
import com.hadenwatne.splatbot.enums.LanguageKeys;
import com.hadenwatne.splatbot.models.command.ExecutingCommand;
import com.hadenwatne.splatbot.models.data.Weapon;
import com.hadenwatne.splatbot.services.RandomService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class RandomWeapon extends Command {
    public RandomWeapon() {
        super(true);
    }

    @Override
    protected Permission[] configureRequiredBotPermissions() {
        return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected CommandStructure buildCommandStructure() {
        return CommandBuilder.Create("weapon", "Get a randomly-chosen weapon suggestion!")
                .build();
    }

    @Override
    public EmbedBuilder run(ExecutingCommand executingCommand) {
        Weapon randomWeapon = RandomService.GetRandomObjectFromList(App.Splatbot.getWeapons());

        StringBuilder weaponDetails = new StringBuilder();

        weaponDetails.append("**Type**: ");
        weaponDetails.append(randomWeapon.getCategory());
        weaponDetails.append(System.lineSeparator());

        weaponDetails.append("**Sub**: ");
        weaponDetails.append(randomWeapon.getSubWeapon());
        weaponDetails.append(System.lineSeparator());

        weaponDetails.append("**Special**: ");
        weaponDetails.append(randomWeapon.getSpecialWeapon());
        weaponDetails.append(System.lineSeparator());

        return response(EmbedType.TURFWAR)
                .setDescription(executingCommand.getLanguage().getMsg(LanguageKeys.WEAPON_SUGGESTIONS, new String[]{randomWeapon.getName()}))
                .addField("Details", weaponDetails.toString(), false);
    }
}