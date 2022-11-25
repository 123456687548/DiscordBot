package eu.time.discordbot.eis;

import data.Product;
import eis.EisQuery;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

public class EisHandler {
    public static final EisQuery EIS = new EisQuery();

    private EisHandler() {
    }

    public static List<MessageEmbed> createFreeDiscordEmbeds() {
        List<Product> freeProducts = EIS.getFreeProducts();
        List<MessageEmbed> embeds = new ArrayList<>();

        for (Product freeProduct : freeProducts) {
            embeds.add(EisHandler.createDiscordMessage(freeProduct));
        }

        return embeds;
    }

    public static MessageEmbed createDiscordMessage(Product product) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setImage(product.imageUrl());
        embed.addField(product.productName(), product.productCategory(), false);
        embed.addField(product.price().toString(), product.url(), false);

        return embed.build();
    }
}
