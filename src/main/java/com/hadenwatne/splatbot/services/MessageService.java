package com.hadenwatne.splatbot.services;

import com.hadenwatne.splatbot.enums.LogType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

public class MessageService {
    public static void ReplySimpleMessage(Message message, String response, boolean mention) {
        message.reply(response).mentionRepliedUser(mention).queue();
    }

    public static void ReplyToMessage(InteractionHook hook, List<EmbedBuilder> response, boolean mention) {
        List<MessageEmbed> embeds = response.stream().map(EmbedBuilder::build).toList();
        hook.sendMessageEmbeds(embeds).mentionRepliedUser(mention).queue();
    }

    public static void ReplyToMessage(InteractionHook hook, File file, List<EmbedBuilder> response, boolean mention) {
        FileUpload fileUpload = FileUpload.fromData(file);
        List<MessageEmbed> embeds = response.stream().map(EmbedBuilder::build).toList();

        hook.sendFiles(fileUpload).addEmbeds(embeds).mentionRepliedUser(mention).queue();
    }

    public static void ReplyToMessage(InteractionHook hook, InputStream file, String name, List<EmbedBuilder> response, boolean mention) {
        FileUpload fileUpload = FileUpload.fromData(file, name);
        List<MessageEmbed> embeds = response.stream().map(EmbedBuilder::build).toList();

        hook.sendFiles(fileUpload).addEmbeds(embeds).mentionRepliedUser(mention).queue();
    }

    public static void ReplyToMessage(InteractionHook hook, List<EmbedBuilder> response, boolean mention, Consumer<? super Message> onSuccess) {
        List<MessageEmbed> embeds = response.stream().map(EmbedBuilder::build).toList();

        hook.sendMessageEmbeds(embeds).mentionRepliedUser(mention).queue(onSuccess, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to interaction hook "+hook.getInteraction().getId()+" in channel "+ hook.getInteraction().getChannel().getId());
            LoggingService.Log(LogType.ERROR, error.getMessage());
        });
    }

    public static void ReplyToMessage(Message message, List<EmbedBuilder> response, boolean mention) {
        List<MessageEmbed> embeds = response.stream().map(EmbedBuilder::build).toList();

        message.replyEmbeds(embeds).mentionRepliedUser(mention).queue(success -> {}, error -> {
            MessageService.SendMessage(message.getChannel(), response, false);
        });
    }

    public static void ReplyToMessage(Message message, File file, List<EmbedBuilder> response, boolean mention) {
        FileUpload fileUpload = FileUpload.fromData(file);
        List<MessageEmbed> embeds = response.stream().map(EmbedBuilder::build).toList();

        message.replyFiles(fileUpload).setEmbeds(embeds).mentionRepliedUser(mention).queue(success -> { file.delete(); });
    }

    public static void ReplyToMessage(Message message, InputStream file, String name, List<EmbedBuilder> response, boolean mention) {
        FileUpload fileUpload = FileUpload.fromData(file, name);
        List<MessageEmbed> embeds = response.stream().map(EmbedBuilder::build).toList();

        message.replyFiles(fileUpload).setEmbeds(embeds).mentionRepliedUser(mention).queue();
    }

    public static void ReplyToMessage(Message message, List<EmbedBuilder> response, boolean mention, Consumer<? super Message> onSuccess) {
        List<MessageEmbed> embeds = response.stream().map(EmbedBuilder::build).toList();

        message.replyEmbeds(embeds).mentionRepliedUser(mention).queue(onSuccess);
    }

    public static void ReplyToMessage(InteractionHook hook, String response, boolean mention) {
        hook.sendMessage(response).mentionRepliedUser(mention).queue();
    }

    public static void ReplyToMessage(Message message, String response, boolean mention) {
        message.reply(response).mentionRepliedUser(mention).queue(success -> {}, error -> {
            MessageService.SendSimpleMessage(message.getChannel(), response);
        });
    }

    public static void SendDirectMessage(User user, List<EmbedBuilder> message) {
        List<MessageEmbed> embeds = message.stream().map(EmbedBuilder::build).toList();

        user.openPrivateChannel().queue(channel -> {
            channel.sendMessageEmbeds(embeds).queue();
        });
    }

    public static void SendMessage(MessageChannel channel, List<EmbedBuilder> message, boolean mention) {
        List<MessageEmbed> embeds = message.stream().map(EmbedBuilder::build).toList();

        channel.sendMessageEmbeds(embeds).mentionRepliedUser(mention).queue();
    }

    public static void SendMessage(MessageChannel channel, InputStream file, String name, List<EmbedBuilder> message) {
        FileUpload fileUpload = FileUpload.fromData(file, name);
        List<MessageEmbed> embeds = message.stream().map(EmbedBuilder::build).toList();

        channel.sendFiles(fileUpload).mentionRepliedUser(false).setEmbeds(embeds).queue();
    }

    public static Message SendMessageBlocking(MessageChannel channel, List<EmbedBuilder> message) {
        List<MessageEmbed> embeds = message.stream().map(EmbedBuilder::build).toList();

        return channel.sendMessageEmbeds(embeds).mentionRepliedUser(false).complete();
    }

    public static void SendSimpleMessage(MessageChannel channel, String message) {
        channel.sendMessage(message).mentionRepliedUser(false).queue();
    }
}
