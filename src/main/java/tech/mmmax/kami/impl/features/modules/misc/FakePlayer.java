package tech.mmmax.kami.impl.features.modules.misc;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.chat.ChatMessage;
import tech.mmmax.kami.api.utils.chat.ChatUtils;
import tech.mmmax.kami.api.utils.player.PlayerUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class FakePlayer extends Module {

    String playerName = "stinky";
    int eID = -696420;
    EntityPlayer fakePlayer;
    Value moveMode = (new ValueBuilder()).withDescriptor("Move").withValue("Looking").withModes(new String[] { "Looking", "Facing", "None"}).register(this);
    Value dist = (new ValueBuilder()).withDescriptor("Distance").withValue(Integer.valueOf(6)).withRange(Integer.valueOf(0), Integer.valueOf(15)).register(this);
    Value debug = (new ValueBuilder()).withDescriptor("Debug").withValue(Boolean.valueOf(false)).register(this);

    public FakePlayer() {
        super("Fake Player", Feature.Category.Misc);
    }

    public void onEnable() {
        super.onEnable();
        if (!NullUtils.nullCheck()) {
            GameProfile profile = new GameProfile(UUID.fromString("2da1acb3-1a8c-471f-a877-43f13cf37e6a"), this.playerName);

            this.fakePlayer = new EntityOtherPlayerMP(FakePlayer.mc.world, profile);
            if (((String) this.moveMode.getValue()).equals("Facing")) {
                double[] forward = PlayerUtils.radians();
                double x = forward[0] * ((Number) this.dist.getValue()).doubleValue();
                double z = forward[1] * ((Number) this.dist.getValue()).doubleValue();

                this.fakePlayer.setPosition(FakePlayer.mc.player.posX + x, FakePlayer.mc.player.posY, FakePlayer.mc.player.posZ + z);
            } else {
                this.fakePlayer.copyLocationAndAnglesFrom(FakePlayer.mc.player);
            }

            this.fakePlayer.setHealth(FakePlayer.mc.player.getHealth() + FakePlayer.mc.player.getAbsorptionAmount());
            this.fakePlayer.inventory = FakePlayer.mc.player.inventory;
            this.fakePlayer.stepHeight = 2.0F;
            FakePlayer.mc.world.spawnEntity(this.fakePlayer);
        }
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck() && this.fakePlayer != null) {
            if (!((String) this.moveMode.getValue()).equals("None")) {
                if (((String) this.moveMode.getValue()).equals("Looking") && FakePlayer.mc.objectMouseOver != null && FakePlayer.mc.objectMouseOver.typeOfHit == Type.BLOCK) {
                    this.fakePlayer.setPositionAndRotation((double) FakePlayer.mc.objectMouseOver.getBlockPos().getX() + 0.5D, (double) this.getNearestGround(FakePlayer.mc.objectMouseOver.getBlockPos()).getY(), (double) FakePlayer.mc.objectMouseOver.getBlockPos().getZ() + 0.5D, FakePlayer.mc.player.rotationYaw, FakePlayer.mc.player.rotationPitch);
                }

                if (((String) this.moveMode.getValue()).equals("Facing")) {
                    this.fakePlayer.moveForward = FakePlayer.mc.player.moveForward;
                    this.fakePlayer.moveStrafing = FakePlayer.mc.player.moveStrafing;
                    this.fakePlayer.stepHeight = FakePlayer.mc.player.stepHeight;
                    this.fakePlayer.rotationYaw = FakePlayer.mc.player.rotationYaw;
                    this.fakePlayer.setSprinting(FakePlayer.mc.player.isSprinting());
                    this.fakePlayer.motionX = FakePlayer.mc.player.motionX;
                    this.fakePlayer.motionY = FakePlayer.mc.player.motionY;
                    this.fakePlayer.motionZ = FakePlayer.mc.player.motionZ;
                    this.fakePlayer.move(MoverType.SELF, this.fakePlayer.motionX, this.fakePlayer.motionY, this.fakePlayer.motionZ);
                }

                if (((Boolean) this.debug.getValue()).booleanValue()) {
                    ChatUtils.sendMessage(new ChatMessage("Moved fake player", true, this.fakePlayer.getEntityId() * 10));
                }
            }

        }
    }

    BlockPos getNearestGround(BlockPos pos) {
        for (int i = 0; i <= 10; ++i) {
            if (FakePlayer.mc.world.getBlockState(pos).getMaterial().isSolid()) {
                pos = pos.add(0, 1, 0);
            }

            if (FakePlayer.mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial().isSolid()) {
                pos = pos.add(0, 2, 0);
            }
        }

        return pos;
    }

    public void onDisable() {
        super.onDisable();
        if (this.fakePlayer != null) {
            FakePlayer.mc.world.removeEntityFromWorld(this.fakePlayer.getEntityId());
            this.fakePlayer = null;
        }

    }
}
