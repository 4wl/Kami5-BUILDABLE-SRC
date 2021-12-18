package tech.mmmax.kami.mixin.mixins;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ GuiMainMenu.class})
public class MixinGuiMainMenu extends GuiScreen {

}
