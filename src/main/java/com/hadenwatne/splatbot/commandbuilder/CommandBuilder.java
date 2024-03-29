package com.hadenwatne.splatbot.commandbuilder;

import com.hadenwatne.splatbot.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.List;
import java.util.regex.Pattern;

public class CommandBuilder {
    public static CommandStructure Create(String name, String description) {
        return new CommandStructure(name, description);
    }

    public static CommandData BuildSlashCommandData(Command command) {
        CommandStructure structure = command.getCommandStructure();
        String description = structure.getDescription();
        String shortDesc = description.length() > 100 ? (description.substring(0, 96) + "...") : description;
        SlashCommandData data = Commands.slash(structure.getName(), shortDesc);

        data.setGuildOnly(command.requiresGuild());

        // If there are subcommands, add these instead.
        if (structure.getSubCommands().size() > 0 || structure.getSubcommandGroups().size() > 0) {
            // Add SubCommands.
            for (CommandStructure subCommand : structure.getSubCommands()) {
                String subShortDesc = subCommand.getDescription().length() > 100 ? (subCommand.getDescription().substring(0, 96) + "...") : subCommand.getDescription();
                SubcommandData subCommandData = new SubcommandData(subCommand.getName(), subShortDesc);

                // Build sub command's parameter data.
                for (CommandParameter p : subCommand.getParameters()) {
                    OptionData option = buildCommandOptionData(p);

                    subCommandData.addOptions(option);
                }

                data.addSubcommands(subCommandData);
            }

            // Add SubCommand groups.
            for(SubCommandGroup subCommandGroup : structure.getSubcommandGroups()) {
                SubcommandGroupData group = new SubcommandGroupData(subCommandGroup.getName(), subCommandGroup.getDescription());

                for (CommandStructure subCommand : subCommandGroup.getSubCommands()) {
                    String subShortDesc = subCommand.getDescription().length() > 100 ? (subCommand.getDescription().substring(0, 96) + "...") : subCommand.getDescription();
                    SubcommandData subCommandData = new SubcommandData(subCommand.getName(), subShortDesc);

                    // Build sub command's parameter data.
                    for (CommandParameter p : subCommand.getParameters()) {
                        OptionData option = buildCommandOptionData(p);

                        subCommandData.addOptions(option);
                    }

                    group.addSubcommands(subCommandData);
                }

                data.addSubcommandGroups(group);
            }
        } else {
            // Build primary command's parameter data.
            for (CommandParameter p : structure.getParameters()) {
                OptionData option = buildCommandOptionData(p);

                data.addOptions(option);
            }
        }

        return data;
    }

    public static Pattern BuildPrimaryPattern(CommandStructure command) {
        StringBuilder primary = new StringBuilder();
        primary.append("^(?<command>(");
        primary.append("(");
        primary.append(command.getName());
        primary.append(")");

        for(String alias : command.getAliases()) {
            primary.append("|");
            primary.append("(");
            primary.append(alias);
            primary.append(")");
        }

        primary.append(")\\b)");

        return Pattern.compile(primary.toString(), Pattern.CASE_INSENSITIVE);
    }

    public static Pattern BuildContextPattern(CommandStructure command) {
        StringBuilder sb = new StringBuilder();

        // Build pattern for subCommandGroups.
        boolean hasSubCommandGroups = command.getSubcommandGroups().size() > 0;

        if (hasSubCommandGroups) {
            for (SubCommandGroup group : command.getSubcommandGroups()) {
                if (sb.length() > 0) {
                    sb.append("|");
                }

                sb.append("(");

                if (group.getAliases().size() > 0) {
                    sb.append("(");

                    // Add the main command
                    sb.append("(");
                    sb.append(group.getName());
                    sb.append(")");

                    // Add aliases
                    for (String alias : group.getAliases()) {
                        sb.append("|");
                        sb.append("(");
                        sb.append(alias);
                        sb.append(")");
                    }

                    sb.append(")");
                } else {
                    sb.append(group.getName());
                }

                sb.append("\\s");
                sb.append("(");
                sb.append(BuildSubCommandPattern(group.getSubCommands()));
                sb.append("))");
            }
        }

        // Make sure that additional SubCommands are OR'd properly.
        if (hasSubCommandGroups) {
            sb.append("|(");
        }

        // Build pattern for subcommands.
        if (command.getSubCommands().size() > 0) {
            sb.append(BuildSubCommandPattern(command.getSubCommands()));
        }

        // If the command has parameters of its own, add them after.
        sb.append(BuildParameterPattern(command));

        // Make sure that additional SubCommands are OR'd properly.
        if (hasSubCommandGroups) {
            sb.append(")");
        }

        String context = "(?<context>" +
                sb +
                ")?";

        return Pattern.compile(context, Pattern.CASE_INSENSITIVE);
    }

