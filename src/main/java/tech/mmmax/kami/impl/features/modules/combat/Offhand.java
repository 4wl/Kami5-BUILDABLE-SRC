package tech.mmmax.kami.impl.features.modules.combat;

import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Mouse;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.player.InventoryUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class Offhand extends Module {

    Value item = (new ValueBuilder()).withDescriptor("Item").withValue("Totem").withModes(new String[] { "Totem", "Gapple", "Crystal", "Bow"}).register(this);
    Value weakItem = (new ValueBuilder()).withDescriptor("Weak Item").withValue("Totem").withModes(new String[] { "Totem", "Gapple", "Crystal", "Bow"}).register(this);
    Value totemHealth = (new ValueBuilder()).withDescriptor("Totem Health").withValue(Integer.valueOf(15)).withRange(Integer.valueOf(0), Integer.valueOf(36)).register(this);
    Value swordGap = (new ValueBuilder()).withDescriptor("Sword Gap").withValue(Boolean.valueOf(false)).register(this);
    Value swordGapWeakness = (new ValueBuilder()).withDescriptor("Sword Gap Weakness").withValue(Boolean.valueOf(false)).register(this);

    public Offhand() {
        super("Offhand", Feature.Category.Combat);
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (Offhand.mc.currentScreen == null) {
            if (Offhand.mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() != this.getItem()) {
                InventoryUtils.moveItemToOffhand(this.getItem());
            }

        }
    }

    public Item getItem() {
        Item i = Items.TOTEM_OF_UNDYING;

        if (Offhand.mc.player.getHealth() + Offhand.mc.player.getAbsorptionAmount() > (float) ((Number) this.totemHealth.getValue()).intValue()) {
            if (((Boolean) this.swordGap.getValue()).booleanValue() && Offhand.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Items.DIAMOND_SWORD)) {
                if (!((Boolean) this.swordGapWeakness.getValue()).booleanValue() && !Mouse.isButtonDown(1)) {
                    i = this.getSelectedItem();
                } else {
                    i = Items.GOLDEN_APPLE;
                }
            } else {
                i = this.getSelectedItem();
            }
        }

        return i;
    }

    public Item getSelectedItem() {
        Object i = null;

        if (!Offhand.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            if (((String) this.item.getValue()).equalsIgnoreCase("totem")) {
                i = Items.TOTEM_OF_UNDYING;
            } else if (((String) this.item.getValue()).equals("Crystal")) {
                i = Items.END_CRYSTAL;
            } else if (((String) this.item.getValue()).equals("Gapple")) {
                i = Items.GOLDEN_APPLE;
            } else if (((String) this.item.getValue()).equals("Bow")) {
                i = Items.BOW;
            } else if (((String) this.item.getValue()).equals("String")) {
                i = Items.STRING;
            }
        } else if (((String) this.weakItem.getValue()).equalsIgnoreCase("totem")) {
            i = Items.TOTEM_OF_UNDYING;
        } else if (((String) this.weakItem.getValue()).equals("Crystal")) {
            i = Items.END_CRYSTAL;
        } else if (((String) this.weakItem.getValue()).equals("Gapple")) {
            i = Items.GOLDEN_APPLE;
        } else if (((String) this.weakItem.getValue()).equals("Bow")) {
            i = Items.BOW;
        } else if (((String) this.weakItem.getValue()).equals("String")) {
            i = Items.STRING;
        }

        return (Item) i;
    }
}
