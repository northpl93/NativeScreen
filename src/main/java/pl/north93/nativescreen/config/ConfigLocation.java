package pl.north93.nativescreen.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ConfigLocation
{
    private int x;
    private int y;
    private int z;
    private String world;

    public Location toBukkit()
    {
        final World bukkitWorld = Bukkit.getWorld(this.world);
        return new Location(bukkitWorld, this.x, this.y, this.z);
    }
}