    public static Pattern BuildMatcherPattern(CommandStructure command) {
        String matcherPattern = command.getPrimaryPattern().pattern() +
                "\\s?" +
                command.getContextPattern().pattern() +
                "$";

        return Pattern.compile(matcherPattern, Pattern.CASE_INSENSITIVE);
    }

    public static String BuildUsage(CommandStructure command, boolean boldCommandName) {
        StringBuilder sb = new StringBuilder();
        List<CommandStructure> subCommands = command.getSubCommands();
        List<SubCommandGroup> subCommandGroups = command.getSubcommandGroups();

        if(boldCommandName) {
            sb.append("**");
        }

        sb.append(command.getName());

        if(boldCommandName) {
            // We currently bold the subcommand only, so if this command is being bolded,
            // add the first alias it has.
            if(command.getAliases().size() > 0) {
                sb.append(" ");
                sb.append("(");
                sb.append(command.getAliases().get(0));
                sb.append(")");
            }

            sb.append("**");
        }

        // Add subcommands and groups.
        if(subCommands.size() > 0 || subCommandGroups.size() > 0) {
            sb.append("...");
            sb.append("\n• ");

            if(subCommands.size() > 0) {
                sb.append(buildSubCommandUsageLabel(subCommands));
            }

            if(subCommandGroups.size() > 0) {
                if(subCommands.size() > 0 ) {
                    sb.append("\n• ");
                }

                sb.append(buildSubCommandGroupUsageLabel(subCommandGroups));
            }
        }

        // Now add any command parameters.
        for (CommandParameter p : command.getParameters()) {
            sb.append(" ");

            if (p.isRequired()) {
                sb.append("<");
                sb.append(buildUsageLabel(p));
                sb.append(">");
            } else {
                sb.append("_");
                sb.append("[");
                sb.append(buildUsageLabel(p));
                sb.append("]");
                sb.append("_");
            }
        }

        return sb.toString();
    }

    public static String BuildExample(CommandStructure command) {
        StringBuilder example = new StringBuilder();
        List<CommandStructure> subCommands = command.getSubCommands();
        List<SubCommandGroup> subCommandGroups = command.getSubcommandGroups();

        example.append(command.getName());

        if(subCommands.size() > 0 || subCommandGroups.size() > 0) {
            example.append("...");

            for(CommandStructure subCommand : subCommands) {
                example.append(System.lineSeparator());

                example.append(command.getName());
                example.append(" ");
                example.append(subCommand.getName());

                for(CommandParameter parameter : subCommand.getParameters()) {
                    example.append(" ");
                    example.append(parameter.getExample());
                }
            }

            for(SubCommandGroup subCommandGroup: subCommandGroups) {
                for(CommandStructure subCommand : subCommandGroup.getSubCommands()) {
                    example.append(System.lineSeparator());

                    example.append(command.getName());
                    example.append(" ");
                    example.append(subCommandGroup.getName());
                    example.append(" ");
                    example.append(subCommand.getName());

                    for(CommandParameter parameter : subCommand.getParameters()) {
                        if(parameter.getExample() != null && parameter.getExample().length() > 0) {
                            example.append(" ");
                            example.append(parameter.getExample());
                        }
                    }
                }
            }
        } else {
            for(CommandParameter parameter : command.getParameters()) {
                example.append(" ");
                example.append(parameter.getExample());
            }
        }

        return example.toString();
    }

    private static OptionData buildCommandOptionData(CommandParameter p) {
        OptionData option = new OptionData(MapParameterType(p.getType()), p.getName().toLowerCase(), p.getDescription())
                .setRequired(p.isRequired());

        if (p.getSelectionOptions().size() > 0) {
            for (String so : p.getSelectionOptions()) {
                option.addChoice(so, so);
            }
        }

        return option;
    }

