package pl.north93.nativescreen.renderer.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.north93.nativescreen.MainPlugin;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapManager;

public class SetPerformanceCountersCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args)
    {
        final IMapManager mapManager = MainPlugin.getInstance().getMapManager();
        final Player player = (Player) commandSender;

        if (args.length != 2)
        {
            player.sendMessage(ChatColor.RED + "/setperformancecounters boardName enabled");
            return true;
        }

        final IBoard board = mapManager.getBoardByName(args[0]);
        if (board == null)
        {
            player.sendMessage(ChatColor.RED + "Not found board with specified name");
            return true;
        }

        final boolean enabled = Boolean.parseBoolean(args[1]);

        board.getRendererThread().setPerformanceCountersEnabled(enabled);
        player.sendMessage(ChatColor.GREEN + "Changed performance counters to " + enabled);

        return true;
    }
}
