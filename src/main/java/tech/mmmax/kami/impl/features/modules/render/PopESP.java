package tech.mmmax.kami.impl.features.modules.render;

import com.mojang.authlib.GameProfile;
import java.awt.Color;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.color.ColorUtil;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.features.modules.misc.FakePlayer;

public class PopESP extends Module {

    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    Value fadeStart = (new ValueBuilder()).withDescriptor("Fade Start").withValue(Integer.valueOf(100)).withRange(Integer.valueOf(0), Integer.valueOf(4000)).register(this);
    Value fadeTime = (new ValueBuilder()).withDescriptor("Fade Time").withValue(Integer.valueOf(500)).withRange(Integer.valueOf(0), Integer.valueOf(2000)).register(this);
    Value self = (new ValueBuilder()).withDescriptor("Self").withValue(Boolean.valueOf(false)).register(this);
    Value fillColorS = (new ValueBuilder()).withDescriptor("Fill Color").withValue(new Color(15, 100, 255, 100)).register(this);
    Value lineColorS = (new ValueBuilder()).withDescriptor("Line Color").withValue(new Color(15, 100, 255, 255)).register(this);
    Value glint = (new ValueBuilder()).withDescriptor("Glint").withValue(Boolean.valueOf(true)).register(this);
    Value glintColor = (new ValueBuilder()).withDescriptor("Glint Color").withValue(new Color(134, 55, 255, 123)).register(this);
    EntityOtherPlayerMP player = null;
    ModelPlayer playerModel = null;
    long startTime = 0L;

    public PopESP() {
        super("PopESP", Feature.Category.Render);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!NullUtils.nullCheck()) {
            if (event.getPacket() instanceof SPacketEntityStatus) {
                SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();

                if (packet.getOpCode() == 35 && packet.getEntity(PopESP.mc.world) != null && (((Boolean) this.self.getValue()).booleanValue() || packet.getEntity(PopESP.mc.world).getEntityId() != PopESP.mc.player.getEntityId())) {
                    GameProfile profile = new GameProfile(PopESP.mc.player.getUniqueID(), "");

                    this.player = new EntityOtherPlayerMP(FakePlayer.mc.world, profile);
                    this.player.copyLocationAndAnglesFrom(packet.getEntity(PopESP.mc.world));
                    this.playerModel = new ModelPlayer(0.0F, false);
                    this.startTime = System.currentTimeMillis();
                }
            }

        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!NullUtils.nullCheck()) {
            GL11.glLineWidth(1.0F);
            int lineA = ((Color) this.lineColorS.getValue()).getAlpha();
            int fillA = ((Color) this.fillColorS.getValue()).getAlpha();
            int glintA = ((Color) this.glintColor.getValue()).getAlpha();

            if (System.currentTimeMillis() - this.startTime > ((Number) this.fadeStart.getValue()).longValue()) {
                long lineColor = System.currentTimeMillis() - this.startTime - ((Number) this.fadeStart.getValue()).longValue();
                double finalGlintColor = this.normalize((double) lineColor, 0.0D, ((Number) this.fadeTime.getValue()).doubleValue());

                finalGlintColor = MathHelper.clamp(finalGlintColor, 0.0D, 1.0D);
                finalGlintColor = -finalGlintColor + 1.0D;
                lineA = (int) (finalGlintColor * (double) lineA);
                fillA = (int) (finalGlintColor * (double) fillA);
                glintA = (int) (finalGlintColor * (double) glintA);
            }

            Color lineColor1 = ColorUtil.newAlpha((Color) this.lineColorS.getValue(), lineA);
            Color fillColor = ColorUtil.newAlpha((Color) this.fillColorS.getValue(), fillA);
            Color finalGlintColor1 = ColorUtil.newAlpha((Color) this.glintColor.getValue(), glintA);

            if (this.player != null && this.playerModel != null) {
                RenderUtil.prepare();
                ColorUtil.glColor(fillColor);
                GL11.glPolygonMode(1032, 6914);
                RenderUtil.renderEntity(this.player, this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, (float) this.player.ticksExisted, this.player.rotationYawHead, this.player.rotationPitch, 1.0F);
                if (((Boolean) this.glint.getValue()).booleanValue()) {
                    PopESP.mc.getRenderManager().renderEngine.bindTexture(PopESP.RES_ITEM_GLINT);
                    GL11.glTexCoord3d(1.0D, 1.0D, 1.0D);
                    GL11.glEnable(3553);
                    GL11.glBlendFunc(768, 771);
                    ColorUtil.glColor(finalGlintColor1);
                    RenderUtil.renderEntity(this.player, this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, (float) this.player.ticksExisted, this.player.rotationYawHead, this.player.rotationPitch, 1.0F);
                    GL11.glBlendFunc(770, 771);
                }

                ColorUtil.glColor(lineColor1);
                GL11.glPolygonMode(1032, 6913);
                RenderUtil.renderEntity(this.player, this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, (float) this.player.ticksExisted, this.player.rotationYawHead, this.player.rotationPitch, 1.0F);
                GL11.glPolygonMode(1032, 6914);
                RenderUtil.release();
            }

        }
    }

    double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}
