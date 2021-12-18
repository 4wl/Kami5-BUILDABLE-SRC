package tech.mmmax.kami.impl.features.modules.player;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Mouse;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.management.FriendManager;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.chat.ChatMessage;
import tech.mmmax.kami.api.utils.chat.ChatUtils;
import tech.mmmax.kami.api.utils.player.InventoryUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class MiddleClick extends Module {

    Value friend = (new ValueBuilder()).withDescriptor("Friend").withValue(Boolean.valueOf(true)).register(this);
    Value pearl = (new ValueBuilder()).withDescriptor("Pearl").withValue(Boolean.valueOf(true)).register(this);
    Value xp = (new ValueBuilder()).withDescriptor("XP").withValue(Boolean.valueOf(true)).register(this);
    Value offhandSwap = (new ValueBuilder()).withDescriptor("Offhand Swap").withValue(Boolean.valueOf(false)).register(this);
    boolean hasPressed = false;

    public MiddleClick() {
        super("Middle Click", Feature.Category.Player);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (Mouse.isButtonDown(2)) {
                Entity pointed = MiddleClick.mc.getRenderManager().pointedEntity;
                int oldSlot;
                int xpSlot;

                if (!this.hasPressed) {
                    if (((Boolean) this.friend.getValue()).booleanValue() && pointed != null) {
                        if (FriendManager.INSTANCE.isFriend(pointed)) {
                            FriendManager.INSTANCE.removeFriend(pointed);
                            ChatUtils.sendMessage(new ChatMessage("Removed " + pointed.getName() + " from friends", false, 0));
                        } else {
                            FriendManager.INSTANCE.addFriend(pointed);
                            ChatUtils.sendMessage(new ChatMessage("Added " + pointed.getName() + " from friends", false, 0));
                        }
                    }

                    if (pointed == null && ((Boolean) this.pearl.getValue()).booleanValue() && this.allowPearl()) {
                        oldSlot = MiddleClick.mc.player.inventory.currentItem;
                        xpSlot = InventoryUtils.getHotbarItemSlot(Items.ENDER_PEARL);
                        if (xpSlot == -1 && !((Boolean) this.offhandSwap.getValue()).booleanValue()) {
                            ChatUtils.sendMessage(new ChatMessage("No pearls in hotbar", false, 0));
                            this.hasPressed = true;
                            return;
                        }

                        Item oldItem = MiddleClick.mc.player.getHeldItemOffhand().getItem();

                        if (((Boolean) this.offhandSwap.getValue()).booleanValue()) {
                            InventoryUtils.moveItemToOffhand(Items.ENDER_PEARL);
                        } else {
                            InventoryUtils.switchToSlotGhost(xpSlot);
                        }

                        MiddleClick.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(((Boolean) this.offhandSwap.getValue()).booleanValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                        if (((Boolean) this.offhandSwap.getValue()).booleanValue()) {
                            InventoryUtils.moveItemToOffhand(oldItem);
                        } else {
                            InventoryUtils.switchToSlotGhost(oldSlot);
                        }
                    }
                }

                if (((Boolean) this.xp.getValue()).booleanValue() && this.allowExp()) {
                    oldSlot = MiddleClick.mc.player.inventory.currentItem;
                    xpSlot = InventoryUtils.getHotbarItemSlot(Items.EXPERIENCE_BOTTLE);
                    if (xpSlot == -1) {
                        this.hasPressed = true;
                        return;
                    }

                    InventoryUtils.switchToSlotGhost(xpSlot);
                    MiddleClick.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    InventoryUtils.switchToSlotGhost(oldSlot);
                }

                this.hasPressed = true;
            } else {
                this.hasPressed = false;
            }

        }
    }

    boolean allowPearl() {
        RayTraceResult mouseOver = MiddleClick.mc.objectMouseOver;

        return mouseOver == null || mouseOver.typeOfHit == Type.MISS;
    }

    boolean allowExp() {
        RayTraceResult mouseOver = MiddleClick.mc.objectMouseOver;

        return mouseOver != null && mouseOver.typeOfHit == Type.BLOCK;
    }
}
