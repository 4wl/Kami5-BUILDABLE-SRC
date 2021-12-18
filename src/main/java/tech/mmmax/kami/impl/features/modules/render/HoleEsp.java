package tech.mmmax.kami.impl.features.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.utils.world.HoleUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.gui.ClickGui;

public class HoleEsp extends Module {

    Value glowMode = (new ValueBuilder()).withDescriptor("Glow Mode").withValue("Fade").withModes(new String[] { "Fade", "Fog"}).register(this);
    Value lineWidth = (new ValueBuilder()).withDescriptor("Line Width").withValue(Float.valueOf(2.0F)).withRange(Double.valueOf(0.1D), Integer.valueOf(5)).register(this);
    Value height = (new ValueBuilder()).withDescriptor("Height").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(0), Integer.valueOf(2)).register(this);
    Value range = (new ValueBuilder()).withDescriptor("Range").withValue(Double.valueOf(5.0D)).withRange(Double.valueOf(1.0D), Double.valueOf(30.0D)).register(this);
    Value doubles = (new ValueBuilder()).withDescriptor("Doubles").withValue(Boolean.valueOf(true)).register(this);
    Value bedrockFill = (new ValueBuilder()).withDescriptor("Bedrock Fill").withValue(new Color(0, 255, 0, 100)).register(this);
    Value bedrockLine = (new ValueBuilder()).withDescriptor("Bedrock Line").withValue(new Color(255, 255, 255, 255)).register(this);
    Value obbyFill = (new ValueBuilder()).withDescriptor("Obby Fill").withValue(new Color(0, 255, 218, 100)).register(this);
    Value obbyLine = (new ValueBuilder()).withDescriptor("Obby Line").withValue(new Color(255, 255, 255, 255)).register(this);
    Value doubleFill = (new ValueBuilder()).withDescriptor("Double Fill").withValue(new Color(255, 0, 11, 100)).register(this);
    Value doubleLine = (new ValueBuilder()).withDescriptor("Double Line").withValue(new Color(255, 255, 255, 255)).register(this);
    Value bedrockFill2 = (new ValueBuilder()).withDescriptor("Bedrock Fill2").withValue(new Color(0, 255, 0, 0)).register(this);
    Value bedrockLine2 = (new ValueBuilder()).withDescriptor("Bedrock Line2").withValue(new Color(255, 255, 255, 0)).register(this);
    Value obbyFill2 = (new ValueBuilder()).withDescriptor("Obby Fill2").withValue(new Color(0, 255, 218, 0)).register(this);
    Value obbyLine2 = (new ValueBuilder()).withDescriptor("Obby Line2").withValue(new Color(255, 255, 255, 0)).register(this);
    Value doubleFill2 = (new ValueBuilder()).withDescriptor("Double Fill2").withValue(new Color(255, 0, 11, 0)).register(this);
    Value doubleLine2 = (new ValueBuilder()).withDescriptor("Double Line2").withValue(new Color(255, 255, 255, 0)).register(this);
    ExecutorService service = Executors.newCachedThreadPool();
    volatile List holes = new ArrayList();

    public HoleEsp() {
        super("Hole ESP", Feature.Category.Render);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent e) {
        this.service.submit(() -> {
            // $FF: Couldn't be decompiled
        });
    }

    @SubscribeEvent
    public void onRender3d(RenderWorldLastEvent event) {
        AxisAlignedBB holeBB;
        Color outlineColor;
        Color outlineColor2;

        for (Iterator iterator = this.holes.iterator(); iterator.hasNext(); RenderUtil.renderBB(3, holeBB, outlineColor, outlineColor2)) {
            HoleUtils.Hole hole = (HoleUtils.Hole) iterator.next();
            Color mainOutline = ClickGui.CONTEXT.getColorScheme().getOutlineColor();

            GL11.glLineWidth(RenderUtil.getInterpolatedLinWid((float) HoleEsp.mc.player.getDistance((double) hole.pos1.getX(), (double) hole.pos1.getY(), (double) hole.pos1.getZ()), ((Number) this.lineWidth.getValue()).floatValue(), ((Number) this.lineWidth.getValue()).floatValue()));
            holeBB = hole.doubleHole ? new AxisAlignedBB((double) hole.pos1.getX(), (double) hole.pos1.getY(), (double) hole.pos1.getZ(), (double) (hole.pos2.getX() + 1), (double) (hole.pos2.getY() + 1), (double) (hole.pos2.getZ() + 1)) : new AxisAlignedBB(hole.pos1);
            holeBB = new AxisAlignedBB(holeBB.minX, holeBB.minY, holeBB.minZ, holeBB.maxX, holeBB.minY + ((Number) this.height.getValue()).doubleValue(), holeBB.maxZ);
            Color fillColor = hole.bedrock ? (Color) this.bedrockFill.getValue() : (hole.doubleHole ? (Color) this.doubleFill.getValue() : (Color) this.obbyFill.getValue());
            Color fillColor2 = hole.bedrock ? (Color) this.bedrockFill2.getValue() : (hole.doubleHole ? (Color) this.doubleFill2.getValue() : (Color) this.obbyFill2.getValue());

            outlineColor = hole.bedrock ? (Color) this.bedrockLine.getValue() : (hole.doubleHole ? (Color) this.doubleLine.getValue() : (Color) this.obbyLine.getValue());
            outlineColor2 = hole.bedrock ? (Color) this.bedrockLine2.getValue() : (hole.doubleHole ? (Color) this.doubleLine2.getValue() : (Color) this.obbyLine2.getValue());
            if (((String) this.glowMode.getValue()).equals("Fade")) {
                RenderUtil.renderBB(7, holeBB, fillColor, fillColor2);
            } else if (((String) this.glowMode.getValue()).equals("Fog")) {
                RenderUtil.renderBBFog(holeBB, fillColor, fillColor2);
            }
        }

    }
}
