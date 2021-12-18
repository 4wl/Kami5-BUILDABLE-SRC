package tech.mmmax.kami.api.feature.script;

import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.MoveEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.feature.script.node.nodes.EventNode;

public class Script extends Module {

    Map nodes = new HashMap();

    public Script(String name) {
        super(name, Feature.Category.Scripts);
        this.setType(Feature.FeatureType.Script);
        this.nodes.put(Integer.valueOf(0), new EventNode("Tick Event"));
        this.nodes.put(Integer.valueOf(1), new EventNode("Render2D Event"));
        this.nodes.put(Integer.valueOf(2), new EventNode("Render3D Event"));
        this.nodes.put(Integer.valueOf(3), new EventNode("Packet Event"));
        this.nodes.put(Integer.valueOf(4), new EventNode("Move Event"));
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        HashMap args = new HashMap();

        args.put(Integer.valueOf(0), event);
        ((EventNode) this.nodes.get(Integer.valueOf(0))).runNode(args);
    }

    @SubscribeEvent
    public void onRender2D(Text event) {
        HashMap args = new HashMap();

        args.put(Integer.valueOf(0), event);
        ((EventNode) this.nodes.get(Integer.valueOf(1))).runNode(args);
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        HashMap args = new HashMap();

        args.put(Integer.valueOf(0), event);
        ((EventNode) this.nodes.get(Integer.valueOf(2))).runNode(args);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        HashMap args = new HashMap();

        args.put(Integer.valueOf(0), event);
        ((EventNode) this.nodes.get(Integer.valueOf(3))).runNode(args);
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        HashMap args = new HashMap();

        args.put(Integer.valueOf(0), event);
        ((EventNode) this.nodes.get(Integer.valueOf(4))).runNode(args);
    }
}
