package pl.north93.nativescreen.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import lombok.Data;

@Data
public class PluginConfig
{
    private final List<BoardConfig> boards;

    public PluginConfig(final ConfigurationSection config)
    {
        this.boards = new ArrayList<>();

        final ConfigurationSection boardsSection = config.getConfigurationSection("boards");
        for (final String boardName : boardsSection.getKeys(false))
        {
            final ConfigurationSection boardConfig = boardsSection.getConfigurationSection(boardName);
            this.boards.add(new BoardConfig(boardName, boardConfig));
        }
    }
}
