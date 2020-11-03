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
 * Klasa pomocnicza sluzaca do recznego wysylania danych
 * zawartych w pakiecie PacketPlayOutEntityMetadata.
 */
@ToString
public class EntityMetaPacketHelper
{
    private final ByteBuf              buffer;
    private final PacketDataSerializer pds;

    /**
     * Tworzy nowa instancje klasy i wprowadza do bufora
     * dane ID entity.
     *
     * @param entityId ID entity ktorego dotyczy ten pakiet.
     * @see Entity#getEntityId()
     */
    public EntityMetaPacketHelper(final int entityId)
    {
        this.buffer = UnpooledByteBufAllocator.DEFAULT.buffer(32);
        this.pds = new PacketDataSerializer(this.buffer);

        this.pds.d(0x3C); // writeVarInt PacketPlayOutEntityMetadata id PAMIETAC ZEBY TU ZMIENIC PRZY AKTUSLIZACJI MINECRAFTA
        this.pds.d(entityId); // writeVarInt
    }

    /**
     * Dodaje nowe metadane do tej instancji.
     * Informacje o metadanych najlepiej czerpac z
     * http://wiki.vg/Entities#Entity_Metadata_Format
     *
     * @param metaId ID danej metadata (Index na wiki.vg)
     * @param metaType Typ metadany (Type na wiki.vg)
     * @param value Wartosc ktora ustalamy (zgodna z typem)
     */
    public void addMeta(final int metaId, final MetaType metaType, final Object value)
    {
        this.pds.writeByte(metaId);
        metaType.write(this.pds, value);
    }

    /**
     * Zapisuje na koncu bufora informacje o koncu metadanych
     * i zwraca instancje ByteBufa ktora nalezy pozniej
     * zamknac!
     * Nalezy wywolywac ta metode tylko raz, inaczej uzyskamy
     * uszkodzony pakiet.
     *
     * @return Bufor z gotowym pakietem.
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
