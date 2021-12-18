package tech.mmmax.kami.impl.gui;

import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.gui.GUI;
import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.impl.features.modules.client.ClickGuiModule;
import tech.mmmax.kami.impl.gui.components.CategoryFrame;
import tech.mmmax.kami.impl.gui.renderer.Renderer;

public class ClickGui extends GUI {

    public static Context CONTEXT = new Context(ClickGuiModule.INSTANCE, ClickGuiModule.INSTANCE, new Renderer());
    public static ClickGui INSTANCE;

    public ClickGui() {
        super(ClickGui.CONTEXT);
    }

    public void addComponents() {
        super.addComponents();
        int offset = 100;
        Feature.Category[] afeature_category = Feature.Category.values();
        int i = afeature_category.length;

        for (int j = 0; j < i; ++j) {
            Feature.Category category = afeature_category[j];

            this.getContext().getComponents().add(new CategoryFrame(category, new Rect(offset, 40, 100, 200)));
            offset += this.getContext().getMetrics().getFrameWidth() + 10;
        }

    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
