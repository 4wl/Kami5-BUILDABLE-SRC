package tech.mmmax.kami.api.gui.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tech.mmmax.kami.api.gui.component.IComponent;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.render.IRenderer;
import tech.mmmax.kami.api.gui.theme.IColorScheme;
import tech.mmmax.kami.api.gui.theme.IMetrics;

public class Context {

    List components = new ArrayList();
    IColorScheme colorScheme;
    IMetrics metrics;
    IRenderer renderer;
    float partialTicks = 0.0F;

    public Context(IColorScheme colorScheme, IMetrics metrics, IRenderer renderer) {
        this.colorScheme = colorScheme;
        this.metrics = metrics;
        this.renderer = renderer;
    }

    public List getComponents() {
        return this.components;
    }

    public IColorScheme getColorScheme() {
        return this.colorScheme;
    }

    public IMetrics getMetrics() {
        return this.metrics;
    }

    public void setComponents(List components) {
        this.components = components;
    }

    public void setColorScheme(IColorScheme colorScheme) {
        this.colorScheme = colorScheme;
    }

    public void setMetrics(IMetrics metrics) {
        this.metrics = metrics;
    }

    public IRenderer getRenderer() {
        return this.renderer;
    }

    public void setRenderer(IRenderer renderer) {
        this.renderer = renderer;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public IComponent getHovering(MouseHelper mouseHelper) {
        IComponent hovering = null;
        Iterator iterator = this.getComponents().iterator();

        while (iterator.hasNext()) {
            IComponent component = (IComponent) iterator.next();

            if (component.getDims().collideWithMouse(mouseHelper)) {
                hovering = component;
            }
        }

        return hovering;
    }

    public ScaledResolution getScaledResolution() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }
}
