package tech.mmmax.kami.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class PickBlockEvent extends Event {

    public boolean isCancelable() {
        return true;
    }
}
