package tech.mmmax.kami.api.utils.render;

import java.awt.Color;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.wrapper.IMinecraft;

public class RenderUtil implements IMinecraft {

    public static Tessellator tessellator = Tessellator.getInstance();
    public static BufferBuilder builder = RenderUtil.tessellator.getBuffer();

    public static float getInterpolatedLinWid(float distance, float line, float lineFactor) {
        return line * lineFactor / distance;
    }

    public static void renderBB(int glMode, AxisAlignedBB bb, Color bottom, Color top) {
        bb = updateToCamera(bb);
        prepare();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtil.builder = RenderUtil.tessellator.getBuffer();
        RenderUtil.builder.begin(glMode, DefaultVertexFormats.POSITION_COLOR);
        buildBBBuffer(RenderUtil.builder, bb, bottom, top);
        RenderUtil.tessellator.draw();
        release();
    }

    public static void renderBBFog(AxisAlignedBB bb, Color main, Color center) {
        bb = updateToCamera(bb);
        prepare();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtil.builder = RenderUtil.tessellator.getBuffer();
        RenderUtil.builder.begin(4, DefaultVertexFormats.POSITION_COLOR);
        buildBBBufferFog(RenderUtil.builder, bb, main, center);
        RenderUtil.tessellator.draw();
        release();
    }

