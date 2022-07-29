package pl.north93.nativescreen.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.metadata.EntityMetadataStore;
import org.bukkit.craftbukkit.v1_12_R1.metadata.PlayerMetadataStore;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import org.apache.commons.lang3.StringUtils;

import pl.north93.nativescreen.MainPlugin;

/**
 * Various utils related to {@link org.bukkit.metadata.Metadatable}.
 */
public final class MetadataUtils
{
    private static final Field internalMap;

    static
    {
        try
        {
            internalMap = MetadataStoreBase.class.getDeclaredField("metadataMap");
        }
        catch (final NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getMetadata(final Metadatable metadatable, final String name)
    {
        final List<MetadataValue> metadata = metadatable.getMetadata(name);
        if (metadata.isEmpty())
        {
            return null;
        }

        return (T) metadata.get(0).value();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getMetadataOrCompute(final Metadatable metadatable, final String name, final Supplier<T> defaultValue)
    {
        final List<MetadataValue> metadata = metadatable.getMetadata(name);
        if (metadata.isEmpty())
        {
            final T newValue = defaultValue.get();
            setMetadata(metadatable, name, newValue);

            return newValue;
        }

        return (T) metadata.get(0).value();
    }

    public static void setMetadata(final Metadatable metadatable, final String name, final Object value)
    {
        metadatable.setMetadata(name, new FixedMetadataValue(MainPlugin.getInstance(), value));
    }

    public static void deleteMetadata(final Metadatable metadatable, final String name)
    {
        metadatable.removeMetadata(name, MainPlugin.getInstance());
    }

    public static void removeEntityMetadata(final UUID entityId)
    {
        final String keyStart = entityId + ":";
        removeMetadata(getEntityMetadata(), keyStart);
    }

    public static void removePlayerMetadata(final Player player)
    {
        final String keyStart = player.getName().toLowerCase(Locale.ROOT) + ":";
        removeMetadata(getPlayerMetadata(), keyStart);
    }

    private static EntityMetadataStore getEntityMetadata()
    {
        final CraftServer server = (CraftServer) Bukkit.getServer();
        return server.getEntityMetadata();
    }

    private static PlayerMetadataStore getPlayerMetadata()
    {
        final CraftServer server = (CraftServer) Bukkit.getServer();
        return server.getPlayerMetadata();
    }

    @SuppressWarnings("unchecked")
    private static void removeMetadata(final MetadataStoreBase<?> metadataStoreBase, final String keyStart)
    {
        final Map<String, Map<Plugin, MetadataValue>> map;
        try
        {
            map = (Map<String, Map<Plugin, MetadataValue>>) internalMap.get(metadataStoreBase);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        assert map != null;

        map.entrySet().removeIf(entry -> StringUtils.startsWith(entry.getKey(), keyStart));
    }
}
