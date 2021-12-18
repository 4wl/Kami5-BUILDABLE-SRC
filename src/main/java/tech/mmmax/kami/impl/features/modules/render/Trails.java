package tech.mmmax.kami.impl.features.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.utils.color.ColorUtil;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class Trails extends Module {

    Value lineWidth = (new ValueBuilder()).withDescriptor("Line Width").withValue(Integer.valueOf(2)).withRange(Double.valueOf(0.1D), Integer.valueOf(5)).register(this);
    Value lifetime = (new ValueBuilder()).withDescriptor("Lifetime").withValue(Integer.valueOf(1000)).withRange(Integer.valueOf(0), Integer.valueOf(5000)).register(this);
    Value fade = (new ValueBuilder()).withDescriptor("Fade").withValue(Boolean.valueOf(true)).register(this);
    Value xp = (new ValueBuilder()).withDescriptor("XP").withValue(Boolean.valueOf(false)).register(this);
    Value arrow = (new ValueBuilder()).withDescriptor("Arrow").withValue(Boolean.valueOf(false)).register(this);
    Value startColor = (new ValueBuilder()).withDescriptor("Start Color").withValue(new Color(255, 255, 255)).register(this);
    Value endColor = (new ValueBuilder()).withDescriptor("End Color").withValue(new Color(0, 255, 72)).register(this);
    Value self = (new ValueBuilder()).withDescriptor("Self").withValue(Boolean.valueOf(false)).register(this);
    Value selfTime = (new ValueBuilder()).withDescriptor("Self Time").withValue(Integer.valueOf(1000)).withRange(Integer.valueOf(0), Integer.valueOf(2000)).register(this);
    Value target = (new ValueBuilder()).withDescriptor("Target").withValue(Boolean.valueOf(false)).register(this);
    Value targetTime = (new ValueBuilder()).withDescriptor("Target Time").withValue(Integer.valueOf(1000)).withRange(Integer.valueOf(0), Integer.valueOf(2000)).register(this);
    Map trails = new HashMap();

    public Trails() {
        super("Trails", Feature.Category.Render);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            Iterator playerTrail = Trails.mc.world.loadedEntityList.iterator();

            while (playerTrail.hasNext()) {
                Entity toRemove = (Entity) playerTrail.next();

                if (this.allowEntity(toRemove)) {
                    if (this.trails.containsKey(toRemove.getUniqueID())) {
                        if (toRemove.isDead) {
                            if (((Trails.ItemTrail) this.trails.get(toRemove.getUniqueID())).timer.isPaused()) {
                                ((Trails.ItemTrail) this.trails.get(toRemove.getUniqueID())).timer.resetDelay();
                            }

                            ((Trails.ItemTrail) this.trails.get(toRemove.getUniqueID())).timer.setPaused(false);
                        } else {
                            ((Trails.ItemTrail) this.trails.get(toRemove.getUniqueID())).positions.add(new Trails.Position(toRemove.getPositionVector()));
                        }
                    } else {
                        this.trails.put(toRemove.getUniqueID(), new Trails.ItemTrail(toRemove));
                    }
                }
            }

            if (((Boolean) this.self.getValue()).booleanValue()) {
                if (this.trails.containsKey(Trails.mc.player.getUniqueID())) {
                    Trails.ItemTrail playerTrail1 = (Trails.ItemTrail) this.trails.get(Trails.mc.player.getUniqueID());

                    playerTrail1.timer.resetDelay();
                    ArrayList toRemove1 = new ArrayList();
                    Iterator iterator = playerTrail1.positions.iterator();

                    while (iterator.hasNext()) {
                        Trails.Position position = (Trails.Position) iterator.next();

                        if (System.currentTimeMillis() - position.time > ((Number) this.selfTime.getValue()).longValue()) {
                            toRemove1.add(position);
                        }
                    }

                    playerTrail1.positions.removeAll(toRemove1);
                    playerTrail1.positions.add(new Trails.Position(Trails.mc.player.getPositionVector()));
                } else {
                    this.trails.put(Trails.mc.player.getUniqueID(), new Trails.ItemTrail(Trails.mc.player));
                }
            } else if (this.trails.containsKey(Trails.mc.player.getUniqueID())) {
                this.trails.remove(Trails.mc.player.getUniqueID());
            }

        }
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if (!NullUtils.nullCheck()) {
            Iterator iterator = this.trails.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                if (((Trails.ItemTrail) entry.getValue()).entity.isDead || Trails.mc.world.getEntityByID(((Trails.ItemTrail) entry.getValue()).entity.getEntityId()) == null) {
                    if (((Trails.ItemTrail) entry.getValue()).timer.isPaused()) {
                        ((Trails.ItemTrail) entry.getValue()).timer.resetDelay();
                    }

                    ((Trails.ItemTrail) entry.getValue()).timer.setPaused(false);
                }

                if (!((Trails.ItemTrail) entry.getValue()).timer.isPassed()) {
                    this.drawTrail((Trails.ItemTrail) entry.getValue());
                }
            }

        }
    }

    public void drawTrail(Trails.ItemTrail trail) {
        double fadeAmount = this.normalize((double) (System.currentTimeMillis() - trail.timer.getStartTime()), 0.0D, ((Number) this.lifetime.getValue()).doubleValue());
        int alpha = (int) (fadeAmount * 255.0D);

        alpha = MathHelper.clamp(alpha, 0, 255);
        alpha = 255 - alpha;
        alpha = trail.timer.isPaused() ? 255 : alpha;
        Color fadeColor = ColorUtil.newAlpha((Color) this.startColor.getValue(), alpha);

        RenderUtil.prepare();
        GL11.glLineWidth(((Number) this.lineWidth.getValue()).floatValue());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtil.builder = RenderUtil.tessellator.getBuffer();
        RenderUtil.builder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        this.buildBuffer(RenderUtil.builder, trail, (Color) this.endColor.getValue(), ((Boolean) this.fade.getValue()).booleanValue() ? fadeColor : (Color) this.endColor.getValue());
        RenderUtil.tessellator.draw();
        RenderUtil.release();
    }

    public void buildBuffer(BufferBuilder builder, Trails.ItemTrail trail, Color start, Color end) {
        Iterator iterator = trail.positions.iterator();

        while (iterator.hasNext()) {
            Trails.Position p = (Trails.Position) iterator.next();
            Vec3d pos = RenderUtil.updateToCamera(p.pos);
            double value = this.normalize((double) trail.positions.indexOf(p), 0.0D, (double) trail.positions.size());

            RenderUtil.addBuilderVertex(builder, pos.x, pos.y, pos.z, ColorUtil.interpolate((float) value, start, end));
        }

    }

    boolean allowEntity(Entity e) {
        return e instanceof EntityEnderPearl || e instanceof EntityExpBottle && ((Boolean) this.xp.getValue()).booleanValue() || e instanceof EntityArrow && ((Boolean) this.arrow.getValue()).booleanValue();
    }

    double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    public static class Position {

        public Vec3d pos;
        public long time;

        public Position(Vec3d pos) {
            this.pos = pos;
            this.time = System.currentTimeMillis();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                Trails.Position position = (Trails.Position) o;

                return this.time == position.time && Objects.equals(this.pos, position.pos);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[] { this.pos, Long.valueOf(this.time)});
        }
    }

    public class ItemTrail {

        public Entity entity;
        public List positions;
        public Timer timer;

        public ItemTrail(Entity entity) {
            this.entity = entity;
            this.positions = new ArrayList();
            this.timer = new Timer();
            this.timer.setDelay(((Number) Trails.this.lifetime.getValue()).longValue());
            this.timer.setPaused(true);
        }
    }
}
