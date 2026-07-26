package com.xyp.gtnc.Common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import com.xyp.gtnc.Loader.QuestLoader;

/** Updates the packaged BetterQuesting task line without resetting quest progress. */
public class CommandQuestUpdate extends CommandBase {

    @Override
    public String getCommandName() {
        return "gtnc_quests";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/gtnc_quests update";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!sender.canCommandSenderUseCommand(getRequiredPermissionLevel(), getCommandName())) {
            sender.addChatMessage(new ChatComponentTranslation("commands.error.perm"));
            return;
        }
        if (args.length != 1 || !"update".equalsIgnoreCase(args[0])) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        if (QuestLoader.updateTasks()) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "已更新 Back to future 任务线。"));
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "任务线更新失败，请查看服务端日志。"));
        }
    }
}
