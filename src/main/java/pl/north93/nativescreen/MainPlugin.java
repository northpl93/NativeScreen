package pl.north93.nativescreen;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.north93.nativescreen.config.BoardConfig;
import pl.north93.nativescreen.config.PluginConfig;
import pl.north93.nativescreen.fullscreen.SetFullScreenRendererCmd;
import pl.north93.nativescreen.fullscreen.SetRandomColorRendererCmd;
import pl.north93.nativescreen.gui.DebugMouseMovementRenderer;
import pl.north93.nativescreen.input.INavigationController;
import pl.north93.nativescreen.input.helper.NavigationOutputHandlerDebugger;
import pl.north93.nativescreen.input.helper.NavigationOutputHandlerRendererRedirect;
import pl.north93.nativescreen.input.impl.MinecraftInputGrabber;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapManager;
import pl.north93.nativescreen.renderer.cmd.SetTargetFpsCmd;
import pl.north93.nativescreen.renderer.cmd.SetPerformanceCountersCmd;
import pl.north93.nativescreen.renderer.impl.MapManagerImpl;
import pl.north93.nativescreen.video.cmd.PlayVideoCmd;
import pl.north93.nativescreen.video.cmd.SetVideoRendererCmd;
import pl.north93.nativescreen.winapi.renderer.SetNativeWindowRendererCmd;
import pl.north93.nmsutils.NMSUtils;

public class MainPlugin extends JavaPlugin
{
    private static MainPlugin INSTANCE;
    private MinecraftInputGrabber grabber;
    private IMapManager mapManager;

    @Override
    public void onEnable()
    {
        INSTANCE = this;

        final NMSUtils nmsUtils = (NMSUtils) Bukkit.getPluginManager().getPlugin("NMSUtils");

        this.grabber = new MinecraftInputGrabber(nmsUtils.getProtocolManager());
        this.mapManager = new MapManagerImpl(this);
        Bukkit.getPluginManager().registerEvents(this.grabber, this);

        final PluginConfig config = new PluginConfig(this.getConfig());
        for (final BoardConfig boardConfig : config.getBoards())
        {
            final World world = Bukkit.getWorld(boardConfig.getWorld());
            final Location leftCorner = boardConfig.getLeftCorner().toBukkit(world);
            final Location rightCorner = boardConfig.getRightCorner().toBukkit(world);

            final IBoard board = this.mapManager.createBoard(boardConfig.getName(), leftCorner, rightCorner);
            board.getRendererThread().setTargetFps(25);
            board.getRendererThread().start();
        }

        this.getCommand("setvideorenderer").setExecutor(new SetVideoRendererCmd());
        this.getCommand("setnativewindowrenderer").setExecutor(new SetNativeWindowRendererCmd());
        this.getCommand("setfullscreenrenderer").setExecutor(new SetFullScreenRendererCmd());
        this.getCommand("setrandomcolorrenderer").setExecutor(new SetRandomColorRendererCmd());
        this.getCommand("settargetfps").setExecutor(new SetTargetFpsCmd());
        this.getCommand("setperformancecounters").setExecutor(new SetPerformanceCountersCmd());
        this.getCommand("playvideo").setExecutor(new PlayVideoCmd());
    }

    @Override
    public void onDisable()
    {
    }

    public static MainPlugin getInstance()
    {
        return INSTANCE;
    }

    public IMapManager getMapManager()
    {
        return this.mapManager;
    }

    public MinecraftInputGrabber getGrabber()
    {
        return this.grabber;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        if (command.getName().equalsIgnoreCase("test"))
        {
            final Player player = (Player) sender;

            if (this.grabber.isPlayerGrabbed(player))
            {
                this.grabber.unGrabPlayer(player);
                return true;
            }

            if (! this.grabber.grabPlayer(player))
            {
                player.sendMessage(ChatColor.RED + "Click on minecart first!");
            }

            final IBoard board = this.mapManager.getBoardByName("test1");

             final INavigationController navigationController = this.grabber.getNavigationController();
             navigationController.registerNavigationHandler(new NavigationOutputHandlerDebugger());
             // pass all input events to renderer of our board
             navigationController.registerNavigationHandler(new NavigationOutputHandlerRendererRedirect(board));

            // all input will be redirected by NavigationOutputHandlerRendererRedirect, so we
            // don't have to register DebugMouseMovementRenderer in INavigationController
            board.setRenderer(new DebugMouseMovementRenderer());
        }


        return true;
    }
}
