package pl.north93.nativescreen.renderer.impl;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.north93.nativescreen.renderer.IMapCanvasDirectAccess;
import pl.north93.nativescreen.renderer.IMapUploader;

/**
 * Klasa przechowujaca stan map u danego gracza.
 * <p>
 * Dane sa przechowywane jako WeakHashMap wiec nie ma potrzeby
 * recznego zwalniania identyfikatorow. Zapewnia to sensowne
 * cachowanie identyfikatorow.
 */
@ToString
class PlayerMapData
{
    private final Player player;
    private final Map<MapImpl, MapContainer> mapping;
    private int latestId;

    public PlayerMapData(final Player player)
    {
        this.player = player;
        this.mapping = new WeakHashMap<>();
    }

    /**
     * Metoda zwracajaca ID przypisane do tej mapy dla tego gracza.
     *
     * @param map Mapa ktorej powiazane ID sprawdzamy.
     * @return ID powiazane z ta mapa.
     */
    public int getMapId(final MapImpl map)
    {
        final MapContainer container = this.getOrComputeContainer(map);
        return container.getId();
    }

    public boolean isMapVisible(final MapImpl map)
    {
        return map.isTrackedBy(this.player);
    }

    public boolean isClientCanvasMatchesServer(final MapImpl map)
    {
        final MapContainer container = this.getOrComputeContainer(map);

        final IMapCanvasDirectAccess serverCanvas = container.getServerCanvas();
        if (serverCanvas == null)
        {
            return true; // na pewno?
        }

        final IMapCanvasDirectAccess clientCanvas = container.getClientCanvas();
        if (clientCanvas == null)
        {
            return false;
        }

        // porownujemy canvas widoczny u klienta i na serwerze
        return serverCanvas.equals(clientCanvas);
    }

    public void uploadServerCanvasToClient(final IMapUploader mapUploader, final MapImpl map)
    {
        final MapContainer container = this.getOrComputeContainer(map);

        mapUploader.uploadMapToPlayer(this.player, container.getId(), container.getServerCanvas());
        container.setClientCanvas(container.getServerCanvas());
    }

    public void resetAllClientSideCanvases()
    {
        synchronized (this.mapping)
        {
            for (final MapContainer container : this.mapping.values())
            {
                container.setClientCanvas(null);
            }
        }
    }

    public MapContainer getOrComputeContainer(final MapImpl map)
    {
        return this.mapping.computeIfAbsent(map, this::generateNewContainer);
    }

    private MapContainer generateNewContainer(final MapImpl map)
    {
        synchronized (this.mapping)
        {
            final Collection<Integer> values = this.mapping.values().stream().map(MapContainer::getId).collect(Collectors.toSet());
            do
            {
                this.latestId = (++ this.latestId) % Short.MAX_VALUE;
            } while (values.contains(this.latestId));

            return new MapContainer(this.latestId);
        }
    }
}

@Getter
@Setter
@ToString
final class MapContainer
{
    private final int id;
    private IMapCanvasDirectAccess serverCanvas;
    private IMapCanvasDirectAccess clientCanvas;

    public MapContainer(final int id)
    {
        this.id = id;
    }
}
