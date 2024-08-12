package com.craftaro.ultimatefishing.bait;

import com.craftaro.core.chat.AdventureUtils;
import com.craftaro.ultimatefishing.UltimateFishing;
import com.craftaro.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.ultimatefishing.rarity.Rarity;
import com.craftaro.ultimatefishing.settings.Settings;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bait {

    private final String bait;
    private final String color;

    private Material material;
    private double sellPrice;

    // The amount of uses for this bait.
    private int uses;

    // The rarities this bait will improve the chances of catching.
    private List<Rarity> target;

    // The bonus percent chance.
    private double chanceBonus;

    private double criticalChance;

    private boolean enchanted;

    public Bait(String bait, String color, Material material, double sellPrice, int uses, List<Rarity> target, double chanceBonus, boolean enchanted, double criticalChance) {
        this.bait = bait;
        this.color = color;
        this.material = material;
        this.sellPrice = sellPrice;
        this.uses = uses;
        this.target = target;
        this.chanceBonus = chanceBonus;
        this.enchanted = enchanted;
        this.criticalChance = criticalChance;
    }

    public ItemStack asItemStack(int amount) {
        ItemStack itemStack = new ItemStack(material, amount);
//        ItemMeta meta = itemStack.getItemMeta();
//        meta.setDisplayName(TextUtils.formatText("&" + color)
//                + UltimateFishing.getInstance().getLocale().getMessage("object.bait.name")
//                .processPlaceholder("bait", bait).getMessage());
//        meta.setLore(Collections.singletonList(UltimateFishing.getInstance().getLocale()
//                .getMessage("object.bait.lore").getMessage()));
//        itemStack.setItemMeta(meta);

        AdventureUtils.formatItemName(itemStack, "&" + color + UltimateFishing.getInstance().getLocale().getMessage("object.bait.name").processPlaceholder("bait", bait).toText());
        AdventureUtils.formatItemLore(itemStack, Collections.singletonList(UltimateFishing.getInstance().getLocale().getMessage("object.bait.lore").toText()));

        if (enchanted)
            ItemUtils.addGlow(itemStack);


        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("bait", bait);

        return nbtItem.getItem();
    }

    public ItemStack asItemStack() {
        return asItemStack(1);
    }

    public ItemStack applyBait(ItemStack item) {
        //check for config settings if rod custom lore, enchants and displayname are allowed
        if (item.hasItemMeta()
                && ((!Settings.BAIT_ON_ROD_WITH_DISPLAYNAME.getBoolean() && item.getItemMeta().hasDisplayName() & item.getItemMeta().getDisplayName() != null && !item.getItemMeta().getDisplayName().isEmpty())
                || (!Settings.BAIT_ON_ROD_WITH_ENCHANTS.getBoolean() && item.getItemMeta().hasEnchants() && !item.getItemMeta().getEnchants().isEmpty())))
            return null;
        if (item.hasItemMeta() && !Settings.BAIT_ON_ROD_WITH_LORE.getBoolean() && item.getItemMeta().hasLore() && !item.getItemMeta().getLore().isEmpty()) {
            //check for custom lore
            int ignorelines = 0;
            NBTItem nbtItem = new NBTItem(item);
            if (nbtItem.hasKey("uses")) {
                ignorelines = 1;
            } else if (hasInvisibleString(item)) {
                ignorelines = 2;
            }
            if (item.getItemMeta().getLore().size() > ignorelines)
                return null;
        }

        int uses = 0;
        int max = 0;

        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey("uses")) {
            uses = nbtItem.getInteger("uses");
            max = nbtItem.getInteger("max");
        } else if (hasInvisibleString(item)) {
            String[] split = TextUtils.convertFromInvisibleString(item.getItemMeta().getLore().get(0)).split(":");
            uses = Integer.parseInt(split[1]);
            max = Integer.parseInt(split[2]);
        }
        return applyBait(item, uses, max + this.uses);
    }

    private ItemStack applyBait(ItemStack item, int uses, int max) {
        Bait currentBait = UltimateFishing.getInstance().getBaitManager().getBait(item);
        if (currentBait != null && !currentBait.getBait().equals(this.getBait())) //disallow multiple baits on same rod
            return null;

        int originalLoreIndex = 0;
        NBTItem nbtRod = new NBTItem(item);
        if (nbtRod.hasKey("max")) {
            originalLoreIndex = 1; //1st line of lore is bait description
        } else if (hasInvisibleString(item)) {
            originalLoreIndex = 2; //line 0 hidden string, line 1 description
        } //else nothing on rod

        List<String> originalLore = item.hasItemMeta() && item.getItemMeta().hasLore()
                ? item.getItemMeta().getLore().subList(originalLoreIndex, item.getItemMeta().getLore().size())
                : new ArrayList<>();
        String baited = UltimateFishing.getInstance().getLocale().getMessage("object.bait.baited")
                .processPlaceholder("bait", TextUtils.formatText("&" + color) + bait)
                .processPlaceholder("uses", max - uses)
                .processPlaceholder("max", max)
                .toText();

        ItemMeta meta = item.getItemMeta();
        originalLore.add(0, baited);
        meta.setLore(originalLore);
        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("bait", bait);
        nbtItem.setInteger("uses", uses); // Uses is the amount of uses the user has made not the amount left.
        nbtItem.setInteger("max", max);

        return nbtItem.getItem();
    }

    public String getBait() {
        return bait;
    }

    public String getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public List<Rarity> getTarget() {
        return target;
    }

    public double getChanceBonus() {
        return chanceBonus;
    }

    public void setChanceBonus(double chanceBonus) {
        this.chanceBonus = chanceBonus;
    }

    public double getCriticalChance() {
        return criticalChance;
    }

    public void setCriticalChance(double criticalChance) {
        this.criticalChance = criticalChance;
    }

    public ItemStack use(ItemStack item) {
        if (item == null) return null;
        int uses;
        int max;

        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey("uses")) {
            uses = nbtItem.getInteger("uses") + 1;
            max = nbtItem.getInteger("max");
        } else if (hasInvisibleString(item)) {
            String[] split = TextUtils.convertFromInvisibleString(item.getItemMeta().getLore().get(0)).split(":");
            uses = Integer.parseInt(split[1]) + 1;
            max = Integer.parseInt(split[2]);
        } else {
            return item;
        }

        if (uses < max)
            return applyBait(item, uses, max);
        else {
            nbtItem.setString("bait", "UNSET"); // Not sure why I had to do this.
            nbtItem.setInteger("uses", 0);
            nbtItem.setInteger("max", 0);
            ItemStack newItem = nbtItem.getItem();
            ItemMeta meta = newItem.getItemMeta();
            List<String> lore = meta.getLore();
            lore.remove(0);
            meta.setLore(lore);
            newItem.setItemMeta(meta);
            return newItem;
        }
    }

    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasInvisibleString(ItemStack item) {
        int invisibleStringColonIndex;
        String invisibleString;
        return item.getItemMeta().hasLore() && !item.getItemMeta().getLore().isEmpty()
                && (invisibleStringColonIndex = item.getItemMeta().getLore().get(0).indexOf(":")) >= 0 //check for first :
                && (invisibleStringColonIndex = item.getItemMeta().getLore().get(0).indexOf(":", invisibleStringColonIndex)) >= 0 //check for 2nd :
                && (invisibleStringColonIndex = item.getItemMeta().getLore().get(0).indexOf(":", invisibleStringColonIndex)) < 0 //check no furhter :, ie only 2 :
                && isInt((invisibleString = TextUtils.convertFromInvisibleString(item.getItemMeta().getLore().get(0))).split(":")[1]) //check uses is int
                && isInt(invisibleString.split(":")[2]);
    }
}
