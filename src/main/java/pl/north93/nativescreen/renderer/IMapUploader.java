package pl.north93.nativescreen.renderer;

import java.util.Collection;

import org.bukkit.entity.Player;

/**
 * Map uploader is responsible for delivering map content to clients.
 */
public interface IMapUploader
{
    void uploadMapToAudience(Collection<Player> audience, IMap map, IMapCanvasDirectAccess newCanvas);
}
