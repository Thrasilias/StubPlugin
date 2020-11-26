package nl.thrasilias.muniverse.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import java.util.concurrent.CompletableFuture;

public class ColorTranslator {
    public static String translateAlternateColorCodes(String txt){
        Matcher m = Pattern.compile("<(#[0-9a-fA-F]{6})>|&(#[0-9a-fA-F]{6})")
                .matcher(txt);
        while (m.find()){
            String firstGroup = m.group(1);
            String hex = firstGroup!=null ? firstGroup : m.group(2);
            txt = txt.replace(m.group(0),ChatColor.of(hex).toString());
        }
        txt = ChatColor.translateAlternateColorCodes('&', txt);
        return txt;
    }

    public static String removeFormats(String txt){
        Matcher m = Pattern.compile("<(#[0-9a-fA-F]{6})>|&(#[0-9a-fA-F]{6})")
                .matcher(txt);
        while (m.find()){
            txt = txt.replace(m.group(0),"");
        }
        txt = ChatColor.translateAlternateColorCodes('&', txt);
        for (char ch:ChatColor.ALL_CODES.toCharArray()){
            txt = txt.replace("ยง"+ch,"");
        }
        return txt;
    }

    public static String formatMentions(JDA jda,String txt,String msgFormat){
        Matcher usermentions = Pattern.compile("<@!?(\\d+)>")
                .matcher(txt);
        while (usermentions.find()){
            RestAction<User> action = jda.retrieveUserById(usermentions.group(1));
            CompletableFuture<User> future = action.submit();
            try {
                User user = future.get();
                txt = txt.replace(usermentions.group(0),ChatColor.of("#7289da")+"@"+user.getName()+"#"+user.getDiscriminator()+msgFormat);
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        Matcher channelmentions = Pattern.compile("<#(\\d+)>")
                .matcher(txt);
        while (channelmentions.find()){
            TextChannel channel = jda.getTextChannelById(channelmentions.group(1));
            if (channel != null){
                txt = txt.replace(channelmentions.group(0),ChatColor.of("#7289da")+"#"+channel.getName()+msgFormat);
            }
        }
        Matcher rolementions = Pattern.compile("<@&(\\d+)>")
                .matcher(txt);
        while (rolementions.find()){
            Role role = jda.getRoleById(rolementions.group(1));
            if (role != null){
                txt = txt.replace(rolementions.group(0),ChatColor.of("#"+Integer.toHexString(role.getColorRaw()))+"@"+role.getName()+msgFormat);
            }
        }

        return txt;
    }

}