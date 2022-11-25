package eu.time.discordbot.discord.features;

import eu.time.discordbot.discord.util.Downloader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AntiTikTok extends AbstractFeature<MessageReceivedEvent> {
    @Override
    public void execute(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String contentRaw = message.getContentRaw();

        if (contentRaw.contains(".tiktok.com/")) {
            try (FileUpload fileUpload = FileUpload.fromData(getShameFile(), "Shame.gif")) {
                event.getChannel().sendMessage(String.format("Shame %s for posting TikTok", event.getAuthor().getAsMention())).addFiles(fileUpload).queue();
            } catch (IOException e) {
                event.getChannel().sendMessage(String.format("Shame %s for posting TikTok", event.getAuthor().getAsMention())).queue();
            }
//            message.delete().queue();
        }
    }

    private File getShameFile() throws IOException {
        URL url = new URL("https://c.tenor.com/rwZNrZ2V2MoAAAAC/shame-go-t.gif");

        File shameFile = new File("shame.gif");

        if (!shameFile.exists()) {
            Downloader.downloadFile(url, shameFile.getName());
        }

        return shameFile;
    }
}
