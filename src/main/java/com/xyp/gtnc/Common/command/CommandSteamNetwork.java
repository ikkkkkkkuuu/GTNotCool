package com.xyp.gtnc.Common.command;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.xyp.gtnc.utils.world.steam.SteamWirelessNetworkManager;

import gregtech.common.misc.spaceprojects.SpaceProjectManager;

public class CommandSteamNetwork extends CommandBase {

    @Override
    public String getCommandName() {
        return "steam_network";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/steam_network <add|set|join|display> [player] [amount]";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        String currentArg = args.length == 0 ? "" : args[args.length - 1].trim();

        if (args.length == 1) {
            Stream.of("add", "set", "join", "display")
                .filter(s -> s.startsWith(currentArg))
                .forEach(completions::add);
        } else if (args.length == 2) {
            completions.addAll(
                getListOfStringsMatchingLastWord(
                    args,
                    MinecraftServer.getServer()
                        .getAllUsernames()));
        } else if (args.length == 3 && "join".equalsIgnoreCase(args[0])) {
            completions.addAll(
                getListOfStringsMatchingLastWord(
                    args,
                    MinecraftServer.getServer()
                        .getAllUsernames()));
        }
        return completions;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (!canUseOpCommand(sender)) return;
                String username = args.length > 1 ? args[1] : sender.getCommandSenderName();
                UUID uuid = SpaceProjectManager.getPlayerUUIDFromName(username);
                BigInteger amount = new BigInteger(args[2]);
                String formattedName = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;
                String formattedAmount = EnumChatFormatting.RED + NumberFormatUtil.formatNumber(amount)
                    + EnumChatFormatting.RESET;

                if (SteamWirelessNetworkManager.addSteamToGlobalSteamMap(uuid, amount)) {
                    sender.addChatMessage(
                        new ChatComponentText(
                            "Successfully added " + formattedAmount + " Steam to " + formattedName + "'s network."));
                } else {
                    sender.addChatMessage(
                        new ChatComponentText(
                            "Failed to add " + formattedAmount + " Steam to " + formattedName + "'s network."));
                }
                sender.addChatMessage(
                    new ChatComponentText(
                        formattedName + " currently has "
                            + EnumChatFormatting.RED
                            + NumberFormatUtil.formatNumber(SteamWirelessNetworkManager.getUserSteam(uuid))
                            + EnumChatFormatting.RESET
                            + " Steam."));
            }
            case "set" -> {
                if (!canUseOpCommand(sender)) return;
                String username = args.length > 1 ? args[1] : sender.getCommandSenderName();
                UUID uuid = SpaceProjectManager.getPlayerUUIDFromName(username);
                BigInteger amount = new BigInteger(args[2]);
                String formattedName = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;

                if (amount.compareTo(BigInteger.ZERO) < 0) {
                    sender.addChatMessage(new ChatComponentText("Cannot set steam network to a negative value."));
                    break;
                }
                SteamWirelessNetworkManager.setUserSteam(uuid, amount);
                sender.addChatMessage(
                    new ChatComponentText(
                        "Set " + formattedName
                            + "'s steam network to "
                            + EnumChatFormatting.RED
                            + NumberFormatUtil.formatNumber(amount)
                            + EnumChatFormatting.RESET
                            + " Steam."));
            }
            case "join" -> {
                String subject, team;
                if (args.length >= 3) {
                    subject = args[1];
                    team = args[2];
                } else if (args.length == 2) {
                    subject = sender.getCommandSenderName();
                    team = args[1];
                } else {
                    sender
                        .addChatMessage(new ChatComponentText("Usage: /steam_network join <your_name> <target_name>"));
                    break;
                }

                UUID uuidSubject = SpaceProjectManager.getPlayerUUIDFromName(subject);
                UUID uuidTeam = SpaceProjectManager.getLeader(SpaceProjectManager.getPlayerUUIDFromName(team));
                String formattedSubject = EnumChatFormatting.BLUE + subject + EnumChatFormatting.RESET;
                String formattedTeam = EnumChatFormatting.BLUE + team + EnumChatFormatting.RESET;

                if (uuidSubject.equals(uuidTeam)) {
                    SpaceProjectManager.putInTeam(uuidSubject, uuidSubject);
                    sender.addChatMessage(
                        new ChatComponentText(formattedSubject + " has left and rejoined their own steam network."));
                    break;
                }
                if (SpaceProjectManager.getLeader(uuidSubject)
                    .equals(uuidTeam)) {
                    sender.addChatMessage(new ChatComponentText("They are already in the same network!"));
                    break;
                }

                UUID senderUUID = SpaceProjectManager.getPlayerUUIDFromName(sender.getCommandSenderName());
                if (!SpaceProjectManager.getLeader(senderUUID)
                    .equals(uuidTeam) && !canUseOpCommandRaw(sender)) {
                    sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
                    break;
                }

                SpaceProjectManager.putInTeam(uuidSubject, uuidTeam);
                sender.addChatMessage(
                    new ChatComponentText(formattedSubject + " has joined " + formattedTeam + "'s steam network."));
                sender.addChatMessage(new ChatComponentText("To undo: /steam_network join " + subject + " " + subject));
            }
            case "display" -> {
                String username = args.length > 1 ? args[1] : sender.getCommandSenderName();
                UUID uuid = SpaceProjectManager.getPlayerUUIDFromName(username);
                String formattedName = EnumChatFormatting.BLUE + username + EnumChatFormatting.RESET;

                if (!SpaceProjectManager.isInTeam(uuid)) {
                    sender.addChatMessage(new ChatComponentText(formattedName + " has no steam network."));
                    break;
                }
                UUID teamUUID = SpaceProjectManager.getLeader(uuid);
                sender.addChatMessage(
                    new ChatComponentText(
                        formattedName + " has "
                            + EnumChatFormatting.RED
                            + NumberFormatUtil.formatNumber(SteamWirelessNetworkManager.getUserSteam(uuid))
                            + EnumChatFormatting.RESET
                            + " Steam in their network."));
                if (!uuid.equals(teamUUID)) {
                    sender.addChatMessage(
                        new ChatComponentText(
                            formattedName + " is in "
                                + EnumChatFormatting.BLUE
                                + SpaceProjectManager.getPlayerNameFromUUID(teamUUID)
                                + EnumChatFormatting.RESET
                                + "'s network."));
                }
            }
            default -> sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "Unknown command. Use: " + getCommandUsage(sender)));
        }
    }

    private boolean canUseOpCommand(ICommandSender sender) {
        if (!sender.canCommandSenderUseCommand(2, getCommandName())) {
            sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
            return false;
        }
        return true;
    }

    private boolean canUseOpCommandRaw(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(2, getCommandName());
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
