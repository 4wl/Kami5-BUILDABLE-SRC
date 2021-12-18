package tech.mmmax.kami.impl.features.modules.client;

import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class Optimizer extends Module {

    public Value entityFrustum = (new ValueBuilder()).withDescriptor("Entity Frustum").withValue(Boolean.valueOf(false)).register(this);
    public static Optimizer INSTANCE;

    public Optimizer() {
        super("Optimizer", Feature.Category.Client);
        Optimizer.INSTANCE = this;
    }
}
