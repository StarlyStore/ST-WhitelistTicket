package net.starly.whitelist.command;

import net.starly.core.jb.version.nms.tank.NmsItemStackUtil;
import net.starly.core.jb.version.nms.wrapper.ItemStackWrapper;
import net.starly.core.jb.version.nms.wrapper.NBTTagCompoundWrapper;
import net.starly.whitelist.WhitelistTicket;
import net.starly.whitelist.message.MessageContext;
import net.starly.whitelist.message.MessageLoader;
import net.starly.whitelist.message.MessageType;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class WhitelistTicketCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MessageContext messageContext = MessageContext.getInstance();
        if (!(sender instanceof Player)) {
            messageContext.getMessageAfterPrefix(MessageType.ERROR, "wrongPlatform").ifPresent(sender::sendMessage);
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            messageContext.getMessageAfterPrefix(MessageType.ERROR, "wrongCommand").ifPresent(player::sendMessage);
            return false;
        }

        switch (args[0]) {

            case "리로드": {
                if (!player.hasPermission("starly.whitelist.reload")) {
                    messageContext.getMessageAfterPrefix(MessageType.ERROR, "noPermission").ifPresent(player::sendMessage);
                    return false;
                } else if (args.length != 1) {
                    messageContext.getMessageAfterPrefix(MessageType.ERROR, "wrongCommand").ifPresent(player::sendMessage);
                    return false;
                }


                WhitelistTicket plugin = WhitelistTicket.getInstance();

                File messageConfigFile = new File(plugin.getDataFolder(), "message.yml");
                if (!messageConfigFile.exists()) plugin.saveResource("message.yml", false);
                MessageLoader.load(YamlConfiguration.loadConfiguration(messageConfigFile));

                messageContext.getMessageAfterPrefix(MessageType.NORMAL, "reloadComplete").ifPresent(player::sendMessage);
                return false;
            }

            case "설정": {
                if (!player.hasPermission("starly.whitelist.set")) {
                    messageContext.getMessageAfterPrefix(MessageType.ERROR, "noPermission").ifPresent(player::sendMessage);
                    return false;
                } else if (args.length != 1) {
                    messageContext.getMessageAfterPrefix(MessageType.ERROR, "wrongCommand").ifPresent(player::sendMessage);
                    return false;
                }


                ItemStack handStack = player.getInventory().getItemInMainHand();
                if (handStack == null || handStack.getType() == Material.AIR) {
                    messageContext.getMessageAfterPrefix(MessageType.ERROR, "noItemInHand").ifPresent(player::sendMessage);
                    return false;
                }

                ItemStackWrapper stackWrapper = NmsItemStackUtil.getInstance().asNMSCopy(handStack);
                NBTTagCompoundWrapper tagWrapper = stackWrapper.getTag();
                if (tagWrapper == null) tagWrapper = NmsItemStackUtil.getInstance().getNbtCompoundUtil().newInstance();
                tagWrapper.setString("st-whitelistticket", "true");
                stackWrapper.setTag(tagWrapper);
                handStack = NmsItemStackUtil.getInstance().asBukkitCopy(stackWrapper);

                player.getInventory().setItemInMainHand(handStack);

                messageContext.getMessageAfterPrefix(MessageType.NORMAL, "whitelistTicketSet").ifPresent(player::sendMessage);
                return false;
            }

            default: {
                messageContext.getMessageAfterPrefix(MessageType.ERROR, "wrongCommand").ifPresent(player::sendMessage);
                return false;
            }
        }
    }
}
