package tech.mmmax.kami.api.event;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PacketEvent extends Event {

    public Packet packet;
    public PacketEvent.Time time;

    public PacketEvent(Packet packet, PacketEvent.Time time) {
        this.packet = packet;
        this.time = time;
    }

    public PacketEvent.Time getTime() {
        return this.time;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public boolean isCancelable() {
        return true;
    }

    public static enum Time {

        Send, Receive;
    }
}
