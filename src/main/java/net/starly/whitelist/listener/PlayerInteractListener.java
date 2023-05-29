package net.starly.whitelist.listener;

import net.starly.core.jb.version.nms.tank.NmsItemStackUtil;
import net.starly.core.jb.version.nms.wrapper.ItemStackWrapper;
import net.starly.core.jb.version.nms.wrapper.NBTTagCompoundWrapper;
import net.starly.whitelist.WhitelistTicket;
import net.starly.whitelist.dispatcher.ChatInputDispatcher;
import net.starly.whitelist.dispatcher.PlayerQuitDispatcher;
import net.starly.whitelist.message.MessageContext;
import net.starly.whitelist.message.MessageType;
import net.starly.whitelist.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) return;


        MessageContext messageContext = MessageContext.getInstance();
        Player player = event.getPlayer();

        ItemStack handStack = player.getInventory().getItemInMainHand();
        if (handStack == null || handStack.getType() == Material.AIR) return;

        ItemStackWrapper stackWrapper = NmsItemStackUtil.getInstance().asNMSCopy(handStack);
        NBTTagCompoundWrapper tagWrapper = stackWrapper.getTag();
        if (tagWrapper == null) return;

        String tagValue = tagWrapper.getString("st-whitelistticket");
        if (tagValue == null || tagValue.isEmpty()) return;


        if (ChatInputDispatcher.getChatListener(player.getUniqueId()) != null) {
            messageContext.getMessageAfterPrefix(MessageType.ERROR, "alreadyPendingRequestExistsEnterName").ifPresent(player::sendMessage);
            return;
        }

        handStack.setAmount(handStack.getAmount() - 1);

        Consumer<String> chatListener = new Consumer<String>() {

            @Override
            public void accept(String message) {
                if (!PlayerUtil.hasMinecraftAccount(message)) {
                    messageContext.getMessageAfterPrefix(MessageType.ERROR, "targetNotFoundEnterAgain").ifPresent(player::sendMessage);
                    ChatInputDispatcher.attachListener(player.getUniqueId(), this);
                    return;
                }

                OfflinePlayer targetPlayer = WhitelistTicket.getInstance().getServer().getOfflinePlayer(message);
                if (targetPlayer == null) {
                    messageContext.getMessageAfterPrefix(MessageType.ERROR, "targetNotFoundEnterAgain").ifPresent(player::sendMessage);
                    ChatInputDispatcher.attachListener(player.getUniqueId(), this);
                    return;
                } else if (targetPlayer.isWhitelisted()) {
                    messageContext.getMessageAfterPrefix(MessageType.ERROR, "alreadyWhitelisted").ifPresent(player::sendMessage);
                    ChatInputDispatcher.attachListener(player.getUniqueId(), this);
                    return;
                }

                targetPlayer.setWhitelisted(true);

                messageContext.getMessageAfterPrefix(MessageType.NORMAL, "successfullyAddedPlayer").map(value -> value.replace("{player}", targetPlayer.getName())).ifPresent(player::sendMessage);
            }
        };
        ChatInputDispatcher.attachListener(player.getUniqueId(), chatListener);

        Consumer<Player> quitListener = new Consumer<Player>() {

            @Override
            public void accept(Player player) {
                ChatInputDispatcher.removeChatListener(player.getUniqueId());

                ItemStack copyOfHandStack = handStack.clone();
                copyOfHandStack.setAmount(1);

                Map<Integer, ItemStack> loses = player.getInventory().addItem(copyOfHandStack);
                if (!loses.isEmpty()) player.getWorld().dropItem(player.getLocation(), copyOfHandStack);
            }
        };
        PlayerQuitDispatcher.attachListener(player.getUniqueId(), quitListener);

        messageContext.getMessageAfterPrefix(MessageType.NORMAL, "enterTargetName").ifPresent(player::sendMessage);
    }
}
