package pl.north93.nativescreen.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;

import pl.north93.nativescreen.MainPlugin;
import pl.north93.nativescreen.renderer.IBoard;
import pl.north93.nativescreen.winapi.renderer.NativeWindowRenderer;

public class SetNativeWindowRendererCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args)
    {
        final MainPlugin mainPlugin = MainPlugin.getInstance();
        final Player player = (Player) commandSender;

        if (args.length <= 1)
        {
            player.sendMessage(ChatColor.RED + "/setnativewindowrenderer boardName windowClassName");
            return true;
        }

        final String boardName = args[0];
        final String windowClassName = StringUtils.join(args, ' ', 1, args.length);

        final IBoard board = mainPlugin.getMapManager().getBoardByName(boardName);
        if (board == null)
        {
            player.sendMessage(ChatColor.RED + "Not found board with specified name");
            return true;
        }

        // SunAwtFrame, LWJGL, The Witcher, TaskManagerWindow
        board.setRenderer(new NativeWindowRenderer(windowClassName));
        player.sendMessage(ChatColor.GREEN + "Changed renderer to NativeWindowRenderer with className " + windowClassName);

        return true;
    }
}