    private static String BuildSubCommandPattern(List<CommandStructure> subCommands) {
        StringBuilder scb = new StringBuilder();

        for (CommandStructure subCommand : subCommands) {
            if(scb.length() > 0) {
                scb.append("|");
            }

            // Begins the subcommand group.
            scb.append("(");

            // Add subcommand name & aliases
            if(subCommand.getAliases().size() > 0) {
                // Begin a group to OR each name and alias.
                scb.append("((");
                scb.append(subCommand.getName());
                scb.append(")");

                // Add each additional alias as an OR group.
                for(String alias : subCommand.getAliases()) {
                    scb.append("|(");
                    scb.append(alias);
                    scb.append(")");
                }

                // End the alias group
                scb.append(")");
            } else {
                scb.append(subCommand.getName());
            }

            // Add subcommand parameters
            if(subCommand.getParameters().size() > 0) {
                boolean anySubCommandParameterRequired = subCommand.getParameters().stream().anyMatch(CommandParameter::isRequired);

                if(!anySubCommandParameterRequired) {
                    scb.append("\\b");
                }

                scb.append("\\s");

                if(!anySubCommandParameterRequired) {
                    scb.append("?(");
                }

                scb.append(BuildParameterPattern(subCommand));

                if(!anySubCommandParameterRequired) {
                    scb.append(")?");
                }
            }

            // Ends the subcommand group.
            scb.append(")");
        }

        return scb.toString();
    }

    private static String BuildParameterPattern(CommandStructure command) {
        StringBuilder sb = new StringBuilder();

        for(CommandParameter p : command.getParameters()) {
            if(sb.length() > 0) {
                if(!p.isRequired()) {
                    sb.append("(\\s");
                    sb.append(p.getPattern().pattern());
                    sb.append(")?");
                    continue;
                }else{
                    sb.append("\\s");
                }
            }

            sb.append(p.getPattern().pattern());

            if(!p.isRequired()) {
                sb.append("?");
            }
        }

        return sb.toString();
    }

    private static String buildSubCommandUsageLabel(List<CommandStructure> subCommands) {
        StringBuilder subCommandData = new StringBuilder();

        for(CommandStructure subCommand : subCommands) {
            if(subCommandData.length() > 0) {
                subCommandData.append("\n• ");
            }

            subCommandData.append(BuildUsage(subCommand, true));
        }

        return subCommandData.toString();
    }

    private static String buildSubCommandGroupUsageLabel(List<SubCommandGroup> subCommandGroups) {
        StringBuilder subCommandGroupData = new StringBuilder();

        for(SubCommandGroup group : subCommandGroups) {
            StringBuilder subCommandData = new StringBuilder();

            if (subCommandGroupData.length() > 0) {
                subCommandGroupData.append("\n• ");
            }

            for (CommandStructure subCommand : group.getSubCommands()) {
                if (subCommandData.length() > 0) {
                    subCommandData.append("\n• ");
                }

                subCommandData.append("**");
                subCommandData.append(group.getName());

                if(group.getAliases().size() > 0) {
                    subCommandData.append(" ");
                    subCommandData.append("(");
                    subCommandData.append(group.getAliases().get(0));
                    subCommandData.append(")");
                }

                subCommandData.append("**");
                subCommandData.append(" ");
                subCommandData.append(BuildUsage(subCommand, true));
            }

            subCommandGroupData.append(subCommandData);
        }

        return subCommandGroupData.toString();
    }

    private static String buildUsageLabel(CommandParameter p) {
        StringBuilder sb = new StringBuilder();

        if(p.getType() == ParameterType.SELECTION) {
            sb.append("[");

            StringBuilder ssb = new StringBuilder();

            for(String s : p.getSelectionOptions()) {
                if(ssb.length() > 0) {
                    ssb.append(" | ");
                }

                ssb.append(s);
            }

            sb.append(ssb);
            sb.append("]");
        } else if(p.getType() == ParameterType.TIMECODE) {
            sb.append("#[y|d|h|m|s]");
        } else {
            sb.append(p.getName());
        }

        return sb.toString();
    }

    private static OptionType MapParameterType(ParameterType type) {
        switch(type) {
            case INTEGER:
                return OptionType.INTEGER;
            case BOOLEAN:
                return OptionType.BOOLEAN;
            case DISCORD_ROLE:
                return OptionType.ROLE;
            case DISCORD_USER:
                return OptionType.USER;
            case DISCORD_CHANNEL:
                return OptionType.CHANNEL;
            default:
                return OptionType.STRING;
        }
    }
}
