package pl.north93.nativescreen.config;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import lombok.Data;

@Data
public class ConfigLocation
{
    private final int x;
    private final int y;
    private final int z;

    public ConfigLocation(final ConfigurationSection config)
    {
        this.x = config.getInt("x");
        this.y = config.getInt("y");
        this.z = config.getInt("z");
    }

    public Location toBukkit(final World world)
    {
        return new Location(world, this.x, this.y, this.z);
    }
}
