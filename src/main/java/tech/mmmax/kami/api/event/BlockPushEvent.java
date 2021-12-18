package tech.mmmax.kami.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class BlockPushEvent extends Event {

    public boolean isCancelable() {
        return true;
    }
}
