package tech.mmmax.kami.impl.features.modules.render;

import java.awt.Color;
import java.util.function.Consumer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.event.RenderCrosshairEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.render.IRenderer;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.gui.ClickGui;

public class CrossHair extends Module {

    Value fillRed = (new ValueBuilder()).withDescriptor("Fill Red").withValue(Integer.valueOf(255)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value fillGreen = (new ValueBuilder()).withDescriptor("Fill Green").withValue(Integer.valueOf(255)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value fillBlue = (new ValueBuilder()).withDescriptor("Fill Blue").withValue(Integer.valueOf(255)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value outlineValue = (new ValueBuilder()).withDescriptor("Outline V").withValue(Integer.valueOf(10)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value fillColorMode = (new ValueBuilder()).withDescriptor("Fill Mode").withValue("Normal").withModes(new String[] { "Normal", "Custom"}).withAction((set) -> {
        this.fillRed.setActive(((String) set.getValue()).equals("Custom"));
        this.fillGreen.setActive(((String) set.getValue()).equals("Custom"));
        this.fillBlue.setActive(((String) set.getValue()).equals("Custom"));
    }).register(this);
    Value outlineColorMode = (new ValueBuilder()).withDescriptor("Outline Mode").withValue("Normal").withModes(new String[] { "Normal", "Custom", "None"}).withAction((set) -> {
        this.outlineValue.setActive(((String) set.getValue()).equals("Custom"));
    }).register(this);
    Value width = (new ValueBuilder()).withDescriptor("Width").withValue(Integer.valueOf(3)).withRange(Integer.valueOf(1), Integer.valueOf(8)).register(this);
    Value length = (new ValueBuilder()).withDescriptor("Length").withValue(Integer.valueOf(6)).withRange(Integer.valueOf(4), Integer.valueOf(15)).register(this);
    Value dist = (new ValueBuilder()).withDescriptor("Distance").withValue(Integer.valueOf(4)).withRange(Integer.valueOf(2), Integer.valueOf(8)).register(this);
    Value mode = (new ValueBuilder()).withDescriptor("Mode").withValue("Distance").withModes(new String[] { "Distance", "Close", "Dynamic"}).withAction((set) -> {
        this.dist.setActive(((String) set.getValue()).equals("Distance"));
    }).register(this);
    Value attackIndicator = (new ValueBuilder()).withDescriptor("Attack Indicator").withValue(Boolean.valueOf(false)).register(this);

    public CrossHair() {
        super("Cross Hair", Feature.Category.Render);
    }

    @SubscribeEvent
    public void onRender(RenderCrosshairEvent event) {
        GlStateManager.disableTexture2D();
        if (((String) this.mode.getValue()).equals("Close")) {
            this.renderCloseCrossHair(event.getPartialTicks(), event.getScaledResolution());
        }

        if (((String) this.mode.getValue()).equals("Distance")) {
            this.renderDistanceCrosshair(event.getPartialTicks(), event.getScaledResolution());
        }

        GlStateManager.enableTexture2D();
        event.setCanceled(true);
    }

    void renderCloseCrossHair(float partialTicks, ScaledResolution sr) {
        int centerX = sr.getScaledWidth() / 2;
        int centerY = sr.getScaledHeight() / 2;
        Rect vertical = new Rect(centerX - ((Number) this.width.getValue()).intValue() / 2, centerY - ((Number) this.length.getValue()).intValue(), ((Number) this.width.getValue()).intValue(), ((Number) this.length.getValue()).intValue() * 2);
        Rect horizontal = new Rect(centerX - ((Number) this.length.getValue()).intValue(), centerY - ((Number) this.width.getValue()).intValue() / 2, ((Number) this.length.getValue()).intValue() * 2, ((Number) this.width.getValue()).intValue());

        ClickGui.CONTEXT.getRenderer().renderRect(vertical, this.getOutlineColor(), this.getOutlineColor(), IRenderer.RectMode.Outline, ClickGui.CONTEXT);
        ClickGui.CONTEXT.getRenderer().renderRect(horizontal, this.getOutlineColor(), this.getOutlineColor(), IRenderer.RectMode.Outline, ClickGui.CONTEXT);
        ClickGui.CONTEXT.getRenderer().renderRect(vertical, this.getColor(), this.getColor(), IRenderer.RectMode.Fill, ClickGui.CONTEXT);
        ClickGui.CONTEXT.getRenderer().renderRect(horizontal, this.getColor(), this.getColor(), IRenderer.RectMode.Fill, ClickGui.CONTEXT);
    }

    void renderDistanceCrosshair(float partialTicks, ScaledResolution sr) {
        int centerX = sr.getScaledWidth() / 2;
        int centerY = sr.getScaledHeight() / 2;
        Rect top = new Rect(centerX - ((Number) this.width.getValue()).intValue() / 2, centerY - ((Number) this.length.getValue()).intValue() - ((Number) this.dist.getValue()).intValue() / 2, ((Number) this.width.getValue()).intValue(), ((Number) this.length.getValue()).intValue());
        Rect left = new Rect(centerX - ((Number) this.length.getValue()).intValue() - ((Number) this.dist.getValue()).intValue() / 2, centerY - ((Number) this.width.getValue()).intValue() / 2, ((Number) this.length.getValue()).intValue(), ((Number) this.width.getValue()).intValue());
        Rect right = new Rect(centerX + ((Number) this.dist.getValue()).intValue() / 2, centerY - ((Number) this.width.getValue()).intValue() / 2, ((Number) this.length.getValue()).intValue(), ((Number) this.width.getValue()).intValue());
        Rect bottom = new Rect(centerX - ((Number) this.width.getValue()).intValue() / 2, centerY + ((Number) this.dist.getValue()).intValue() / 2, ((Number) this.width.getValue()).intValue(), ((Number) this.length.getValue()).intValue());

        ClickGui.CONTEXT.getRenderer().renderRect(top, this.getOutlineColor(), this.getOutlineColor(), IRenderer.RectMode.Outline, ClickGui.CONTEXT);
        ClickGui.CONTEXT.getRenderer().renderRect(left, this.getOutlineColor(), this.getOutlineColor(), IRenderer.RectMode.Outline, ClickGui.CONTEXT);
        ClickGui.CONTEXT.getRenderer().renderRect(bottom, this.getOutlineColor(), this.getOutlineColor(), IRenderer.RectMode.Outline, ClickGui.CONTEXT);
        ClickGui.CONTEXT.getRenderer().renderRect(right, this.getOutlineColor(), this.getOutlineColor(), IRenderer.RectMode.Outline, ClickGui.CONTEXT);
        ClickGui.CONTEXT.getRenderer().renderRect(top, this.getColor(), this.getColor(), IRenderer.RectMode.Fill, ClickGui.CONTEXT);
        ClickGui.CONTEXT.getRenderer().renderRect(left, this.getColor(), this.getColor(), IRenderer.RectMode.Fill, ClickGui.CONTEXT);
        ClickGui.CONTEXT.getRenderer().renderRect(bottom, this.getColor(), this.getColor(), IRenderer.RectMode.Fill, ClickGui.CONTEXT);
        ClickGui.CONTEXT.getRenderer().renderRect(right, this.getColor(), this.getColor(), IRenderer.RectMode.Fill, ClickGui.CONTEXT);
    }

    Color getColor() {
        String s = (String) this.fillColorMode.getValue();
        byte b0 = -1;

        switch (s.hashCode()) {
        case -1955878649:
            if (s.equals("Normal")) {
                b0 = 0;
            }
            break;

        case 2029746065:
            if (s.equals("Custom")) {
                b0 = 1;
            }
        }

        switch (b0) {
        case 0:
            return ClickGui.CONTEXT.getColorScheme().getMainColor(0);

        case 1:
            return new Color(((Number) this.fillRed.getValue()).intValue(), ((Number) this.fillGreen.getValue()).intValue(), ((Number) this.fillBlue.getValue()).intValue());

        default:
            return new Color(255, 255, 255, 255);
        }
    }

    Color getOutlineColor() {
        String s = (String) this.outlineColorMode.getValue();
        byte b0 = -1;

        switch (s.hashCode()) {
        case -1955878649:
            if (s.equals("Normal")) {
                b0 = 0;
            }
            break;

        case 2433880:
            if (s.equals("None")) {
                b0 = 2;
            }
            break;

        case 2029746065:
            if (s.equals("Custom")) {
                b0 = 1;
            }
        }

        switch (b0) {
        case 0:
            return new Color(0, 0, 0, 255);

        case 1:
            return new Color(((Number) this.outlineValue.getValue()).intValue(), ((Number) this.outlineValue.getValue()).intValue(), ((Number) this.outlineValue.getValue()).intValue(), 255);

        case 2:
            return new Color(0, 0, 0, 0);

        default:
            return new Color(0, 0, 0, 100);
        }
    }
}
