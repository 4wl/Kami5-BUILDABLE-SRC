package tech.mmmax.kami.impl.features.modules.player;

import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.event.PickBlockEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;

public class NoInteract extends Module {

    public NoInteract() {
        super("No Interact", Feature.Category.Player);
    }

    @SubscribeEvent
    public void onPickBlock(PickBlockEvent event) {
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onBlockInteract(RightClickBlock event) {
        if (!NullUtils.nullCheck()) {
            if (NoInteract.mc.world.getBlockState(event.getPos()).getBlock() == Blocks.ANVIL || NoInteract.mc.world.getBlockState(event.getPos()).getBlock() == Blocks.ENDER_CHEST) {
                event.setCanceled(true);
            }

        }
    }
}
