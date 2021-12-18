package tech.mmmax.kami.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import tech.mmmax.kami.api.management.SavableManager;

@Mod(
    name = "Kami5L",
    modid = "kami5l",
    version = "1.0"
)
public class KamiMod {

    public static String NAME = "Kami5";
    public static final String MOD_ID = "kami5";
    public static final String VERSION = "1.7";
    public static final String NAME_VERSION = KamiMod.NAME + " 1.7";
    public static String NAME_VERSION_COLORED = KamiMod.NAME + ChatFormatting.GRAY + " 1.7";
    @Instance
    public KamiMod INSTANCE;

    public static void updateName() {
        KamiMod.NAME_VERSION_COLORED = KamiMod.NAME + ChatFormatting.GRAY + " " + "1.7";
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("Initialzied");
        System.out.println("penis - hausemasterissue");
        Register.INSTANCE = new Register();
        Register.INSTANCE.registerAll();
        KamiMod.ShutdownHook.setup();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        SavableManager.INSTANCE.load();
    }

    static class ShutdownHook extends Thread {

        public static void setup() {
            Runtime.getRuntime().addShutdownHook(new KamiMod.ShutdownHook());
        }

        public void run() {
            super.run();

            try {
                SavableManager.INSTANCE.save();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }
    }
}
