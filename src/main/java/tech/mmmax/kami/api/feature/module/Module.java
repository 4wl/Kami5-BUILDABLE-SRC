package tech.mmmax.kami.api.feature.module;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Map;
import tech.mmmax.kami.api.binds.IBindable;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.management.BindManager;
import tech.mmmax.kami.api.utils.chat.ChatMessage;
import tech.mmmax.kami.api.utils.chat.ChatUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.api.value.custom.Bind;
import tech.mmmax.kami.api.wrapper.IMinecraft;

public class Module extends Feature implements IBindable, IMinecraft {

    Bind bind = new Bind();
    Value chatNotify = (new ValueBuilder()).withDescriptor("Chat Notify").withValue(Boolean.valueOf(true)).register(this);

    public Module(String name, Feature.Category category) {
        super(name, category, Feature.FeatureType.Module);
        BindManager.INSTANCE.getBindables().add(this);
    }

    public Value register(Value value) {
        this.getValues().add(value);
        return value;
    }

    public Bind getBind() {
        return this.bind;
    }

    public void setBind(Bind bind) {
        this.bind = bind;
    }

    public void onEnable() {
        super.onEnable();
        if (((Boolean) this.chatNotify.getValue()).booleanValue()) {
            ChatUtils.sendMessage(new ChatMessage("Enabled: " + ChatFormatting.GREEN + ChatFormatting.BOLD + this.getDisplayName(), true, 69420));
        }

    }

    public void onDisable() {
        super.onDisable();
        if (((Boolean) this.chatNotify.getValue()).booleanValue()) {
            ChatUtils.sendMessage(new ChatMessage("Disabled: " + ChatFormatting.RED + ChatFormatting.BOLD + this.getDisplayName(), true, 69420));
        }

    }

    public void load(Map objects) {
        super.load(objects);
        this.bind.setKey(((Integer) objects.get("bind")).intValue());
    }

    public Map save() {
        Map toSave = super.save();

        toSave.put("bind", Integer.valueOf(this.bind.getKey()));
        return toSave;
    }

    public int getKey() {
        return this.bind.getKey();
    }

    public void onKey() {
        this.toggle();
    }
}
