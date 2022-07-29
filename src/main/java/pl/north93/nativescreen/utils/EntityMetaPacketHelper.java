package pl.north93.nativescreen.utils;

import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.Vector3f;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.ToString;

/**
 * Our implementation of PacketPlayOutEntityMetadata, so we don't
 * have to mess with mojang's DataWatchers.
 */
@ToString
public class EntityMetaPacketHelper
{
    private final ByteBuf buffer;
    private final PacketDataSerializer pds;

    /**
     * Creates a new instance of a packet.
     * It'll write the packet ID and entityId into the internal buffer.
     *
     * @param entityId Entity which should be affected by this packet.
     * @see Entity#getEntityId()
     */
    public EntityMetaPacketHelper(final int entityId)
    {
        this.buffer = UnpooledByteBufAllocator.DEFAULT.buffer(32);
        this.pds = new PacketDataSerializer(this.buffer);

        this.pds.d(0x3C); // writeVarInt PacketPlayOutEntityMetadata id REMEMBER TO SYNC IT WITH MOJANG'S ID
        this.pds.d(entityId); // writeVarInt
    }

    /**
     * Adds a new metadata into this packet.
     * <a href="http://wiki.vg/Entities#Entity_Metadata_Format">Here is a list of all entity's metadatas</a>
     *
     * @param metaId ID of the metadata (Index on wiki.vg)
     * @param metaType Type of the metadata (Type on wiki.vg)
     * @param value New value of the metadata
     */
    public void addMeta(final int metaId, final MetaType metaType, final Object value)
    {
        this.pds.writeByte(metaId);
        metaType.write(this.pds, value);
    }

    /**
     * Writes "packet end" marker into an internal buffer, and returns the buffer.
     * Remember to release the buffer obtained from this method.
     * Calling this method more than once will break the packet.
     *
     * @return Buffer containing the written packet.
     * @see ByteBuf#release()
     */
    public ByteBuf complete()
    {
        this.pds.writeByte(0xff);
        return this.buffer;
    }

    public enum MetaType
    {
        VAR_INT
                {
                    @Override
                    void write(final PacketDataSerializer serializer, final Object object)
                    {
                        serializer.d(1);
                        serializer.d((Integer) object);
                    }
                },
        STRING
                {
                    @Override
                    void write(final PacketDataSerializer serializer, final Object object)
                    {
                        serializer.d(3);
                        serializer.a((String) object);
                    }
                },
        SLOT
                {
                    @Override
                    void write(final PacketDataSerializer serializer, final Object object)
                    {
                        serializer.d(5);

                        final ItemStack bukkitStack = (ItemStack) object;
                        // a(n.m.s.ItemStack)
                        serializer.a(CraftItemStack.asNMSCopy(bukkitStack));
                    }
                },
        BOOLEAN
                {
                    @Override
                    void write(final PacketDataSerializer serializer, final Object object)
                    {
                        serializer.d(6);
                        serializer.writeBoolean((Boolean) object);
                    }
                },
        VECTOR
                {
                    @Override
                    void write(final PacketDataSerializer serializer, final Object object)
                    {
                        final Vector3f vector = (Vector3f) object;

                        serializer.d(7);
                        serializer.writeFloat(vector.getX());
                        serializer.writeFloat(vector.getY());
                        serializer.writeFloat(vector.getZ());
                    }
                };


        abstract void write(PacketDataSerializer serializer, Object object);
    }
}