    public static void buildBBBuffer(BufferBuilder builder, AxisAlignedBB bb, Color bottom, Color top) {
        addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, bottom);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.minZ, bottom);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, bottom);
        addBuilderVertex(builder, bb.minX, bb.minY, bb.maxZ, bottom);
        addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, bottom);
        addBuilderVertex(builder, bb.minX, bb.maxY, bb.minZ, top);
        addBuilderVertex(builder, bb.minX, bb.maxY, bb.maxZ, top);
        addBuilderVertex(builder, bb.minX, bb.minY, bb.maxZ, bottom);
        addBuilderVertex(builder, bb.minX, bb.minY, bb.maxZ, bottom);
        addBuilderVertex(builder, bb.minX, bb.maxY, bb.maxZ, top);
        addBuilderVertex(builder, bb.maxX, bb.maxY, bb.maxZ, top);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, bottom);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, bottom);
        addBuilderVertex(builder, bb.maxX, bb.maxY, bb.maxZ, top);
        addBuilderVertex(builder, bb.maxX, bb.maxY, bb.minZ, top);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.minZ, bottom);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.minZ, bottom);
        addBuilderVertex(builder, bb.maxX, bb.maxY, bb.minZ, top);
        addBuilderVertex(builder, bb.minX, bb.maxY, bb.minZ, top);
        addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, bottom);
        addBuilderVertex(builder, bb.minX, bb.maxY, bb.minZ, top);
        addBuilderVertex(builder, bb.maxX, bb.maxY, bb.minZ, top);
        addBuilderVertex(builder, bb.maxX, bb.maxY, bb.maxZ, top);
        addBuilderVertex(builder, bb.minX, bb.maxY, bb.maxZ, top);
        addBuilderVertex(builder, bb.minX, bb.maxY, bb.minZ, top);
    }

    public static void buildBBBufferFog(BufferBuilder builder, AxisAlignedBB bb, Color main, Color center) {
        double centerX = (bb.maxX - bb.minX) / 2.0D;
        double centerY = (bb.maxY - bb.minY) / 2.0D;
        double centerZ = (bb.maxZ - bb.minZ) / 2.0D;

        addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, main);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.minZ, main);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, main);
        addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, main);
        addBuilderVertex(builder, bb.minX + centerX, bb.maxY, bb.minZ + centerZ, center);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.minZ, main);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.minZ, main);
        addBuilderVertex(builder, bb.minX + centerX, bb.maxY, bb.minZ + centerZ, center);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, main);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, main);
        addBuilderVertex(builder, bb.minX, bb.minY, bb.maxZ, main);
        addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, main);
        addBuilderVertex(builder, bb.maxX, bb.minY, bb.maxZ, main);
        addBuilderVertex(builder, bb.minX + centerX, bb.maxY, bb.minZ + centerZ, center);
        addBuilderVertex(builder, bb.minX, bb.minY, bb.maxZ, main);
        addBuilderVertex(builder, bb.minX, bb.minY, bb.maxZ, main);
        addBuilderVertex(builder, bb.minX + centerX, bb.maxY, bb.minZ + centerZ, center);
        addBuilderVertex(builder, bb.minX, bb.minY, bb.minZ, main);
    }

    public static void addBuilderVertex(BufferBuilder bufferBuilder, double x, double y, double z, Color color) {
        bufferBuilder.pos(x, y, z).color((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F).endVertex();
    }

    public static void prepare() {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
    }

    public static void release() {
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GL11.glEnable(3553);
        GL11.glPolygonMode(1032, 6914);
    }

    public static AxisAlignedBB updateToCamera(AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minX - RenderUtil.mc.getRenderManager().viewerPosX, bb.minY - RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ - RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX - RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY - RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ - RenderUtil.mc.getRenderManager().viewerPosZ);
    }

    public static Vec3d updateToCamera(Vec3d vec) {
        return new Vec3d(vec.x - RenderUtil.mc.getRenderManager().viewerPosX, vec.y - RenderUtil.mc.getRenderManager().viewerPosY, vec.z - RenderUtil.mc.getRenderManager().viewerPosZ);
    }

    public static void renderEntity(EntityLivingBase entity, ModelBase modelBase, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (RenderUtil.mc.getRenderManager() != null) {
            if (modelBase instanceof ModelPlayer) {
                ModelPlayer partialTicks = (ModelPlayer) modelBase;

                partialTicks.bipedHeadwear.showModel = false;
                partialTicks.bipedBodyWear.showModel = false;
                partialTicks.bipedLeftLegwear.showModel = false;
                partialTicks.bipedRightLegwear.showModel = false;
                partialTicks.bipedLeftArmwear.showModel = false;
                partialTicks.bipedRightArmwear.showModel = false;
            }

            float partialTicks1 = RenderUtil.mc.getRenderPartialTicks();
            double x = entity.posX - RenderUtil.mc.getRenderManager().viewerPosX;
            double y = entity.posY - RenderUtil.mc.getRenderManager().viewerPosY;
            double z = entity.posZ - RenderUtil.mc.getRenderManager().viewerPosZ;

            GlStateManager.pushMatrix();
            if (entity.isSneaking()) {
                y -= 0.125D;
            }

            renderLivingAt(x, y, z);
            prepareRotations(entity);
            float f4 = prepareScale(entity, scale);
            float yaw = handleRotationFloat(entity, partialTicks1);

            GlStateManager.enableAlpha();
            modelBase.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks1);
            modelBase.setRotationAngles(limbSwing, limbSwingAmount, 0.0F, yaw, entity.rotationPitch, f4, entity);
            modelBase.render(entity, limbSwing, limbSwingAmount, 0.0F, yaw, entity.rotationPitch, f4);
            GlStateManager.popMatrix();
        }
    }

    public static void prepareTranslate(EntityLivingBase entityIn, double x, double y, double z) {
        renderLivingAt(x - RenderUtil.mc.getRenderManager().viewerPosX, y - RenderUtil.mc.getRenderManager().viewerPosY, z - RenderUtil.mc.getRenderManager().viewerPosZ);
    }

    public static void renderLivingAt(double x, double y, double z) {
        GlStateManager.translate((float) x, (float) y, (float) z);
    }

    public static float prepareScale(EntityLivingBase entity, float scale) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        double widthX = entity.getRenderBoundingBox().maxX - entity.getRenderBoundingBox().minX;
        double widthZ = entity.getRenderBoundingBox().maxZ - entity.getRenderBoundingBox().minZ;

        GlStateManager.scale((double) scale + widthX, (double) (scale * entity.height), (double) scale + widthZ);
        float f = 0.0625F;

        GlStateManager.translate(0.0F, -1.501F, 0.0F);
        return f;
    }

    public static void prepareRotations(EntityLivingBase entityLivingBase) {
        GlStateManager.rotate(180.0F - entityLivingBase.rotationYaw, 0.0F, 1.0F, 0.0F);
    }

    public static float roundAngle(float f) {
        while (f >= 360.0F) {
            f -= 360.0F;
        }

        return f;
    }

    public static float handleRotationFloat(EntityLivingBase livingBase, float partialTicks) {
        return livingBase.rotationYawHead;
    }

    public static void applyRotations(EntityLivingBase entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
        if (entityLiving.deathTime > 0) {
            float s = ((float) entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;

            s = MathHelper.sqrt(s);
            if (s > 1.0F) {
                s = 1.0F;
            }

            GlStateManager.rotate(s, 0.0F, 0.0F, 1.0F);
        } else {
            String s1 = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName());

            if (s1 != null && ("Dinnerbone".equals(s1) || "Grumm".equals(s1)) && (!(entityLiving instanceof EntityPlayer) || ((EntityPlayer) entityLiving).isWearing(EnumPlayerModelParts.CAPE))) {
                GlStateManager.translate(0.0F, entityLiving.height + 0.1F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            }
        }

    }
}
