package com.craftaro.ultimatefishing.commands;

import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.ultimatefishing.rarity.Rarity;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandSellAll extends AbstractCommand {

    private final UltimateFishing plugin;

    public CommandSellAll(UltimateFishing plugin) {
        super(CommandType.PLAYER_ONLY, "sellall");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {

        Player player = (Player) sender;

        double totalNew = UltimateFishing.calculateTotalValue(player.getInventory());
        if (totalNew == 0) {
            plugin.getLocale().getMessage("command.sellall.fail").sendPrefixedMessage(player);
            return ReturnType.SUCCESS;
        }

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) continue;

            Rarity rarity = plugin.getRarityManager().getRarity(itemStack);

            if (rarity == null) continue;
            player.getInventory().remove(itemStack);
        }

        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
            ItemStack itemStack = player.getInventory().getItemInOffHand();
            Rarity rarity = plugin.getRarityManager().getRarity(itemStack);
            if (rarity != null)
                player.getInventory().setItemInOffHand(null);
        }

        EconomyManager.deposit(player, totalNew);

        plugin.getLocale().getMessage("command.sellall.success")
                .processPlaceholder("total", plugin.formatEconomy(totalNew))
                .sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatefishing.sellall";
    }

    @Override
    public String getSyntax() {
        return "sellall";
    }

    @Override
    public String getDescription() {
        return "Sells all fish in your inventory.";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}