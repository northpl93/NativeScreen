package pl.north93.nativescreen.config;

import org.bukkit.configuration.ConfigurationSection;

import lombok.Data;

@Data
public class BoardConfig
{
    private final String name;
    private final String world;
    private final ConfigLocation leftCorner;
    private final ConfigLocation rightCorner;

    public BoardConfig(final String name, final ConfigurationSection config)
    {
        this.name = name;
        this.world = config.getString("world");
        this.leftCorner = new ConfigLocation(config.getConfigurationSection("leftCorner"));
        this.rightCorner = new ConfigLocation(config.getConfigurationSection("rightCorner"));
    }
}
