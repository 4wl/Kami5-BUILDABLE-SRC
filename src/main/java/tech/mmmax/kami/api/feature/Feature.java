package tech.mmmax.kami.api.feature;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraftforge.common.MinecraftForge;
import tech.mmmax.kami.api.config.ISavable;
import tech.mmmax.kami.api.management.SavableManager;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class Feature implements ISavable {

    String name;
    boolean enabled;
    Feature.FeatureType type;
    List values;
    Feature.Category category;
    public Value visible;
    public Value displayName;

    public Feature(String name, Feature.Category category, Feature.FeatureType type) {
        this.name = name;
        this.enabled = false;
        this.type = type;
        this.values = new ArrayList();
        this.category = category;
        SavableManager.INSTANCE.getSavables().add(this);
        this.visible = (new ValueBuilder()).withDescriptor("Visible").withValue(Boolean.valueOf(true)).register(this);
        this.displayName = (new ValueBuilder()).withDescriptor("Name").withValue(this.getName()).register(this);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return (String) this.displayName.getValue();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (this.enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }

    }

    public Feature.Category getCategory() {
        return this.category;
    }

    public void setCategory(Feature.Category category) {
        this.category = category;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public void toggle() {
        this.setEnabled(!this.isEnabled());
    }

    public Feature.FeatureType getType() {
        return this.type;
    }

    public void setType(Feature.FeatureType type) {
        this.type = type;
    }

    public List getValues() {
        return this.values;
    }

    public void setValues(List values) {
        this.values = values;
    }

    public String getHudInfo() {
        return "";
    }

    public void load(Map objects) {
        Object e = objects.get("enabled");

        if (e != null) {
            this.setEnabled(((Boolean) e).booleanValue());
        }

        this.setEnabled(objects.get("enabled") != null ? ((Boolean) objects.get("enabled")).booleanValue() : this.isEnabled());
        Iterator iterator = this.getValues().iterator();

        while (iterator.hasNext()) {
            Value value = (Value) iterator.next();
            Object o = objects.get(value.getTag());

            if (o != null) {
                try {
                    if (value.getValue() instanceof Color) {
                        Map map = (Map) o;
                        Color colorVal = new Color(((Integer) map.get("red")).intValue(), ((Integer) map.get("green")).intValue(), ((Integer) map.get("blue")).intValue(), ((Integer) map.get("alpha")).intValue());

                        value.setValue(colorVal);
                    } else {
                        value.setValue(o);
                    }
                } catch (Exception exception) {
                    ;
                }
            }
        }

    }

    public Map save() {
        HashMap toSave = new HashMap();

        toSave.put("enabled", Boolean.valueOf(this.enabled));
        Iterator iterator = this.getValues().iterator();

        while (iterator.hasNext()) {
            Value value = (Value) iterator.next();

            if (value.getValue() instanceof Color) {
                HashMap color = new HashMap();

                color.put("red", Integer.valueOf(((Color) value.getValue()).getRed()));
                color.put("green", Integer.valueOf(((Color) value.getValue()).getGreen()));
                color.put("blue", Integer.valueOf(((Color) value.getValue()).getBlue()));
                color.put("alpha", Integer.valueOf(((Color) value.getValue()).getAlpha()));
                toSave.put(value.getTag(), color);
            } else {
                toSave.put(value.getTag(), value.getValue());
            }
        }

        return toSave;
    }

    public String getFileName() {
        return this.getName() + ".yml";
    }

    public String getDirName() {
        return "features";
    }

    public static enum Category {

        Client, Player, Combat, Misc, Render, Hud, Scripts;
    }

    public static enum FeatureType {

        Module, Hud, Script;
    }
}
