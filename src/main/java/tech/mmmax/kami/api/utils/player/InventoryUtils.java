package tech.mmmax.kami.api.utils.player;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import tech.mmmax.kami.api.wrapper.IMinecraft;

public class InventoryUtils implements IMinecraft {

    public static int getHotbarItemSlot(Item item) {
        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            if (InventoryUtils.mc.player.inventory.getStackInSlot(i).getItem().equals(item)) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getHotbarItemSlot2(Item item) {
        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            if (InventoryUtils.mc.player.inventory.getStackInSlot(i).getItem().equals(item)) {
                slot = i;
                break;
            }
        }

        return slot == -1 ? InventoryUtils.mc.player.inventory.currentItem : slot;
    }

    public static void switchToSlot(int slot) {
        InventoryUtils.mc.player.inventory.currentItem = slot;
    }

    public static void switchToSlot(Item item) {
        InventoryUtils.mc.player.inventory.currentItem = getHotbarItemSlot2(item);
    }

    public static void switchToSlotGhost(int slot) {
        InventoryUtils.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
    }

    public static void switchToSlotGhost(Item item) {
        switchToSlotGhost(getHotbarItemSlot2(item));
    }

    public static int getItemCount(Item item) {
        boolean count = false;
        int count1 = InventoryUtils.mc.player.inventory.mainInventory.stream().filter((itemStack) -> {
            return itemStack.getItem().equals(item);
        }).mapToInt(ItemStack::getCount).sum();

        return count1;
    }

    public static int getInventoryItemSlot(Item item) {
        int slot = -1;

        for (int i = 45; i > 0; --i) {
            if (InventoryUtils.mc.player.inventory.getStackInSlot(i).getItem().equals(item)) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static void moveItemToOffhand(int slot) {
        boolean startMoving = true;
        boolean moving = false;
        boolean returning = false;
        int returnSlot = 0;

        if (slot != -1) {
            if (!moving && startMoving) {
                InventoryUtils.mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, InventoryUtils.mc.player);
                moving = true;
                startMoving = false;
            }

            if (moving) {
                InventoryUtils.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, InventoryUtils.mc.player);
                moving = false;
                returning = true;
            }

            if (returning) {
                for (int i = 0; i < 45; ++i) {
                    if (InventoryUtils.mc.player.inventory.getStackInSlot(i).isEmpty()) {
                        returnSlot = i;
                        break;
                    }
                }

                if (returnSlot != -1) {
                    InventoryUtils.mc.playerController.windowClick(0, returnSlot < 9 ? returnSlot + 36 : returnSlot, 0, ClickType.PICKUP, InventoryUtils.mc.player);
                }

                returning = false;
            }

            startMoving = true;
        }
    }

    public static void moveItemToOffhand(int slot, int returnSlot) {
        boolean startMoving = true;
        boolean moving = false;
        boolean returning = false;

        if (slot != -1) {
            if (!moving && startMoving) {
                InventoryUtils.mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, InventoryUtils.mc.player);
                moving = true;
                startMoving = false;
            }

            if (moving) {
                InventoryUtils.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, InventoryUtils.mc.player);
                moving = false;
                returning = true;
            }

            if (returning) {
                if (returnSlot != -1) {
                    InventoryUtils.mc.playerController.windowClick(0, returnSlot < 9 ? returnSlot + 36 : returnSlot, 0, ClickType.PICKUP, InventoryUtils.mc.player);
                }

                returning = false;
            }

            startMoving = true;
        }
    }

    public static void moveItemToOffhand(Item item) {
        int slot = getInventoryItemSlot(item);

        if (slot != -1) {
            moveItemToOffhand(slot);
        }

    }

    public static void moveItem(int slot, int slotOut) {
        boolean startMoving = true;
        boolean moving = false;
        boolean returning = false;

        if (!moving && startMoving) {
            InventoryUtils.mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, InventoryUtils.mc.player);
            moving = true;
            startMoving = false;
        }

        if (moving) {
            InventoryUtils.mc.playerController.windowClick(0, slotOut < 9 ? slotOut + 36 : slotOut, 0, ClickType.PICKUP, InventoryUtils.mc.player);
            moving = false;
            returning = true;
        }

        if (returning) {
            InventoryUtils.mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, InventoryUtils.mc.player);
            returning = false;
        }

        startMoving = true;
    }

    public static void moveItem(Item item, int slot) {
        moveItem(getInventoryItemSlot(item), slot);
    }
}
