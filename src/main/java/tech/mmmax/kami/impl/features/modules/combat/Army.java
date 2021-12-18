package tech.mmmax.kami.impl.features.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;

public class Army extends Module {

    List handlers = new ArrayList();
    boolean ignore = false;

    public Army() {
        super("Army", Feature.Category.Combat);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!NullUtils.nullCheck()) {
            if (event.getTime() == PacketEvent.Time.Send && !this.ignore) {
                this.ignore = true;
                Iterator iterator = this.handlers.iterator();

                while (iterator.hasNext()) {
                    NetHandlerPlayClient handler = (NetHandlerPlayClient) iterator.next();

                    handler.sendPacket(event.getPacket());
                }

                this.ignore = false;
            }

        }
    }

    public void addAccount(String token) {}
}
