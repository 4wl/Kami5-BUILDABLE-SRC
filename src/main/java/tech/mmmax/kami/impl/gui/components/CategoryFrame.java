package tech.mmmax.kami.impl.gui.components;

import java.util.Iterator;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.gui.component.impl.FrameComponent;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.management.FeatureManager;
import tech.mmmax.kami.impl.gui.components.module.FeatureButton;

public class CategoryFrame extends FrameComponent {

    Feature.Category category;

    public CategoryFrame(Feature.Category category, Rect dims) {
        super(category.toString(), dims);
        this.category = category;
        Iterator iterator = FeatureManager.INSTANCE.getFeatures().iterator();

        while (iterator.hasNext()) {
            Feature feature = (Feature) iterator.next();

            if (feature.getCategory() == this.category) {
                this.getFlow().getComponents().add(new FeatureButton(feature, new Rect(0, 0, 0, 0)));
            }
        }

    }

    public Feature.Category getCategory() {
        return this.category;
    }

    public void setCategory(Feature.Category category) {
        this.category = category;
    }
}
