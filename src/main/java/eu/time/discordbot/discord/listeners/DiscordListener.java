package eu.time.discordbot.discord.listeners;

import eu.time.discordbot.discord.features.Feature;
import eu.time.discordbot.discord.util.MessageHandler;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class DiscordListener extends ListenerAdapter {
    protected final MessageHandler messageHandler = MessageHandler.getInstance();

    protected final List<Feature> features;

    public DiscordListener() {
        this.features = new ArrayList<>();
    }

    public DiscordListener(List<Feature> features) {
        this.features = features;
    }

    protected void addFeature(Feature feature) {
        this.features.add(feature);
    }

    protected void addFeatures(List<Feature> features) {
        this.features.addAll(features);
    }

    protected void executeFeatures(Event event) {
        this.features.forEach(feature -> feature.execute(event));
    }
}
