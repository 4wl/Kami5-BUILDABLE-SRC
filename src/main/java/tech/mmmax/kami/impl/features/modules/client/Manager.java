package tech.mmmax.kami.impl.features.modules.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.KamiMod;

public class Manager extends Module {

    Value clientName;

    public Manager() {
        super("Manager", Feature.Category.Client);
        this.clientName = (new ValueBuilder()).withDescriptor("Client Name").withValue(KamiMod.NAME).register(this);
        this.setEnabled(true);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable() {}

    public void onEnable() {}

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        KamiMod.NAME = (String) this.clientName.getValue();
        KamiMod.updateName();
    }
}
