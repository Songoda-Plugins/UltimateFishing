package com.craftaro.ultimatefishing.commands;

import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.ultimatefishing.bait.Bait;
import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandGive extends AbstractCommand {

    private final UltimateFishing plugin;

    public CommandGive(UltimateFishing plugin) {
        super(CommandType.CONSOLE_OK, "give");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length != 3) return ReturnType.SYNTAX_ERROR;

        OfflinePlayer player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            plugin.getLocale().newMessage("&cThat player does not exist or is currently offline.").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Bait bait = plugin.getBaitManager().getBait(args[1].replace("_", " "));

        if (bait == null) {
            plugin.getLocale().newMessage("&cThe bait &4" + args[1] + "&c does not exist...").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (!isInt(args[2])) {
            plugin.getLocale().newMessage("&4" + args[2] + "&c is not a number...").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (sender != player)
            plugin.getLocale().newMessage("&7You gave &6" + player.getName() + " " + args[2] + " &" + bait.getColor() + bait.getBait() + "&7.")
                    .sendPrefixedMessage(sender);

        plugin.getLocale().getMessage("event.bait.given")
                .processPlaceholder("amount", args[2])
                .processPlaceholder("bait", TextUtils.formatText("&" + bait.getColor() + bait.getBait()))
                .sendPrefixedMessage(player.getPlayer());

        player.getPlayer().getInventory().addItem(bait.asItemStack(Integer.parseInt(args[2])));

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatefishing.give";
    }

    @Override
    public String getSyntax() {
        return "give <player> <bait> <amount>";
    }

    @Override
    public String getDescription() {
        return "Open the sell GUI.";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        }
        if (args.length == 2) {
            return plugin.getBaitManager().getBaits().stream().map(b ->
                    b.getBait().replace(" ", "_")).collect(Collectors.toList());
        }
        if (args.length == 3) {
            return Arrays.asList("1", "2", "3");
        }
        return null;
    }

    public static boolean isInt(String number) {
        if (number == null || number.equals(""))
            return false;
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
