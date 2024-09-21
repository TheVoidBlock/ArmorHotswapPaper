package com.thevoidblock.ArmorHotswapPaper;

import com.destroystokyo.paper.MaterialTags;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class ArmorHotswapPaper extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        ItemStack handItem = null;
        if(event.getHand() == EquipmentSlot.HAND && checkEquipable(inventory.getItemInMainHand().getType())) {
            handItem = inventory.getItemInMainHand();
        } else if(event.getHand() == EquipmentSlot.OFF_HAND && checkEquipable(inventory.getItemInOffHand().getType())) {
            handItem = inventory.getItemInOffHand();
        }

        if(
                handItem != null && !handItem.getType().isBlock() &&
                (
                    event.getAction() == Action.RIGHT_CLICK_AIR ||
                    event.getAction() == Action.RIGHT_CLICK_BLOCK
                )
        ) {

            if(
                    !(event.getHand() == EquipmentSlot.OFF_HAND && checkEquipable(inventory.getItemInMainHand().getType())) &&
                    checkSwappable(handItem.getType(), inventory) &&
                    handItem.getAmount() == 1
            ) {

                swingHand(event.getHand(), player);
                swapWithArmor(handItem, event.getHand(), inventory);
            }
        }
    }

    private void swapWithArmor(ItemStack item, EquipmentSlot hand, PlayerInventory inventory) {
        if(hand != EquipmentSlot.HAND && hand != EquipmentSlot.OFF_HAND) throw new IllegalStateException("Attempted to swap armor with a slot besides a hand. This shouldn't happen.");

        Material itemType = item.getType();
        if(MaterialTags.HEAD_EQUIPPABLE.isTagged(itemType)) {setHandItem(hand, inventory.getHelmet(), inventory); inventory.setHelmet(item); return;}
        if(MaterialTags.CHEST_EQUIPPABLE.isTagged(itemType)) {setHandItem(hand, inventory.getChestplate(), inventory); inventory.setChestplate(item); return;}
        if(MaterialTags.LEGGINGS.isTagged(itemType)) {setHandItem(hand, inventory.getLeggings(), inventory); inventory.setLeggings(item); return;}
        if(MaterialTags.BOOTS.isTagged(itemType)) {setHandItem(hand, inventory.getBoots(), inventory); inventory.setBoots(item);}
    }

    private void setHandItem(EquipmentSlot hand, ItemStack item, PlayerInventory inventory) {
        if(hand == EquipmentSlot.HAND) { inventory.setItemInMainHand(item); return;}
        if(hand == EquipmentSlot.OFF_HAND) inventory.setItemInOffHand(item);
    }

    private void swingHand(EquipmentSlot hand, Player player) {
        if(hand == EquipmentSlot.HAND) {player.swingMainHand(); return;}
        if(hand == EquipmentSlot.OFF_HAND) player.swingOffHand();
    }

    private boolean checkSwappable(Material type, PlayerInventory inventory) {
        if(MaterialTags.HEAD_EQUIPPABLE.isTagged(type) && inventory.getHelmet() != null && !checkBinding(inventory.getHelmet())) return true;
        if(MaterialTags.CHEST_EQUIPPABLE.isTagged(type) && inventory.getChestplate() != null && !checkBinding(inventory.getChestplate())) return true;
        if(MaterialTags.LEGGINGS.isTagged(type) && inventory.getLeggings() != null && !checkBinding(inventory.getLeggings())) return true;
        return MaterialTags.BOOTS.isTagged(type) && inventory.getBoots() != null && !checkBinding(inventory.getBoots());
    }

    private boolean checkBinding(ItemStack item) {
        return item.getEnchantments().containsKey(Enchantment.BINDING_CURSE);
    }

    private boolean checkEquipable(Material type) {
        if(MaterialTags.HEAD_EQUIPPABLE.isTagged(type)) return true;
        if(MaterialTags.CHEST_EQUIPPABLE.isTagged(type)) return true;
        if(MaterialTags.LEGGINGS.isTagged(type)) return true;
        return MaterialTags.BOOTS.isTagged(type);
    }
}