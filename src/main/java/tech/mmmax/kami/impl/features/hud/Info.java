package tech.mmmax.kami.impl.features.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import tech.mmmax.kami.api.feature.hud.HudComponent;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.player.InventoryUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.features.modules.client.HudColors;
import tech.mmmax.kami.impl.gui.ClickGui;

public class Info extends HudComponent {

    Value order = (new ValueBuilder()).withDescriptor("Order").withValue("BottomLeft").withModes(new String[] { "BottomLeft", "BottomRight", "TopRight", "TopLeft"}).register(this);
    Value infoColor = (new ValueBuilder()).withDescriptor("Info Color").withValue(new Color(87, 87, 87)).register(this);
    Value potion = (new ValueBuilder()).withDescriptor("Potions").withValue(Boolean.valueOf(true)).register(this);
    int off = 0;

    public Info() {
        super("Info");
    }

    public void draw(Text event) {
        super.draw(event);
        if (!NullUtils.nullCheck()) {
            DecimalFormat minuteFormatter = new DecimalFormat("0");
            DecimalFormat secondsFormatter = new DecimalFormat("00");
            ArrayList potions = new ArrayList();
            ArrayList info = new ArrayList();
            Iterator crystals = Info.mc.player.getActivePotionEffects().iterator();

            while (crystals.hasNext()) {
                PotionEffect totems = (PotionEffect) crystals.next();
                double exp = (double) (totems.getDuration() / 20 % 60);
                double timeM = (double) (totems.getDuration() / 20 / 60);
                String time = minuteFormatter.format(timeM) + ":" + secondsFormatter.format(exp);
                String name = I18n.format(totems.getEffectName(), new Object[0]) + " " + (totems.getAmplifier() + 1) + " " + ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + time + ChatFormatting.GRAY + "]";

                potions.add(new Info.InfoComponent(new Color(totems.getPotion().getLiquidColor()), name));
            }

            info.add(new Info.InfoComponent((Color) null, "FPS " + ChatFormatting.WHITE + Minecraft.getDebugFPS()));
            if (Info.mc.getConnection() != null && Info.mc.getConnection().getPlayerInfo(Info.mc.player.getUniqueID()) != null) {
                info.add(new Info.InfoComponent((Color) null, "PING " + ChatFormatting.WHITE + Info.mc.getConnection().getPlayerInfo(Info.mc.player.getUniqueID()).getResponseTime()));
            }

            int crystals1 = InventoryUtils.getItemCount(Items.END_CRYSTAL);
            int totems1 = InventoryUtils.getItemCount(Items.TOTEM_OF_UNDYING);
            int exp1 = InventoryUtils.getItemCount(Items.EXPERIENCE_BOTTLE);

            info.add(new Info.InfoComponent((Color) null, "CRYSTALS " + ChatFormatting.WHITE + crystals1));
            info.add(new Info.InfoComponent((Color) null, "TOTEMS " + ChatFormatting.WHITE + totems1));
            info.add(new Info.InfoComponent((Color) null, "XP " + ChatFormatting.WHITE + exp1));
            info.sort(Comparator.comparingInt(applyAsInt<invokedynamic>()));
            this.off = 0;
            if (((String) this.order.getValue()).contains("Top")) {
                this.renderInfo(info);
                if (((Boolean) this.potion.getValue()).booleanValue()) {
                    this.renderPotions(potions);
                }
            } else {
                if (((Boolean) this.potion.getValue()).booleanValue()) {
                    this.renderPotions(potions);
                }

                this.renderInfo(info);
            }

        }
    }

    public void renderPotions(List potions) {
        Info.InfoComponent comp;

        for (Iterator iterator = potions.iterator(); iterator.hasNext(); this.off += ((String) this.order.getValue()).contains("Top") ? ClickGui.CONTEXT.getRenderer().getTextHeight(comp.text) : -ClickGui.CONTEXT.getRenderer().getTextHeight(comp.text)) {
            comp = (Info.InfoComponent) iterator.next();
            int x = ((String) this.order.getValue()).contains("Right") ? ((Number) this.xPos.getValue()).intValue() - ClickGui.CONTEXT.getRenderer().getTextWidth(comp.text) : ((Number) this.xPos.getValue()).intValue();

            ClickGui.CONTEXT.getRenderer().renderText(comp.text, (float) x, (float) (((Number) this.yPos.getValue()).intValue() + this.off), comp.color, ClickGui.CONTEXT.getColorScheme().doesTextShadow());
        }

    }

    public void renderInfo(List info) {
        Info.InfoComponent comp;

        for (Iterator iterator = info.iterator(); iterator.hasNext(); this.off += ((String) this.order.getValue()).contains("Top") ? ClickGui.CONTEXT.getRenderer().getTextHeight(comp.text) : -ClickGui.CONTEXT.getRenderer().getTextHeight(comp.text)) {
            comp = (Info.InfoComponent) iterator.next();
            int x = ((String) this.order.getValue()).contains("Right") ? ((Number) this.xPos.getValue()).intValue() - ClickGui.CONTEXT.getRenderer().getTextWidth(comp.text) : ((Number) this.xPos.getValue()).intValue();

            ClickGui.CONTEXT.getRenderer().renderText(comp.text, (float) x, (float) (((Number) this.yPos.getValue()).intValue() + this.off), HudColors.getTextColor(((Number) this.yPos.getValue()).intValue() + this.off), ClickGui.CONTEXT.getColorScheme().doesTextShadow());
        }

    }

    private static int lambda$draw$0(Info.InfoComponent i) {
        return -ClickGui.CONTEXT.getRenderer().getTextWidth(i.text);
    }

    static class InfoComponent {

        Color color;
        String text;

        public InfoComponent(Color color, String text) {
            this.color = color;
            this.text = text;
        }
    }
}
