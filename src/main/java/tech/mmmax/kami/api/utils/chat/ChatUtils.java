package tech.mmmax.kami.api.utils.chat;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.text.TextComponentString;
import tech.mmmax.kami.api.wrapper.IMinecraft;
import tech.mmmax.kami.impl.KamiMod;

public class ChatUtils implements IMinecraft {

    public static String CLIENT_NAME = ChatFormatting.GRAY + "[" + ChatFormatting.RESET + KamiMod.NAME_VERSION_COLORED + ChatFormatting.GRAY + "]" + ChatFormatting.RESET;

    public static void sendMessage(ChatMessage message) {
        ChatUtils.CLIENT_NAME = ChatFormatting.GRAY + "[" + ChatFormatting.RESET + KamiMod.NAME_VERSION_COLORED + ChatFormatting.GRAY + "]" + ChatFormatting.RESET;
        if (ChatUtils.mc != null && ChatUtils.mc.player != null && ChatUtils.mc.world != null && ChatUtils.mc.ingameGUI != null) {
            if (message.doesOverride()) {
                ChatUtils.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(ChatUtils.CLIENT_NAME + " " + message.text), message.getMessageID());
            } else {
                ChatUtils.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(ChatUtils.CLIENT_NAME + " " + message.text));
            }

        }
    }

    public static String hephaestus(String string) {
        String str = string.replace("a", "ᴀ");

        str = str.replace("b", "ʙ");
        str = str.replace("c", "ᴄ");
        str = str.replace("d", "ᴅ");
        str = str.replace("e", "ᴇ");
        str = str.replace("f", "ғ");
        str = str.replace("g", "ɢ");
        str = str.replace("h", "ʜ");
        str = str.replace("i", "ɪ");
        str = str.replace("j", "ᴊ");
        str = str.replace("k", "ᴋ");
        str = str.replace("l", "ʟ");
        str = str.replace("m", "�?");
        str = str.replace("n", "ɴ");
        str = str.replace("o", "�?");
        str = str.replace("p", "ᴘ");
        str = str.replace("q", "ǫ");
        str = str.replace("r", "ʀ");
        str = str.replace("s", "ѕ");
        str = str.replace("t", "ᴛ");
        str = str.replace("u", "ᴜ");
        str = str.replace("v", "ᴠ");
        str = str.replace("w", "ᴡ");
        str = str.replace("x", "х");
        str = str.replace("y", "�?");
        str = str.replace("z", "ᴢ");
        str = str.replace("|", "�??");
        return str;
    }
}
