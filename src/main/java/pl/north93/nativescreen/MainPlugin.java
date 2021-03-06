package pl.north93.nativescreen;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.north93.nativescreen.fullscreen.SetFullScreenRendererCmd;
import pl.north93.nativescreen.fullscreen.SetRandomColorRendererCmd;
import pl.north93.nativescreen.video.cmd.SetVideoRendererCmd;
import pl.north93.nativescreen.winapi.renderer.SetNativeWindowRendererCmd;
import pl.north93.nativescreen.renderer.cmd.SetTargetFpsCmd;
import pl.north93.nativescreen.input.INavigationController;
import pl.north93.nativescreen.gui.DebugMouseMovementRenderer;
import pl.north93.nativescreen.input.helper.NavigationOutputHandlerDebugger;
import pl.north93.nativescreen.input.helper.NavigationOutputHandlerRendererRedirect;
import pl.north93.nativescreen.input.impl.MinecraftInputGrabber;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapManager;
import pl.north93.nativescreen.renderer.impl.MapManagerImpl;

public class MainPlugin extends JavaPlugin
{
    private static MainPlugin INSTANCE;
    private MinecraftInputGrabber grabber;
    private IMapManager mapManager;

    @Override
    public void onEnable()
    {
        INSTANCE = this;

        this.grabber = new MinecraftInputGrabber();
        this.mapManager = new MapManagerImpl(this);
        Bukkit.getPluginManager().registerEvents(this.grabber, this);

        final World world = Bukkit.getWorld("world");
        final Location leftCorner = new Location(world, 86, 5, 471);
        final Location rightCorner = new Location(world, 72, 12, 471);

        final IBoard board = this.mapManager.createBoard("test1", leftCorner, rightCorner);
        board.getRendererThread().setTargetFps(25);
        board.getRendererThread().start();

        final INavigationController navigationController = this.grabber.getNavigationController();
        navigationController.registerNavigationHandler(new NavigationOutputHandlerDebugger());
        navigationController.registerNavigationHandler(new NavigationOutputHandlerRendererRedirect(board)); // pass all input events to renderer of our board

        //board.setRenderer(new NativeWindowRenderer("sth"));
        //board.setRenderer(new TestFullScreenRenderer());
        //board.setRenderer(new ListWindowsRenderer());

        this.getCommand("setvideorenderer").setExecutor(new SetVideoRendererCmd());
        this.getCommand("setnativewindowrenderer").setExecutor(new SetNativeWindowRendererCmd());
        this.getCommand("setfullscreenrenderer").setExecutor(new SetFullScreenRendererCmd());
        this.getCommand("setrandomcolorrenderer").setExecutor(new SetRandomColorRendererCmd());
        this.getCommand("settargetfps").setExecutor(new SetTargetFpsCmd());
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
            // all input will be redirected by NavigationOutputHandlerRendererRedirect, so we
            // don't have to register DebugMouseMovementRenderer in INavigationController
            board.setRenderer(new DebugMouseMovementRenderer());
        }


        return true;
    }
}
