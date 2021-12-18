package tech.mmmax.kami.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class PerspectiveEvent extends Event {

    float aspect;

    public PerspectiveEvent(float aspect) {
        this.aspect = aspect;
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
    }

    public float getAspect() {
        return this.aspect;
    }
}
