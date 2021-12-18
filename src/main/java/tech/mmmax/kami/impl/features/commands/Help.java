package tech.mmmax.kami.impl.features.commands;

import java.util.Iterator;
import tech.mmmax.kami.api.command.Command;
import tech.mmmax.kami.api.management.CommandManager;
import tech.mmmax.kami.api.utils.chat.ChatMessage;
import tech.mmmax.kami.api.utils.chat.ChatUtils;

public class Help extends Command {

    public Help() {
        super("Help", "Shows you all the commands", new String[] { "help", "commands"});
    }

    public void run(String[] args) {
        Iterator iterator = CommandManager.INSTANCE.getCommands().iterator();

        while (iterator.hasNext()) {
            Command command = (Command) iterator.next();

            ChatUtils.sendMessage(new ChatMessage(command.getName() + " - " + command.getDesc(), false, 0));
        }

    }
}
