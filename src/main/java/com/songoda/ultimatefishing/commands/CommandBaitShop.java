package com.songoda.ultimatefishing.commands;

import com.songoda.ultimatefishing.UltimateFishing;
import com.songoda.ultimatefishing.gui.GUIBaitShop;
import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.gui.GuiManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandBaitShop extends AbstractCommand {

    private final UltimateFishing plugin;
    private final GuiManager guiManager;

    public CommandBaitShop(UltimateFishing plugin, GuiManager guiManager) {
        super(CommandType.PLAYER_ONLY, "baitshop");
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        guiManager.showGUI((Player) sender, new GUIBaitShop(plugin, null));
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatefishing.baitshop";
    }

    @Override
    public String getSyntax() {
        return "baitshop";
    }

    @Override
    public String getDescription() {
        return "Open the bait shop.";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
