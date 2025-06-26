package com.hadenwatne.splatbot.services;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.BotSettingType;
import com.hadenwatne.splatbot.models.data.BotSetting;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class SplatbotService {

    /**
     * Checks whether the member complies with the setting's permission
     * requirements, if applicable.
     * @param setting The setting to check.
     * @param member The user to check.
     * @return A boolean representing whether the user complies.
     */
    public static boolean CheckUserPermission(Guild server, BotSetting setting, Member member) {
        if(server != null) {
            if (setting.getType() == BotSettingType.ROLE) {
                if (App.IsDebug)
                    return true;

                if (member != null) {
                    // Always return true for administrators regardless of setting.
                    if(member.hasPermission(Permission.ADMINISTRATOR)) {
                        return true;
                    }

                    String roleString = setting.getAsString();

                    // If the role requires administrator, make sure they are admin.
                    if (roleString.equals("administrator")) {
                        return member.hasPermission(Permission.ADMINISTRATOR);
                    }

                    Role role = setting.getAsRole(server);

                    // Check if the user has the given role.
                    if(server.getPublicRole().getIdLong() == role.getIdLong()) {
                        return true;
                    }

                    return member.getRoles().contains(role);
                }
            }
        }

        return false;
    }
}
