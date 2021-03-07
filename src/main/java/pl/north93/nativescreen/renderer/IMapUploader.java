package pl.north93.nativescreen.renderer;

import org.bukkit.entity.Player;

/**
 * Map uploader is responsible for delivering map content to client.
 */
public interface IMapUploader
{
    void uploadMapToPlayer(Player player, int mapId, IMapCanvasDirectAccess newCanvas);
}
