package pl.north93.nativescreen.input.impl;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MathHelper;
import net.minecraft.server.v1_12_R1.NetworkManager;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketListener;
import net.minecraft.server.v1_12_R1.PacketPlayInArmAnimation;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying.PacketPlayInLook;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying.PacketPlayInPositionLook;
import net.minecraft.server.v1_12_R1.PacketPlayInKeepAlive;
import net.minecraft.server.v1_12_R1.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_12_R1.PlayerConnection;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.log4j.Log4j2;
import pl.north93.nativescreen.input.Key;

@Log4j2
/*default*/ class NorthChannelHandler extends ChannelDuplexHandler
{
    private final MinecraftInputGrabber    inputGrabber;
    private final NavigationControllerImpl controller;
    private final NetworkManager           networkManager;

    public NorthChannelHandler(final MinecraftInputGrabber inputGrabber, final NavigationControllerImpl controller, final NetworkManager networkManager)
    {
        this.inputGrabber = inputGrabber;
        this.controller = controller;
        this.networkManager = networkManager;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        if ( !( msg instanceof Packet) || ! this.isPlayerGrabbed())
        {
            // msg is not Packet or player isnt grabbed
            super.channelRead(ctx, msg);
            return;
        }

        final Packet<?> packet = (Packet<?>) msg;
        if (packet instanceof PacketPlayInSteerVehicle)
        {
            final PacketPlayInSteerVehicle steerVehicle = (PacketPlayInSteerVehicle) packet;
            this.handleSteerVehicle(steerVehicle);

            return; // catch this packet and do not execute it in minecraft engine
        }
        else if (packet instanceof PacketPlayInFlying)
        {
            final PacketPlayInFlying flying = (PacketPlayInFlying) packet;
            this.handleFlying(flying);

            return; // catch this packet and do not execute it in minecraft engine
        }
        else if (packet instanceof PacketPlayInArmAnimation)
        {
            // packet is incoming when player left click air
            this.controller.signalKeyHit(Key.MOUSE_LEFT);

            return; // catch this packet and do not execute it in minecraft engine
        }
        else if (packet instanceof PacketPlayInKeepAlive)
        {
            //Bukkit.broadcastMessage("Incoming keep alive!");
        }

        //Bukkit.broadcastMessage("Channel read: " + packet);

        super.channelRead(ctx, msg);
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

    private void handleFlying(final PacketPlayInFlying packet)
    {
        final EntityPlayer entityPlayer = this.getMinecraftPlayer();
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

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        log.info("Channel active for {}", ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception
    {
        log.info("Channel inactive for {}", ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception
    {
        if ( !( msg instanceof Packet ) )
        {
            super.write(ctx, msg, promise);
            return;
        }

        final Packet<?> packet = (Packet<?>) msg;
        super.write(ctx, msg, promise);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception
    {
        log.error("Exception caught when processing channel pipeline", cause);
        super.exceptionCaught(ctx, cause);
    }

    private boolean isPlayerGrabbed()
    {
        final EntityPlayer minecraftPlayer = this.getMinecraftPlayer();
        if (minecraftPlayer == null)
        {
            return false;
        }

        return this.inputGrabber.isPlayerGrabbed(minecraftPlayer.getBukkitEntity());
    }

    private EntityPlayer getMinecraftPlayer()
    {
        final PacketListener packetListener = this.networkManager.i(); // should be getPacketListener()
        if ( packetListener instanceof PlayerConnection)
        {
            return ((PlayerConnection) packetListener).player;
        }

        return null;
    }
}
