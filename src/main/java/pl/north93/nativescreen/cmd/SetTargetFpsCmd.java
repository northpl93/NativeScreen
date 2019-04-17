package pl.north93.nativescreen.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.north93.nativescreen.MainPlugin;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.renderer.IMapManager;

public class SetTargetFpsCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args)
    {
        final IMapManager mapManager = MainPlugin.getInstance().getMapManager();
        final Player player = (Player) commandSender;

        if (args.length != 2)
        {
            player.sendMessage(ChatColor.RED + "/settargetfps boardName targetFps");
            return true;
        }

        final String boardName = args[0];
        final int targetFps;
        try
        {
            targetFps = Integer.parseInt(args[1]);
        }
        catch (final NumberFormatException e)
        {
            player.sendMessage(ChatColor.RED + "Invalid targetFps format");
            return true;
        }

        final IBoard board = mapManager.getBoardByName(boardName);
        if (board == null)
        {
            player.sendMessage(ChatColor.RED + "Not found board with specified name");
            return true;
        }

        board.getRendererThread().setTargetFps(targetFps);
        player.sendMessage(ChatColor.GREEN + "Changed target fps to " + targetFps);

        return true;
    }
}
