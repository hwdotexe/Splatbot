package com.hadenwatne.splatbot.models.command;

import com.hadenwatne.splatbot.enums.LogType;
import com.hadenwatne.splatbot.models.data.Language;
import com.hadenwatne.splatbot.models.data.Squid;
import com.hadenwatne.splatbot.services.LoggingService;
import com.hadenwatne.splatbot.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.io.File;
import java.io.InputStream;
import java.util.function.Consumer;

public class ExecutingCommand {
    private final Language language;
    private final Squid squid;

    private String commandName;
    private String subCommandGroup;
    private String subCommand;
    private ExecutingCommandArguments commandArguments;
    private InteractionHook hook;
    private Message message;

    public ExecutingCommand(Language language, Squid squid) {
        this.language = language;
        this.squid = squid;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public Language getLanguage() {
        return this.language;
    }

    public Squid getSquid() {
        return this.squid;
    }

    public String getSubCommandGroup() {
        if(this.subCommandGroup == null) {
            return "";
        }

        return this.subCommandGroup;
    }

    public void setSubCommandGroup(String subCommandGroup) {
        this.subCommandGroup = subCommandGroup;
    }

    public boolean hasSubCommandGroup() {
        return this.subCommandGroup != null;
    }

    public String getSubCommand() {
        if(this.subCommand == null) {
            return "";
        }

        return this.subCommand;
    }

    public void setSubCommand(String subCommand) {
        this.subCommand = subCommand;
    }

    public boolean hasSubCommand() {
        return this.subCommand != null;
    }

    public ExecutingCommandArguments getCommandArguments() {
        return this.commandArguments;
    }

    public void setCommandArguments(ExecutingCommandArguments commandArguments) {
        this.commandArguments = commandArguments;
    }

    public void setInteractionHook(InteractionHook hook) {
        this.hook = hook;
    }

    public Message getMessage() {
        return this.message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public boolean hasMessage() {
        return this.message != null;
    }

    public boolean hasInteractionHook() {
        return this.hook != null;
    }

    public User getAuthorUser() {
        if(hook != null) {
            return hook.getInteraction().getUser();
        } else if(message != null) {
            return message.getAuthor();
        } else {
            return null;
        }
    }

    public Member getAuthorMember() {
        if(hook != null) {
            return hook.getInteraction().getMember();
        } else if(message != null) {
            return message.getMember();
        } else {
            return null;
        }
    }

    public Guild getServer() {
        if(hook != null) {
            return hook.getInteraction().isFromGuild() ? hook.getInteraction().getGuild() : null;
        } else if(message != null) {
            return message.isFromGuild() ? message.getGuild() : null;
        } else {
            return null;
        }
    }

    public MessageChannel getChannel() {
        if(hook != null) {
            return hook.getInteraction().getMessageChannel();
        } else if(message != null) {
            return message.getChannel();
        } else {
            return null;
        }
    }

    public void reply(EmbedBuilder embedBuilder) {
        if(embedBuilder != null) {
            if (hook != null) {
                MessageService.ReplyToMessage(hook, embedBuilder, false);
            } else if (message != null) {
                MessageService.ReplyToMessage(message, embedBuilder, false);
            } else {
                LoggingService.Log(LogType.ERROR, "Could not send response for command " + this.commandName);
            }
        }
    }

    public void reply(EmbedBuilder embedBuilder, boolean mention, Consumer<? super Message> onSuccess) {
        if(embedBuilder != null) {
            if (hook != null) {
                MessageService.ReplyToMessage(hook, embedBuilder, mention, onSuccess);
            } else if (message != null) {
                MessageService.ReplyToMessage(message, embedBuilder, mention, onSuccess);
            } else {
                LoggingService.Log(LogType.ERROR, "Could not send response for command " + this.commandName);
            }
        }
    }

    public void replyFile(InputStream file, String name, EmbedBuilder embedBuilder) {
        if(embedBuilder != null && file != null) {
            if (hook != null) {
                MessageService.ReplyToMessage(hook, file, name, embedBuilder, false);
            } else if (message != null) {
                MessageService.ReplyToMessage(message, file, name, embedBuilder, false);
            } else {
                LoggingService.Log(LogType.ERROR, "Could not send response for command " + this.commandName);
            }
        }
    }

    public void replyFile(File file, EmbedBuilder embedBuilder) {
        if(embedBuilder != null && file != null) {
            if (hook != null) {
                MessageService.ReplyToMessage(hook, file, embedBuilder, false);
            } else if (message != null) {
                MessageService.ReplyToMessage(message, file, embedBuilder, false);
            } else {
                LoggingService.Log(LogType.ERROR, "Could not send response for command " + this.commandName);
            }
        }
    }
}
