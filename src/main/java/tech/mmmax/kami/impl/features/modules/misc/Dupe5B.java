package tech.mmmax.kami.impl.features.modules.misc;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class Dupe5B extends Module {

    Value dropCount = (new ValueBuilder()).withDescriptor("Drop Count").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(1), Integer.valueOf(60)).register(this);
    Value dropAll = (new ValueBuilder()).withDescriptor("Drop All").withValue(Boolean.valueOf(false)).register(this);
    Value shulkerCheck = (new ValueBuilder()).withDescriptor("Shulker Check").withValue(Boolean.valueOf(false)).register(this);

    public Dupe5B() {
        super("5BDupe", Feature.Category.Misc);
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (Dupe5B.mc.player.getHeldItem(EnumHand.MAIN_HAND).getCount() > 1 && (!((Boolean) this.shulkerCheck.getValue()).booleanValue() || Dupe5B.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemShulkerBox)) {
                Dupe5B.mc.displayGuiScreen((GuiScreen) null);
                Dupe5B.mc.getConnection().sendPacket(new CPacketCloseWindow());

                for (int i = 0; i < ((Number) this.dropCount.getValue()).intValue(); ++i) {
                    Dupe5B.mc.player.dropItem(false);
                }

                if (((Boolean) this.dropAll.getValue()).booleanValue()) {
                    Dupe5B.mc.player.dropItem(true);
                }
            }

        }
    }
}
