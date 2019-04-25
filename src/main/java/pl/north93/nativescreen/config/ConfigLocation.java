package pl.north93.nativescreen.config;

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

    public Location toBukkit(final World world)
    {
        return new Location(world, this.x, this.y, this.z);
    }
}
