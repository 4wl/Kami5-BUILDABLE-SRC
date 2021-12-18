package tech.mmmax.kami.impl.features.commands;

import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import tech.mmmax.kami.api.command.Command;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.management.FeatureManager;
import tech.mmmax.kami.api.utils.chat.ChatMessage;
import tech.mmmax.kami.api.utils.chat.ChatUtils;

public class Bind extends Command {

    public Bind() {
        super("Bind", "binds a module", new String[] { "bind", "b"});
    }

    public void run(String[] args) {
        if (args.length > 2) {
            Iterator iterator = FeatureManager.INSTANCE.getFeatures().iterator();

            while (iterator.hasNext()) {
                Feature feature = (Feature) iterator.next();

                if (feature instanceof Module) {
                    Module module = (Module) feature;
                    String modName = module.getName().replace(" ", "");

                    if (modName.equalsIgnoreCase(args[1])) {
                        module.getBind().setKey(Keyboard.getKeyIndex(args[2].toUpperCase()));
                        ChatUtils.sendMessage(new ChatMessage("Bound " + module.getName() + " to " + Keyboard.getKeyName(module.getBind().getKey()), false, 0));
                    }
                }
            }
        } else {
            ChatUtils.sendMessage(new ChatMessage("Please input a valid command", false, 0));
        }

    }
}
