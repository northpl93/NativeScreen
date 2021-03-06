package pl.north93.nativescreen.fullscreen;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.north93.nativescreen.MainPlugin;
import pl.north93.nativescreen.renderer.IBoard;

public class SetRandomColorRendererCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        final MainPlugin mainPlugin = MainPlugin.getInstance();
        final Player player = (Player) sender;

        if (args.length != 1)
        {
            player.sendMessage(ChatColor.RED + "/setrandomcolorrenderer boardName");
            return true;
        }

        final String boardName = args[0];

        final IBoard board = mainPlugin.getMapManager().getBoardByName(boardName);
        if (board == null)
        {
            player.sendMessage(ChatColor.RED + "Not found board with specified name");
            return true;
        }

        board.setRenderer(new RandomColorRenderer());
        player.sendMessage(ChatColor.GREEN + "Changed renderer to RandomColorRenderer");

        return true;
    }
}
