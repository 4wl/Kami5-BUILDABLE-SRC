package tech.mmmax.kami.impl.features.modules.misc;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.player.InventoryUtils;

public class HotbarRefill extends Module {

    Item[] cache = new Item[9];

    public HotbarRefill() {
        super("HotbarRefill", Feature.Category.Misc);
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (HotbarRefill.mc.currentScreen == null) {
                if (HotbarRefill.mc.player.isDead) {
                    this.cache = new Item[9];
                } else {
                    int index = 0;
                    Item[] i = this.cache;
                    int i = i.length;

                    for (int j = 0; j < i; ++j) {
                        Item item = i[j];

                        if (item != null && !item.equals(Item.getItemFromBlock(Blocks.AIR)) && HotbarRefill.mc.player.inventory.getStackInSlot(index).isEmpty()) {
                            try {
                                int slot = this.getSlot(item);

                                if (slot != -1) {
                                    InventoryUtils.moveItem(slot, index);
                                }
                            } catch (Exception exception) {
                                ;
                            }
                        }

                        ++index;
                    }

                    for (int k = 0; k < 9; ++k) {
                        this.cache[k] = HotbarRefill.mc.player.inventory.getStackInSlot(k).getItem();
                    }

                }
            }
        }
    }

    int getSlot(Item item) {
        int slot = -1;

        for (int i = 45; i > 9; --i) {
            if (HotbarRefill.mc.player.inventory.getStackInSlot(i).getItem().equals(item)) {
                slot = i;
                break;
            }
        }

        return slot;
    }
}
