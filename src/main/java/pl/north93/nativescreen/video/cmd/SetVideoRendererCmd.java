package pl.north93.nativescreen.video.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.north93.nativescreen.MainPlugin;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.video.TestVideoRenderer;

public class SetVideoRendererCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args)
    {
        final MainPlugin mainPlugin = MainPlugin.getInstance();
        final Player player = (Player) commandSender;

        if (args.length != 1)
        {
            player.sendMessage(ChatColor.RED + "/setvideorenderer boardName");
            return true;
        }

        final String boardName = args[0];

        final IBoard board = mainPlugin.getMapManager().getBoardByName(boardName);
        if (board == null)
        {
            player.sendMessage(ChatColor.RED + "Not found board with specified name");
            return true;
        }

        try
        {
            board.setRenderer(new TestVideoRenderer());

            player.sendMessage(ChatColor.GREEN + "Changed renderer to TestVideoRenderer");
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }
}
