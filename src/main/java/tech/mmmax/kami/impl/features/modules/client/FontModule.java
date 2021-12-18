package tech.mmmax.kami.impl.features.modules.client;

import java.awt.Font;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class FontModule extends Module {

    public static FontModule INSTANCE;
    public Font font = new Font("Arial", 0, 18);
    public Value fontSize = (new ValueBuilder()).withDescriptor("Font Size").withValue(Integer.valueOf(18)).withRange(Integer.valueOf(10), Integer.valueOf(25)).register(this);

    public FontModule() {
        super("Font", Feature.Category.Client);
        FontModule.INSTANCE = this;
    }
}
