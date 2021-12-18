package tech.mmmax.kami.impl.features.modules.render;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.event.RenderEntityEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.color.ColorUtil;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class Chams extends Module {

    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    Value players = (new ValueBuilder()).withDescriptor("Living").withValue(Boolean.valueOf(false)).register(this);
    Value livingFill = (new ValueBuilder()).withDescriptor("Living Fill").withValue(new Color(255, 255, 255, 255)).register(this);
    Value livingLine = (new ValueBuilder()).withDescriptor("Living Line").withValue(new Color(255, 255, 255, 255)).register(this);
    Value livingGlint = (new ValueBuilder()).withDescriptor("Living Glint").withValue(Boolean.valueOf(true)).register(this);
    Value livingGlintColor = (new ValueBuilder()).withDescriptor("Living Glint Color").withValue(new Color(255, 255, 255, 255)).register(this);
    Value crystals = (new ValueBuilder()).withDescriptor("Crystals").withValue(Boolean.valueOf(true)).register(this);
    Value crystalRotateSpeed = (new ValueBuilder()).withDescriptor("Rotate Speed").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(0), Integer.valueOf(10)).register(this);
    Value crystalScale = (new ValueBuilder()).withDescriptor("Crystal Scale").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(0), Integer.valueOf(3)).register(this);
    Value crystalFill = (new ValueBuilder()).withDescriptor("Crystal Fill").withValue(new Color(255, 255, 255, 255)).register(this);
    Value crystalLine = (new ValueBuilder()).withDescriptor("Crystal Line").withValue(new Color(255, 255, 255, 255)).register(this);
    Value crystalGlint = (new ValueBuilder()).withDescriptor("Crystal Glint").withValue(Boolean.valueOf(true)).register(this);
    Value crystalGlintColor = (new ValueBuilder()).withDescriptor("Crystal Glint Color").withValue(new Color(255, 255, 255, 255)).register(this);
    Value lineWidth = (new ValueBuilder()).withDescriptor("Line Width").withValue(Float.valueOf(2.0F)).withRange(Double.valueOf(0.1D), Integer.valueOf(5)).register(this);
    Value lineWidthInterp = (new ValueBuilder()).withDescriptor("Line Width Interp").withValue(Float.valueOf(5.0F)).withRange(Double.valueOf(0.1D), Integer.valueOf(15)).register(this);
    Value customBlendFunc = (new ValueBuilder()).withDescriptor("Blend Func").withValue(Boolean.valueOf(true)).register(this);

    public Chams() {
        super("Chams", Feature.Category.Render);
    }

    @SubscribeEvent
    public void renderEntity(RenderEntityEvent event) {
        boolean nullCheck = Chams.mc.player == null || Chams.mc.world == null || event.entityIn == null;

        if (!(event.entityIn instanceof EntityEnderCrystal) || ((Boolean) this.crystals.getValue()).booleanValue()) {
            if (!(event.entityIn instanceof EntityLivingBase) || ((Boolean) this.players.getValue()).booleanValue()) {
                RenderUtil.prepare();
                GL11.glPushAttrib(1048575);
                if (((Boolean) this.customBlendFunc.getValue()).booleanValue()) {
                    GL11.glBlendFunc(770, '耄');
                }

                GL11.glEnable(2881);
                GL11.glEnable(2848);
                Color line = event.entityIn instanceof EntityLivingBase ? (Color) this.livingLine.getValue() : (Color) this.crystalLine.getValue();
                Color fill = event.entityIn instanceof EntityLivingBase ? (Color) this.livingFill.getValue() : (Color) this.crystalFill.getValue();
                boolean texture = event.entityIn instanceof EntityLivingBase ? ((Boolean) this.livingGlint.getValue()).booleanValue() : ((Boolean) this.crystalGlint.getValue()).booleanValue();
                Color textureColor = event.entityIn instanceof EntityLivingBase ? (Color) this.livingGlintColor.getValue() : (Color) this.crystalGlintColor.getValue();
                float limbSwingAmt = event.entityIn instanceof EntityEnderCrystal ? event.limbSwingAmount * ((Number) this.crystalRotateSpeed.getValue()).floatValue() : event.limbSwingAmount;

                if (event.entityIn instanceof EntityEnderCrystal) {
                    ((Number) this.crystalScale.getValue()).floatValue();
                } else {
                    float f = event.scale;
                }

                GlStateManager.glLineWidth(nullCheck ? ((Number) this.lineWidth.getValue()).floatValue() : RenderUtil.getInterpolatedLinWid(Chams.mc.player.getDistance(event.entityIn) + 1.0F, ((Number) this.lineWidth.getValue()).floatValue(), ((Number) this.lineWidthInterp.getValue()).floatValue()));
                GlStateManager.disableAlpha();
                if (texture) {
                    Chams.mc.getTextureManager().bindTexture(Chams.RES_ITEM_GLINT);
                    GL11.glTexCoord3d(1.0D, 1.0D, 1.0D);
                    GL11.glEnable(3553);
                    GL11.glBlendFunc(768, 771);
                    ColorUtil.glColor(textureColor);
                    event.modelBase.render(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
                    if (((Boolean) this.customBlendFunc.getValue()).booleanValue()) {
                        GL11.glBlendFunc(770, '耄');
                    } else {
                        GL11.glBlendFunc(770, 771);
                    }
                }

                ColorUtil.glColor(fill);
                GL11.glDisable(3553);
                event.modelBase.render(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
                GL11.glPolygonMode(1032, 6913);
                ColorUtil.glColor(line);
                event.modelBase.render(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
                GL11.glPolygonMode(1032, 6914);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.popAttrib();
                RenderUtil.release();
                event.setCanceled(true);
            }
        }
    }
}
