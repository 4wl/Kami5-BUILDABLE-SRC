package tech.mmmax.kami.api.utils;

import com.google.gson.JsonParser;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.commons.io.IOUtils;

public class AltUtils {

    public static void login(String username, String token) {
        try {
            String e = IOUtils.toString(new URL("https://api.mojang.com/users/profiles/minecraft/" + username), StandardCharsets.UTF_8);
            String uuid = (new JsonParser()).parse(e).getAsJsonObject().get("id").getAsString();
            Session session = new Session(username, UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")).toString(), token, "mojang");
            Field field = Minecraft.class.getDeclaredField("session");

            field.setAccessible(true);
            field.set(Minecraft.getMinecraft(), session);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }
}
