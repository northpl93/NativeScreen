package pl.north93.nativescreen.video;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import pl.north93.nativescreen.renderer.IBoard;

public class YouTubeDownloadTask implements Runnable
{
    private final CommandSender source;
    private final String videoUrl;
    private final IBoard board;

    public YouTubeDownloadTask(final CommandSender source, final String videoUrl, final IBoard board)
    {
        this.source = source;
        this.videoUrl = videoUrl;
        this.board = board;
    }

    @Override
    public void run()
    {
        final String videoUuid = UUID.nameUUIDFromBytes(this.videoUrl.getBytes(StandardCharsets.UTF_8)).toString();

        final File videoCache = new File("videoCache");
        videoCache.mkdir();

        try
        {
            final File videoFile = new File(videoCache, videoUuid);
            if (videoFile.exists())
            {
                this.loadVideo(videoFile);
                return;
            }

            final YoutubeDLRequest request = new YoutubeDLRequest(this.videoUrl, videoCache.getAbsolutePath());
            request.setOption("output", videoUuid);
            request.setOption("format", "bestvideo[height<=1080]/best");

            this.source.sendMessage(ChatColor.GREEN + "Downloading: " + this.videoUrl);

            final YoutubeDLResponse response = YoutubeDL.execute(request);
            this.loadVideo(videoFile);
        }
        catch (final Exception e)
        {
            this.source.sendMessage(ChatColor.RED + "Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadVideo(final File videoFile) throws Exception
    {
        final String videoPath = videoFile.getAbsolutePath();
        this.source.sendMessage(ChatColor.GREEN + "Loading video: " + videoPath);

        this.board.setRenderer(new VideoRenderer(videoPath));
    }
}
