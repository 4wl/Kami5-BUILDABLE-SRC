package tech.mmmax.kami.impl.features.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.utils.color.ColorUtil;
import tech.mmmax.kami.api.utils.player.InventoryUtils;
import tech.mmmax.kami.api.utils.player.RotationUtil;
import tech.mmmax.kami.api.utils.player.TargetUtils;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.utils.world.BlockUtils;
import tech.mmmax.kami.api.utils.world.CrystalUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class CrystalAura extends Module {

    public static CrystalAura INSTANCE;
    Timer placeTimer = new Timer();
    Timer breakTimer = new Timer();
    Value lethalHealth = (new ValueBuilder()).withDescriptor("Lethal Health").withValue(Integer.valueOf(18)).withRange(Integer.valueOf(0), Integer.valueOf(36)).register(this);
    Value lethalMinDmg = (new ValueBuilder()).withDescriptor("Lethal Min Damage").withValue(Integer.valueOf(2)).withRange(Integer.valueOf(0), Integer.valueOf(36)).register(this);
    Value lethalMaxSelfDmg = (new ValueBuilder()).withDescriptor("Lethal Max Self DMG").withValue(Integer.valueOf(36)).withRange(Integer.valueOf(0), Integer.valueOf(36)).register(this);
    Value antiSuicide = (new ValueBuilder()).withDescriptor("Anti Suicide").withValue(Boolean.valueOf(false)).register(this);
    Value antiSuicideHealth = (new ValueBuilder()).withDescriptor("Anti Suicide Health").withValue(Integer.valueOf(36)).withRange(Integer.valueOf(0), Integer.valueOf(36)).register(this);
    Value antiSuicideFactor = (new ValueBuilder()).withDescriptor("Anti Suicide Factor").withValue(Integer.valueOf(4)).withRange(Integer.valueOf(0), Integer.valueOf(10)).register(this);
    Value minDamage = (new ValueBuilder()).withDescriptor("Min Damage").withValue(Double.valueOf(4.0D)).withRange(Double.valueOf(0.0D), Double.valueOf(20.0D)).register(this);
    Value maxSelfDamage = (new ValueBuilder()).withDescriptor("Max Self Damage").withValue(Double.valueOf(15.0D)).withRange(Double.valueOf(0.0D), Integer.valueOf(36)).register(this);
    Value targetRange = (new ValueBuilder()).withDescriptor("Target Range").withValue(Double.valueOf(7.0D)).withRange(Double.valueOf(3.0D), Double.valueOf(20.0D)).register(this);
    Value range = (new ValueBuilder()).withDescriptor("Range").withValue(Integer.valueOf(5)).withRange(Integer.valueOf(1), Integer.valueOf(10)).register(this);
    Value wallsRange = (new ValueBuilder()).withDescriptor("Walls Range").withValue(Integer.valueOf(3)).withRange(Integer.valueOf(1), Integer.valueOf(5)).register(this);
    Value raytraceHits = (new ValueBuilder()).withDescriptor("Raytrace Hits").withValue(Integer.valueOf(2)).withRange(Integer.valueOf(1), Integer.valueOf(9)).register(this);
    Value shrinkFactor = (new ValueBuilder()).withDescriptor("Shrink Factor").withValue(Double.valueOf(0.3D)).withRange(Integer.valueOf(0), Integer.valueOf(1)).register(this);
    Value breakDelay = (new ValueBuilder()).withDescriptor("Break Delay").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(0), Integer.valueOf(1000)).withAction((setting) -> {
        //this.breakTimer.setDelay(((Number) setting.getValue()).longValue());
        //wtf is this
    }).register(this);
    Value placeDelay = (new ValueBuilder()).withDescriptor("Place Delay").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(0), Integer.valueOf(1000)).withAction((setting) -> {
      //  this.placeTimer.setDelay(((Number) setting.getValue()).longValue());
    }).register(this);
    Value inhibit = (new ValueBuilder()).withDescriptor("Inhibit").withValue(Boolean.valueOf(true)).register(this);
    Value fastBreak = (new ValueBuilder()).withDescriptor("Fast Break").withValue(Boolean.valueOf(true)).register(this);
    Value noStuckChain = (new ValueBuilder()).withDescriptor("No Stuck Chain").withValue(Integer.valueOf(3)).withRange(Integer.valueOf(0), Integer.valueOf(10)).register(this);
    Value breakCrystals = (new ValueBuilder()).withDescriptor("Break").withValue(Boolean.valueOf(true)).register(this);
    Value breakRotate = (new ValueBuilder()).withDescriptor("Break Rotate").withValue(Boolean.valueOf(true)).register(this);
    Value breakAttempts = (new ValueBuilder()).withDescriptor("Break Attempts").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(1), Integer.valueOf(10)).register(this);
    Value onlyOwnHealth = (new ValueBuilder()).withDescriptor("Only Own Health").withValue(Integer.valueOf(36)).withRange(Integer.valueOf(0), Integer.valueOf(36)).register(this);
    Value setDead = (new ValueBuilder()).withDescriptor("Set Dead").withValue(Boolean.valueOf(true)).register(this);
    Value swingMode = (new ValueBuilder()).withDescriptor("Swing Mode").withValue("Auto").withModes(new String[] { "Auto", "Mainhand", "Offhand", "None"}).register(this);
    Value placeCrystals = (new ValueBuilder()).withDescriptor("Place").withValue(Boolean.valueOf(true)).register(this);
    Value fastTickPlace = (new ValueBuilder()).withDescriptor("Fast Tick Place").withValue(Boolean.valueOf(false)).register(this);
    Value one13 = (new ValueBuilder()).withDescriptor("1.13").withValue(Boolean.valueOf(false)).register(this);
    Value packetPlace = (new ValueBuilder()).withDescriptor("Packet Place").withValue(Boolean.valueOf(true)).register(this);
    Value placeRotate = (new ValueBuilder()).withDescriptor("Place Rotate").withValue(Boolean.valueOf(true)).register(this);
    Value switchToSlot = (new ValueBuilder()).withDescriptor("Switch").withValue(Boolean.valueOf(false)).register(this);
    Value ghostSwitch = (new ValueBuilder()).withDescriptor("Ghost Switch").withValue(Boolean.valueOf(false)).register(this);
    Value placeAttempts = (new ValueBuilder()).withDescriptor("Place Attempts").withValue(Integer.valueOf(2)).withRange(Integer.valueOf(1), Integer.valueOf(5)).register(this);
    Value placeInhibit = (new ValueBuilder()).withDescriptor("Place Inhibit").withValue(Boolean.valueOf(false)).register(this);
    Value placeBlocks = (new ValueBuilder()).withDescriptor("Place Blocks").withValue(Boolean.valueOf(false)).register(this);
    Value moveFactor = (new ValueBuilder()).withDescriptor("Force Power").withValue(Double.valueOf(0.2D)).withRange(Double.valueOf(0.0D), Double.valueOf(5.0D)).register(this);
    Value fastTickBreak = (new ValueBuilder()).withDescriptor("Fast Tick B`reak").withValue(Boolean.valueOf(false)).register(this);
    Value predict = (new ValueBuilder()).withDescriptor("Predict").withValue(Boolean.valueOf(false)).register(this);
    Value antiWeakness = (new ValueBuilder()).withDescriptor("Anti Weakness").withValue(Boolean.valueOf(true)).register(this);
    Value breakPredictAttempts = (new ValueBuilder()).withDescriptor("Predict Attempts").withValue(Integer.valueOf(3)).withRange(Integer.valueOf(1), Integer.valueOf(10)).register(this);
    Value autoSkip = (new ValueBuilder()).withDescriptor("Auto Skip").withValue(Boolean.valueOf(true)).register(this);
    Value skip = (new ValueBuilder()).withDescriptor("Predict Skip").withValue(Integer.valueOf(2)).withRange(Integer.valueOf(1), Integer.valueOf(10)).register(this);
    Value startFactor = (new ValueBuilder()).withDescriptor("Predict Start").withValue(Integer.valueOf(2)).withRange(Integer.valueOf(0), Integer.valueOf(10)).register(this);
    Value add = (new ValueBuilder()).withDescriptor("Predict Add").withValue(Integer.valueOf(2)).withRange(Integer.valueOf(0), Integer.valueOf(10)).register(this);
    Value smartPredict = (new ValueBuilder()).withDescriptor("Smart Predict").withValue(Boolean.valueOf(true)).register(this);
    Value fade = (new ValueBuilder()).withDescriptor("Fade").withValue(Boolean.valueOf(false)).register(this);
    Value fadeTime = (new ValueBuilder()).withDescriptor("Fade Time").withValue(Integer.valueOf(1000)).withRange(Integer.valueOf(100), Integer.valueOf(2000)).register(this);
    Value pulse = (new ValueBuilder()).withDescriptor("Pulse").withValue(Boolean.valueOf(false)).register(this);
    Value pulseAmount = (new ValueBuilder()).withDescriptor("Pulse Amount").withValue(Integer.valueOf(10)).withRange(Integer.valueOf(5), Integer.valueOf(50)).register(this);
    Value pulseTime = (new ValueBuilder()).withDescriptor("Pulse Time").withValue(Integer.valueOf(100)).withRange(Integer.valueOf(50), Integer.valueOf(1000)).register(this);
    Value fillColorS = (new ValueBuilder()).withDescriptor("Fill Color").withValue(new Color(0, 0, 0, 100)).register(this);
    Value lineColorS = (new ValueBuilder()).withDescriptor("Outline Color").withValue(new Color(255, 255, 255, 255)).register(this);
    EntityLivingBase target;
    CrystalUtil.Crystal placePos;
    List oldPlacements = new ArrayList();
    int highestID;
    int lastSkip;
    int curAlpha;
    boolean shouldPredict;
    int currStuck;
    long lastBroke;

    public CrystalAura() {
        super("Crystal Aura", Feature.Category.Combat);
        this.curAlpha = ((Color) this.fillColorS.getValue()).getAlpha();
        this.shouldPredict = false;
        this.currStuck = 0;
        this.lastBroke = System.currentTimeMillis();
        CrystalAura.INSTANCE = this;
    }

    public void onEnable() {
        super.onEnable();
        this.breakTimer.resetDelay();
        this.breakTimer.setPaused(false);
        this.placeTimer.resetDelay();
        this.placeTimer.setPaused(true);
        this.highestID = 0;
        this.currStuck = 0;
        if (!NullUtils.nullCheck()) {
            RotationUtil.INSTANCE.rotating = false;
            RotationUtil.INSTANCE.resetRotations();
        }
    }

    public void onDisable() {
        super.onDisable();
        if (!NullUtils.nullCheck()) {
            RotationUtil.INSTANCE.rotating = false;
            RotationUtil.INSTANCE.resetRotations();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (RotationUtil.INSTANCE.rotatedYaw == RotationUtil.INSTANCE.yaw && RotationUtil.INSTANCE.rotatedPitch == RotationUtil.INSTANCE.pitch) {
                RotationUtil.INSTANCE.rotating = false;
                RotationUtil.INSTANCE.resetRotations();
            }

            this.target = TargetUtils.getTarget(((Number) this.targetRange.getValue()).doubleValue());
            if (this.target != null) {
                this.doCrystalAura(event);
            }
        }
    }

    public void doCrystalAura(TickEvent event) {
        int oldSlotWeak;
        int i;
        int i1;

        if (this.placeTimer.isPassed() && ((Boolean) this.placeCrystals.getValue()).booleanValue()) {
            if (((Boolean) this.fastTickPlace.getValue()).booleanValue() || event instanceof ClientTickEvent) {
                int crystal = InventoryUtils.getInventoryItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN));
                boolean swordSlot = (double) (this.target.getHealth() + this.target.getAbsorptionAmount()) <= ((Number) this.lethalHealth.getValue()).doubleValue();

                this.placePos = CrystalUtil.getPlacePos(this.target, ((Number) this.range.getValue()).doubleValue(), ((Number) this.wallsRange.getValue()).doubleValue(), ((Boolean) this.one13.getValue()).booleanValue(), ((Number) this.moveFactor.getValue()).doubleValue(), ((Boolean) this.antiSuicide.getValue()).booleanValue() && (double) (CrystalAura.mc.player.getHealth() + CrystalAura.mc.player.getAbsorptionAmount()) <= ((Number) this.antiSuicideHealth.getValue()).doubleValue(), ((Number) this.antiSuicideFactor.getValue()).doubleValue(), swordSlot ? ((Number) this.lethalMinDmg.getValue()).doubleValue() : ((Number) this.minDamage.getValue()).doubleValue(), swordSlot ? ((Number) this.lethalMaxSelfDmg.getValue()).doubleValue() : ((Number) this.maxSelfDamage.getValue()).doubleValue(), ((Boolean) this.placeInhibit.getValue()).booleanValue(), ((Boolean) this.placeBlocks.getValue()).booleanValue() && crystal != -1, ((Number) this.raytraceHits.getValue()).intValue(), ((Number) this.shrinkFactor.getValue()).doubleValue());
                if (this.placePos != null) {
                    if (this.oldPlacements.contains(this.placePos)) {
                        this.oldPlacements.remove(this.placePos);
                    }

                    this.oldPlacements.add(this.placePos);
                    oldSlotWeak = CrystalAura.mc.player.inventory.currentItem;
                    if (this.placePos.blockUnder) {
                        i = CrystalAura.mc.player.inventory.currentItem;
                        InventoryUtils.switchToSlotGhost(crystal);
                        BlockUtils.placeBlock(this.placePos.crystalPos.add(0, -1, 0), true);
                        InventoryUtils.switchToSlotGhost(i);
                    }

                    if (CrystalUtil.getCrystalHand() == EnumHand.MAIN_HAND) {
                        if (((Boolean) this.switchToSlot.getValue()).booleanValue()) {
                            InventoryUtils.switchToSlot(Items.END_CRYSTAL);
                        } else if (((Boolean) this.ghostSwitch.getValue()).booleanValue()) {
                            i = InventoryUtils.getHotbarItemSlot(Items.END_CRYSTAL);
                            if (i == -1) {
                                return;
                            }

                            InventoryUtils.switchToSlotGhost(i);
                        }
                    }

                    if (CrystalAura.mc.player.getHeldItem(CrystalUtil.getCrystalHand()).getItem() == Items.END_CRYSTAL || ((Boolean) this.ghostSwitch.getValue()).booleanValue()) {
                        EnumHand enumhand = ((String) this.swingMode.getValue()).equals("None") ? null : (((String) this.swingMode.getValue()).equals("Auto") ? CrystalUtil.getCrystalHand() : (((String) this.swingMode.getValue()).equals("Offhand") ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));

                        for (i1 = 0; i1 < ((Number) this.placeAttempts.getValue()).intValue(); ++i1) {
                            CrystalUtil.placeCrystal(this.placePos.crystalPos, ((Boolean) this.packetPlace.getValue()).booleanValue(), enumhand);
                        }

                        this.shouldPredict = true;
                    }

                    if (CrystalUtil.getCrystalHand() == EnumHand.MAIN_HAND && ((Boolean) this.ghostSwitch.getValue()).booleanValue() && !((Boolean) this.switchToSlot.getValue()).booleanValue()) {
                        InventoryUtils.switchToSlotGhost(oldSlotWeak);
                    }
                }
            }

            this.placeTimer.resetDelay();
            this.breakTimer.setPaused(false);
            this.placeTimer.setPaused(true);
            ++this.currStuck;
            if (this.currStuck > ((Number) this.noStuckChain.getValue()).intValue()) {
                this.currStuck = 0;
                return;
            }
        }

        if (this.breakTimer.isPassed()) {
            if (((Boolean) this.breakCrystals.getValue()).booleanValue() && (((Boolean) this.fastTickBreak.getValue()).booleanValue() || event instanceof ClientTickEvent)) {
                EntityEnderCrystal entityendercrystal = CrystalUtil.getCrystalToBreak(((Boolean) this.inhibit.getValue()).booleanValue(), ((Number) this.range.getValue()).doubleValue());

                oldSlotWeak = CrystalAura.mc.player.inventory.currentItem;

                for (i = 0; i < 9; ++i) {
                    if (CrystalAura.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemSword) {
                        i = i;
                    }
                }

                if (((Boolean) this.antiWeakness.getValue()).booleanValue() && i != -1 && CrystalAura.mc.player.isPotionActive(MobEffects.WEAKNESS) && entityendercrystal != null) {
                    InventoryUtils.switchToSlotGhost(i);
                }

                if (((Boolean) this.predict.getValue()).booleanValue()) {
                    if (!((Boolean) this.smartPredict.getValue()).booleanValue() || this.shouldPredict) {
                        i = this.highestID + ((Number) this.startFactor.getValue()).intValue();

                        for (i1 = 0; i1 < ((Number) this.breakPredictAttempts.getValue()).intValue(); ++i1) {
                            int crystalID = i + i1 * (((Boolean) this.autoSkip.getValue()).booleanValue() ? this.lastSkip : ((Number) this.skip.getValue()).intValue());

                            if (!CrystalUtil.hitCrystals.contains(Integer.valueOf(crystalID))) {
                                CrystalUtil.breakCrystal(crystalID);
                            }
                        }

                        this.highestID += ((Number) this.add.getValue()).intValue();
                        this.shouldPredict = false;
                    }
                } else if (entityendercrystal != null) {
                    if (((Boolean) this.breakRotate.getValue()).booleanValue()) {
                        RotationUtil.INSTANCE.rotating = true;
                        RotationUtil.INSTANCE.rotate(entityendercrystal.getPositionVector());
                    }

                    for (i = 0; i < ((Number) this.breakAttempts.getValue()).intValue(); ++i) {
                        CrystalUtil.breakCrystal(entityendercrystal);
                    }

                    this.curAlpha = ((Color) this.fillColorS.getValue()).getAlpha() + ((Number) this.pulseAmount.getValue()).intValue();
                    this.lastBroke = System.currentTimeMillis();
                }

                if (((Boolean) this.antiWeakness.getValue()).booleanValue() && i != -1 && CrystalAura.mc.player.isPotionActive(MobEffects.WEAKNESS) && entityendercrystal != null) {
                    InventoryUtils.switchToSlotGhost(oldSlotWeak);
                }
            }

            this.breakTimer.resetDelay();
            this.placeTimer.setPaused(false);
            this.breakTimer.setPaused(true);
            ++this.currStuck;
            if (this.currStuck > ((Number) this.noStuckChain.getValue()).intValue()) {
                this.currStuck = 0;
                return;
            }
        }

    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!NullUtils.nullCheck()) {
            if (event.getPacket() instanceof SPacketSpawnObject) {
                SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();

                if (((Boolean) this.fastBreak.getValue()).booleanValue() && this.target != null && packet.getType() == 51 && CrystalAura.mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= ((Number) this.range.getValue()).doubleValue() && ((double) CrystalAura.mc.player.getHealth() >= ((Number) this.onlyOwnHealth.getValue()).doubleValue() || this.isSelfCrystal(new BlockPos(packet.getX(), packet.getY(), packet.getZ())))) {
                    for (int i = 0; i < ((Number) this.breakAttempts.getValue()).intValue(); ++i) {
                        CrystalUtil.breakCrystal(packet.getEntityID());
                    }

                    this.curAlpha = ((Color) this.fillColorS.getValue()).getAlpha() + ((Number) this.pulseAmount.getValue()).intValue();
                    this.lastBroke = System.currentTimeMillis();
                    this.breakTimer.resetDelay();
                    this.placeTimer.setPaused(false);
                    this.breakTimer.setPaused(true);
                }

                this.checkID(packet.getEntityID());
            }

            if (event.getPacket() instanceof SPacketSoundEffect) {
                SPacketSoundEffect spacketsoundeffect = (SPacketSoundEffect) event.getPacket();

                if (((Boolean) this.setDead.getValue()).booleanValue() && spacketsoundeffect.getCategory() == SoundCategory.BLOCKS && spacketsoundeffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    Iterator iterator = CrystalUtil.getLoadedCrystalsInRange(((Number) this.range.getValue()).doubleValue()).iterator();

                    while (iterator.hasNext()) {
                        Entity c = (Entity) iterator.next();

                        if (c.getDistance(spacketsoundeffect.getX(), spacketsoundeffect.getY(), spacketsoundeffect.getZ()) <= 6.0D) {
                            c.setDead();
                        }
                    }
                }
            }

        }
    }

    boolean isSelfCrystal(BlockPos pos) {
        double targetDamage = (double) CrystalUtil.calculateDamage((double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D, this.target, 0.0D);
        double selfDamage = (double) CrystalUtil.calculateDamage((double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D, CrystalAura.mc.player, 0.0D);
        boolean lethal = (double) (this.target.getHealth() + this.target.getAbsorptionAmount()) <= ((Number) this.lethalHealth.getValue()).doubleValue();
        double minDMG = lethal ? ((Number) this.lethalMinDmg.getValue()).doubleValue() : ((Number) this.minDamage.getValue()).doubleValue();
        double maxDMG = lethal ? ((Number) this.lethalMaxSelfDmg.getValue()).doubleValue() : ((Number) this.maxSelfDamage.getValue()).doubleValue();

        return this.placePos.crystalPos.equals(pos) || targetDamage > minDMG && selfDamage < maxDMG && targetDamage > selfDamage;
    }

    void checkID(int id) {
        if (id > this.highestID) {
            this.lastSkip = this.highestID - id;
            this.highestID = id;
        }

    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!NullUtils.nullCheck()) {
            if (this.target != null && this.placePos != null) {
                if (this.oldPlacements.size() > 0) {
                    this.oldPlacements = (List) this.oldPlacements.stream().filter(Objects::nonNull).distinct().filter((crystal) -> {
                        return System.currentTimeMillis() < ((Number) this.fadeTime.getValue()).longValue();
                    }).collect(Collectors.toList());
                }

                if (System.currentTimeMillis() - this.lastBroke <= ((Number) this.pulseTime.getValue()).longValue() && ((Boolean) this.pulse.getValue()).booleanValue()) {
                    double fillColor = this.normalize((double) (System.currentTimeMillis() - this.lastBroke), 0.0D, ((Number) this.pulseTime.getValue()).doubleValue());

                    fillColor = MathHelper.clamp(fillColor, 0.0D, 1.0D);
                    fillColor = -fillColor;
                    ++fillColor;
                    this.curAlpha = (int) MathHelper.clamp((double) ((Color) this.fillColorS.getValue()).getAlpha() + fillColor * ((Number) this.pulseAmount.getValue()).doubleValue(), 0.0D, 255.0D);
                } else {
                    this.curAlpha = ((Color) this.fillColorS.getValue()).getAlpha();
                }

                Color color = ColorUtil.newAlpha((Color) this.fillColorS.getValue(), this.curAlpha);
                Color lineColor = (Color) this.lineColorS.getValue();

                GL11.glLineWidth(1.7F);
                RenderUtil.renderBB(7, new AxisAlignedBB(this.placePos.crystalPos), color, color);
                RenderUtil.renderBB(3, new AxisAlignedBB(this.placePos.crystalPos), lineColor, lineColor);
                if (((Boolean) this.fade.getValue()).booleanValue()) {
                    Iterator iterator = this.oldPlacements.iterator();

                    while (iterator.hasNext()) {
                        CrystalUtil.Crystal crystal = (CrystalUtil.Crystal) iterator.next();

                        if (this.placePos == null || !crystal.crystalPos.equals(this.placePos.crystalPos)) {
                            double normal = this.normalize((double) (System.currentTimeMillis() - crystal.getStartTime()), 0.0D, ((Number) this.fadeTime.getValue()).doubleValue());
                            Color fillFade = ColorUtil.interpolate((float) normal, ColorUtil.newAlpha(color, 0), color);
                            Color outlineFade = ColorUtil.interpolate((float) normal, ColorUtil.newAlpha(lineColor, 0), lineColor);

                            RenderUtil.renderBB(7, new AxisAlignedBB(crystal.crystalPos), fillFade, fillFade);
                            RenderUtil.renderBB(3, new AxisAlignedBB(crystal.crystalPos), outlineFade, outlineFade);
                        }
                    }
                }

            }
        }
    }

    public String getHudInfo() {
        return this.target != null ? this.target.getName() + ", " + (double) (System.currentTimeMillis() - this.breakTimer.getStartTime()) / 10.0D + (this.placePos != null ? ", " + (((Boolean) this.antiSuicide.getValue()).booleanValue() ? this.placePos.getEnemyDamage() - this.placePos.getSelfDamage() : this.placePos.getEnemyDamage()) : "") : "";
    }

    double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}
