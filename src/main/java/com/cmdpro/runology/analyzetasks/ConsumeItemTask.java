package com.cmdpro.runology.analyzetasks;

import com.cmdpro.runology.api.AnalyzeTask;
import com.cmdpro.runology.api.AnalyzeTaskSerializer;
import com.cmdpro.runology.init.AnalyzeTaskInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class ConsumeItemTask extends AnalyzeTask {
    public ItemStack[] items;
    public ConsumeItemTask(ItemStack[] items) {
        this.items = items;
    }
    @Override
    public void render(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset) {
        int offset = (int)-(((items.length-1)*24f)/2f);
        for (ItemStack i : items) {
            pGuiGraphics.renderItem(i, ((xOffset+88)+offset)-8, (yOffset+40)-8);
            pGuiGraphics.renderItemDecorations(Minecraft.getInstance().font, i, ((xOffset+88)+offset)-8, (yOffset+40)-8);
            offset += 24;
        }
    }

    @Override
    public void renderPost(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, int xOffset, int yOffset) {
        int offset = (int)-(((items.length-1)*24f)/2f);
        for (ItemStack i : items) {
            if (pMouseX >= ((xOffset+88)+offset)-8 && pMouseY >= (yOffset+40)-8 && pMouseX <= ((xOffset+88)+offset)+8 && pMouseY <= (yOffset+40)+8) {
                pGuiGraphics.renderTooltip(Minecraft.getInstance().font, i, pMouseX, pMouseY);
            }
            offset += 24;
        }
    }

    @Override
    public boolean canComplete(Player player) {
        boolean hasAll = true;
        for (ItemStack i : items) {
            if (!invHas(player.getInventory(), i)) {
                hasAll = false;
                break;
            }
        }
        return hasAll;
    }
    public boolean invHas(Inventory inv, ItemStack pStack) {
        int remaining = pStack.getCount();
        for(List<ItemStack> list : inv.compartments) {
            for(ItemStack itemstack : list) {
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(itemstack, pStack)) {
                    remaining -= itemstack.getCount();
                }
            }
        }
        return remaining <= 0;
    }

    @Override
    public void onComplete(Player player) {
        for (ItemStack i : items) {
            player.getInventory().clearOrCountMatchingItems((item) -> ItemStack.isSameItemSameTags(i, item), i.getCount(), player.inventoryMenu.getCraftSlots());
        }
    }

    @Override
    public AnalyzeTaskSerializer getSerializer() {
        return AnalyzeTaskInit.CONSUMEITEM.get();
    }
}
