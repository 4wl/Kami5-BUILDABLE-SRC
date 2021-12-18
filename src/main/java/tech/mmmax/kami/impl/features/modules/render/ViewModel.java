package tech.mmmax.kami.impl.features.modules.render;

import java.util.function.Consumer;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.PerspectiveEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class ViewModel extends Module {

    Value aspectAmount = (new ValueBuilder()).withDescriptor("Aspect Amount").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(0), Integer.valueOf(3)).register(this);
    Value aspect = (new ValueBuilder()).withDescriptor("Aspect").withValue(Boolean.valueOf(false)).withAction((set) -> {
        this.aspectAmount.setActive(((Boolean) set.getValue()).booleanValue());
    }).register(this);
    Value fov = (new ValueBuilder()).withDescriptor("FOV").withValue(Integer.valueOf(130)).withRange(Integer.valueOf(60), Integer.valueOf(170)).register(this);
    Value fovMode = (new ValueBuilder()).withDescriptor("FOV Mode").withValue("Normal").withModes(new String[] { "Normal", "Item"}).register(this);
    Value leftX = (new ValueBuilder()).withDescriptor("Left X").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-2), Integer.valueOf(2)).register(this);
    Value leftY = (new ValueBuilder()).withDescriptor("Left Y").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-2), Integer.valueOf(2)).register(this);
    Value leftZ = (new ValueBuilder()).withDescriptor("Left Z").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-2), Integer.valueOf(2)).register(this);
    Value rightX = (new ValueBuilder()).withDescriptor("Right X").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-2), Integer.valueOf(2)).register(this);
    Value rightY = (new ValueBuilder()).withDescriptor("Right Y").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-2), Integer.valueOf(2)).register(this);
    Value rightZ = (new ValueBuilder()).withDescriptor("Right Z").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-2), Integer.valueOf(2)).register(this);
    Value leftAngle = (new ValueBuilder()).withDescriptor("Left Angle").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-180), Integer.valueOf(180)).register(this);
    Value leftRotateX = (new ValueBuilder()).withDescriptor("Left Rotate X").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-1), Integer.valueOf(1)).register(this);
    Value leftRotateY = (new ValueBuilder()).withDescriptor("Left Rotate Y").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-1), Integer.valueOf(1)).register(this);
    Value leftRotateZ = (new ValueBuilder()).withDescriptor("Left Rotate Z").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-1), Integer.valueOf(1)).register(this);
    Value rightAngle = (new ValueBuilder()).withDescriptor("Right Angle").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-180), Integer.valueOf(180)).register(this);
    Value rightRotateX = (new ValueBuilder()).withDescriptor("Right Rotate X").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-1), Integer.valueOf(1)).register(this);
    Value rightRotateY = (new ValueBuilder()).withDescriptor("Right Rotate Y").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-1), Integer.valueOf(1)).register(this);
    Value rightRotateZ = (new ValueBuilder()).withDescriptor("Right Rotate Z").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-1), Integer.valueOf(1)).register(this);
    Value leftScaleX = (new ValueBuilder()).withDescriptor("Left Scale X").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-3), Integer.valueOf(3)).register(this);
    Value leftScaleY = (new ValueBuilder()).withDescriptor("Left Scale Y").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-3), Integer.valueOf(3)).register(this);
    Value leftScaleZ = (new ValueBuilder()).withDescriptor("Left Scale Z").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-3), Integer.valueOf(3)).register(this);
    Value rightScaleX = (new ValueBuilder()).withDescriptor("Right Scale X").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-3), Integer.valueOf(3)).register(this);
    Value rightScaleY = (new ValueBuilder()).withDescriptor("Right Scale Y").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-3), Integer.valueOf(3)).register(this);
    Value rightScaleZ = (new ValueBuilder()).withDescriptor("Right Scale Z").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(-3), Integer.valueOf(3)).register(this);

    public ViewModel() {
        super("ViewModel", Feature.Category.Render);
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (((String) this.fovMode.getValue()).equals("Normal")) {
            ViewModel.mc.gameSettings.fovSetting = (float) ((Number) this.fov.getValue()).intValue();
        }

    }

    @SubscribeEvent
    public void onItemFOV(FOVModifier event) {
        if (((String) this.fovMode.getValue()).equals("Item")) {
            event.setFOV((float) ((Number) this.fov.getValue()).intValue());
        }

    }

    @SubscribeEvent
    public void setPerspective(PerspectiveEvent event) {
        if (((Boolean) this.aspect.getValue()).booleanValue()) {
            event.setAspect(((Number) this.aspectAmount.getValue()).floatValue());
        }

    }
}
