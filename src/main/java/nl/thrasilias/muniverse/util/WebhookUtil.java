package nl.thrasilias.muniverse.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import nl.thrasilias.muniverse.Logger;
import nl.thrasilias.muniverse.Main;
import nl.thrasilias.muniverse.bot.Bot;
import nl.thrasilias.muniverse.configuration.Config;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class WebhookUtil {
    private static String[] webhooks = new String[]{"#minecraft", "#console"};
    private static int currentWebhook = 0;
    private static String lastUsername;

    public static void sendMessageToDiscord(Bot bot, Player player, String message) {
        String[] groups = Main.getChat().getPlayerGroups(player);
        String parent;
        if (groups.length > 1) {
            parent = Main.getChat().getPrimaryGroup(player);
        } else {
            parent = Config.DEFAULT_RANK;
        }


        sendMessageToDiscord(bot, "https://minotar.net/helm/" + player.getName() + "/100.png", "["+parent+"] "+player.getDisplayName(), message);
    }

    public static void sendMessageToDiscord(Bot bot, String avatar, String username, String message) {
        if (username == null || !username.equals(lastUsername)) {
            currentWebhook = currentWebhook == 0 ? 1 : 0;
        }
        lastUsername = username;

        username = ColorTranslator.removeFormats(username);
        message = ColorTranslator.removeFormats(message);

        String hookName = webhooks[currentWebhook];
        TextChannel channel = bot.getClient().getTextChannelById(Config.CHAT_CHANNEL);

        if (channel == null) {
            Logger.warn("Could not send message to discord. Channel not found!");
            return;
        }

        Webhook webhook = channel.retrieveWebhooks().complete().stream()
                .filter(hook -> hook.getName().equals(hookName))
                .findFirst().orElse(null);
        if (webhook == null) {
            Logger.info("Could not find webhook! Creating a new one. (" + hookName + ")");
            webhook = channel.createWebhook(hookName).complete();
            if (webhook == null) {
                Logger.warn("Could not send message to discord. Webhook not found!");
                return;
            }
        }

        List<String> split = Arrays.asList(message.split(" "));
        for (String word : split) {
            if (!word.startsWith("@")) {
                continue; // must explicitly tag to mention
            }
            Guild guild = webhook.getGuild();
            guild.getMembers().forEach(member -> {
                if (member == guild.getSelfMember()) {
                    return; // don't tag self
                }
                String name = word.substring(1);
                if (name.equalsIgnoreCase(member.getEffectiveName()) ||
                        name.equalsIgnoreCase(member.getUser().getName())) {
                    split.set(split.indexOf(word), member.getAsMention());
                }
            });
        }
        message = String.join(" ", split);

        JDA jda = Main.getInstance().getBot().getClient();

        MessageBuilder msgBuilder = new MessageBuilder();
        msgBuilder.setContent(message);
        msgBuilder.stripMentions(jda, Message.MentionType.EVERYONE); // big no-no
        msgBuilder.stripMentions(jda, Message.MentionType.HERE); // just as bad
        msgBuilder.stripMentions(jda, Message.MentionType.ROLE); // annoying :S

        String content = msgBuilder.build().getContentRaw();
        if (content == null || content.isEmpty()) {
            return; // dont send empty messages
        }

        WebhookMessageBuilder webhookBuilder = new WebhookMessageBuilder();
        webhookBuilder.setContent(content);
        webhookBuilder.setUsername(username);
        webhookBuilder.setAvatarUrl(avatar);

        WebhookClient client = new WebhookClientBuilder(webhook.getUrl()).build();
        client.send(webhookBuilder.build());
        client.close();
    }
}