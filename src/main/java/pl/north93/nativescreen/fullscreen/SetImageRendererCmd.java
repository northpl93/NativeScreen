package pl.north93.nativescreen.fullscreen;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.north93.nativescreen.MainPlugin;
import pl.north93.nativescreen.renderer.IBoard;

public class SetImageRendererCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args)
    {
        final MainPlugin mainPlugin = MainPlugin.getInstance();
        final Player player = (Player) commandSender;

        if (args.length != 2)
        {
            player.sendMessage(ChatColor.RED + "/setimagerenderer boardName imgPath");
            return true;
        }

        final String boardName = args[0];

        final IBoard board = mainPlugin.getMapManager().getBoardByName(boardName);
        if (board == null)
        {
            player.sendMessage(ChatColor.RED + "Not found board with specified name");
            return true;
        }

        final File imageFile = new File(args[1]);
        if (! imageFile.exists())
        {
            player.sendMessage(ChatColor.RED + "Not found image " + imageFile.getAbsolutePath());
            return true;
        }

        board.setRenderer(new ImageRenderer(this.readImageAndScale(imageFile, board)));
        player.sendMessage(ChatColor.GREEN + "Changed renderer to ImageRenderer");

        return true;
    }

    private BufferedImage readImageAndScale(final File imageFile, final IBoard board)
    {
        try
        {
            final BufferedImage originalImage = ImageIO.read(imageFile.toURI().toURL());

            final BufferedImage scaledImage = new BufferedImage(board.getWidth() * 128, board.getHeight() * 128, originalImage.getType());
            final Graphics2D g2d = scaledImage.createGraphics();

            final Image tmp = originalImage.getScaledInstance(board.getWidth() * 128, board.getHeight() * 128, Image.SCALE_SMOOTH);
            g2d.drawImage(tmp, 0, 0, null);

            g2d.dispose();

            return scaledImage;
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Failed to load image", e);
        }
    }
}
