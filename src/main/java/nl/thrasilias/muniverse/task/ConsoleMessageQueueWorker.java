package nl.thrasilias.muniverse.task;

import nl.thrasilias.muniverse.Main;
import nl.thrasilias.muniverse.bot.Bot;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.Queue;

public class ConsoleMessageQueueWorker extends Thread {
    public static final Queue<String> QUEUE = new LinkedList<>();

    public ConsoleMessageQueueWorker() {
        super("WildCore");
    }

    @Override
    public void run() {
        Bot bot = Main.getInstance().getBot();
        while (true) {
            try {
                StringBuilder message = new StringBuilder();
                String line = QUEUE.poll();
                while (line != null) {
                    if (message.length() + line.length() + 1 > 2000) {
                        bot.sendMessageToConsole(message.toString());
                        message = new StringBuilder();
                    }
                    message.append(line).append("\n");
                    line = QUEUE.poll();
                }
                if (StringUtils.isNotBlank(message.toString().replace("\n", "")))
                    bot.sendMessageToConsole(message.toString());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}