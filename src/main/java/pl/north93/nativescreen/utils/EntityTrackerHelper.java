package pl.north93.nativescreen.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityTrackerEntry;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;

public class EntityTrackerHelper
{
    private static final MethodHandle entity_field_tracker;
    static
    {
        try
        {
            final Field tracker = Entity.class.getDeclaredField("tracker");
            tracker.setAccessible(true);
            entity_field_tracker = MethodHandles.lookup().unreflectGetter(tracker).asType(MethodType.methodType(EntityTrackerEntry.class, Entity.class));
        }
        catch (final NoSuchFieldException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Konwertuje daną klasę Entity bukkitowego na Entity NMSowe.
     * Uwzględnia to, że Player może być NorthPlayerem.
     *
     * @param bukkitEntity Obiekt Bukkita reprezentujący Entity.
     * @return Obiekt NMS reprezentujący Entity.
     */
    public static Entity toNmsEntity(final org.bukkit.entity.Entity bukkitEntity)
    {
//        if ( bukkitEntity instanceof Player)
//        {
//            return INorthPlayer.asCraftPlayer((Player) bukkitEntity).getHandle();
//        }

        final CraftEntity craftEntity = (CraftEntity) bukkitEntity;
        return craftEntity.getHandle();
    }

    /**
     * Klasa EntityTrackerEntry sluzy do sledzenia danego entity przez liste
     * graczy bedacych w jego zasiegu. Zarzadza wysylaniem pakietow z
     * informacjami o danym entity.
     *
     * @param entity Entity z ktorego wyciagnac EntityTrackerEntry.
     * @return EntityTrackerEntry dla danego entity.
     */
    public static EntityTrackerEntry getTrackerEntry(final Entity entity)
    {
        try
        {
            return (EntityTrackerEntry) entity_field_tracker.invokeExact(entity);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }
}