package pl.north93.nativescreen.video.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;

import pl.north93.nativescreen.MainPlugin;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.video.YouTubeDownloadTask;

public class PlayVideoCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args)
    {
        final MainPlugin mainPlugin = MainPlugin.getInstance();
        final Player player = (Player) commandSender;

        if (args.length < 2)
        {
            player.sendMessage(ChatColor.RED + "/playvideo boardName videoUrl");
            return true;
        }

        final String boardName = args[0];

        final IBoard board = mainPlugin.getMapManager().getBoardByName(boardName);
        if (board == null)
        {
            player.sendMessage(ChatColor.RED + "Not found board with specified name");
            return true;
        }

        final String videoUrl = StringUtils.join(args, ' ', 1, args.length);

        Bukkit.getScheduler().runTaskAsynchronously(mainPlugin, new YouTubeDownloadTask(player, videoUrl, board));

        return true;
    }
}
