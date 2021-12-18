package tech.mmmax.kami.api.management;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.command.Command;
import tech.mmmax.kami.api.utils.chat.ChatMessage;
import tech.mmmax.kami.api.utils.chat.ChatUtils;

public class CommandManager {

    public static CommandManager INSTANCE;
    public String PREFIX = "-";
    List commands = new ArrayList();

    public CommandManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        if (event.getMessage().startsWith(this.PREFIX)) {
            String sub = event.getMessage().substring(1);
            String[] args = sub.split(" ");

            if (args.length > 0) {
                Iterator iterator = this.commands.iterator();

                while (iterator.hasNext()) {
                    Command command = (Command) iterator.next();
                    String[] astring = command.getAlias();
                    int i = astring.length;

                    for (int j = 0; j < i; ++j) {
                        String s = astring[j];

                        if (s.equalsIgnoreCase(args[0])) {
                            command.run(args);
                            break;
                        }
                    }
                }
            } else {
                ChatUtils.sendMessage(new ChatMessage("Invalid command", false, 0));
            }

            event.setCanceled(true);
        }

    }

    public List getCommands() {
        return this.commands;
    }
}
