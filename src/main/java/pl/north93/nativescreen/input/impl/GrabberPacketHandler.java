package pl.north93.nativescreen.input.impl;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MathHelper;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayInArmAnimation;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying.PacketPlayInLook;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying.PacketPlayInPositionLook;
import net.minecraft.server.v1_12_R1.PacketPlayInKeepAlive;
import net.minecraft.server.v1_12_R1.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntity.PacketPlayOutEntityLook;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pl.north93.nativescreen.input.Key;
import pl.north93.nmsutils.protocol.IPacketHandler;

@Log4j2
@AllArgsConstructor
class GrabberPacketHandler implements IPacketHandler
{
    private final MinecraftInputGrabber inputGrabber;
    private final NavigationControllerImpl controller;

    @Override
    public boolean onPacketSend(final Player player, final Object packet)
    {
        return true;
    }

    @Override
    public boolean onPacketReceive(final Player player, final Object msg)
    {
        if ( !( msg instanceof Packet) || player == null || ! this.inputGrabber.isPlayerGrabbed(player))
        {
            // msg is not Packet or player isnt grabbed
            return true;
        }

        final Packet<?> packet = (Packet<?>) msg;
        if (packet instanceof PacketPlayInSteerVehicle)
        {
            final PacketPlayInSteerVehicle steerVehicle = (PacketPlayInSteerVehicle) packet;
            this.handleSteerVehicle(steerVehicle);

            return false; // catch this packet and do not execute it in minecraft engine
        }
        else if (packet instanceof PacketPlayInFlying)
        {
            final PacketPlayInFlying flying = (PacketPlayInFlying) packet;
            this.handleFlying(player, flying);

            return false; // catch this packet and do not execute it in minecraft engine
        }
        else if (packet instanceof PacketPlayInArmAnimation)
        {
            // packet is incoming when player left click air
            this.controller.signalKeyHit(Key.MOUSE_LEFT);

            return false; // catch this packet and do not execute it in minecraft engine
        }
        else if (packet instanceof PacketPlayInKeepAlive)
        {
            //Bukkit.broadcastMessage("Incoming keep alive!");
        }

        //Bukkit.broadcastMessage("Channel read: " + packet);

        return true;
    }

    private void handleSteerVehicle(final PacketPlayInSteerVehicle packet)
    {
        if (packet.a() > 0) // A pressed
            this.controller.signalKeyDown(Key.NAVIGATION_A);
        else
            this.controller.signalKeyUp(Key.NAVIGATION_A);

        if (packet.a() < 0) // D pressed
            this.controller.signalKeyDown(Key.NAVIGATION_D);
        else
            this.controller.signalKeyUp(Key.NAVIGATION_D);

        if (packet.b() > 0) // W pressed
            this.controller.signalKeyDown(Key.NAVIGATION_W);
        else
            this.controller.signalKeyUp(Key.NAVIGATION_W);

        if (packet.b() < 0) // S pressed
            this.controller.signalKeyDown(Key.NAVIGATION_S);
        else
            this.controller.signalKeyUp(Key.NAVIGATION_S);

        if (packet.c())
            this.controller.signalKeyHit(Key.SPACE);

        if (packet.d())
            this.controller.signalKeyHit(Key.SHIFT);
        //Bukkit.broadcastMessage("sideways: " + packet.a() + " forward: " + packet.b());
    }

    private void handleFlying(final Player player, final PacketPlayInFlying packet)
    {
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        if (entityPlayer == null)
        {
            return;
        }

        if (packet instanceof PacketPlayInLook || packet instanceof PacketPlayInPositionLook)
        {
            final float yaw = packet.a(0f);
            final float pitch = packet.b(0f);

            final float deltaYaw = entityPlayer.yaw - yaw;
            final float deltaPitch = entityPlayer.pitch - pitch;

            this.controller.signalMouseMovement(deltaYaw, deltaPitch);
        }

        final byte i = (byte) MathHelper.d(entityPlayer.yaw * 256.0F / 360.0F);
        final byte j = (byte) MathHelper.d(entityPlayer.pitch * 256.0F / 360.0F);
        final PacketPlayOutEntityLook entityLook = new PacketPlayOutEntityLook(entityPlayer.getId(), i, j, true);

        entityPlayer.playerConnection.networkManager.channel.writeAndFlush(entityLook);
    }
}
