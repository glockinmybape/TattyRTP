package org.glockinmybape.tattyrtp.commands;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.glockinmybape.tattyrtp.Main;
import org.glockinmybape.tattyrtp.Utils.Cooldown;

public class RtpCmd implements CommandExecutor {
    private Main main;

    public RtpCmd(Main main) {
        this.main = main;
    }


    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        } else if (args.length != 1) {
            return true;
        } else {
            String key = args[0];
            long cooldown;
            if (key.equalsIgnoreCase("safe")) {
                if (Cooldown.hasCooldown(commandSender.getName(), "safe")) {
                    cooldown = Cooldown.getCooldown(commandSender.getName(), "safe") / 1000L;
                    this.main.getConfig().getStringList("message.cooldown").forEach((x) -> {
                        this.send((Player)commandSender, ChatColor.translateAlternateColorCodes('&', x).replace("{cooldown}", "" + cooldown));
                    });
                    return true;
                }

                String cords = this.main.getRtpManager().randomTeleport((Player)commandSender);
                this.main.getConfig().getStringList("message.rtp-safe").forEach((x) -> {
                    this.send((Player)commandSender, ChatColor.translateAlternateColorCodes('&', x).replace("{xyz}", cords));
                });
                Cooldown.setCooldown(commandSender.getName(), (long)(this.main.getRtpManager().getCooldownSafe((Player)commandSender) * 1000), "safe");
            } else if (key.equalsIgnoreCase("near")) {
                if (!this.main.getRtpManager().hasNear((Player)commandSender)) {
                    this.sendMessage(commandSender, this.main.getConfig().getStringList("message.permission"));
                    return true;
                }

                if (Cooldown.hasCooldown(commandSender.getName(), "near")) {
                    cooldown = Cooldown.getCooldown(commandSender.getName(), "near") / 1000L;
                    this.main.getConfig().getStringList("message.cooldown").forEach((x) -> {
                        this.send((Player)commandSender, ChatColor.translateAlternateColorCodes('&', x).replace("{cooldown}", "" + cooldown));
                    });
                    return true;
                }

                if (((Player)commandSender).getWorld().getPlayers().size() < this.main.getConfig().getInt("min")) {
                    this.sendMessage(commandSender, this.main.getConfig().getStringList("message.min"));
                    return true;
                }

                this.main.getConfig().getStringList("message.rtp-near").forEach((x) -> {
                    this.send((Player)commandSender, ChatColor.translateAlternateColorCodes('&', x.replace("{name}", this.main.getRtpManager().nearbyRandomTeleport((Player)commandSender))));
                });
                Cooldown.setCooldown(commandSender.getName(), (long)(this.main.getRtpManager().getCooldownNear((Player)commandSender) * 1000), "near");
            } else {
                this.sendMessage(commandSender, this.main.getConfig().getStringList("message.argument"));
            }

            return true;
        }
    }

    private void sendMessage(CommandSender player, List<String> message) {
        List<String> list = new ArrayList();
        message.forEach((x) -> {
            list.add(ChatColor.translateAlternateColorCodes('&', x));
        });
        list.forEach((x) -> {
            this.send((Player)player, x);
        });
    }

    private void send(Player player, String message) {
        String[] msgs = message.split(";");
        String key = msgs[0];
        byte var6 = -1;
        switch(key.hashCode()) {
            case 110371416:
                if (key.equals("title")) {
                    var6 = 0;
                }
                break;
            case 198298141:
                if (key.equals("actionbar")) {
                    var6 = 1;
                }
        }

        switch(var6) {
            case 0:
                String title = msgs[1];
                String subtitle = msgs[2] != null ? msgs[2] : "";
                player.sendTitle(title, subtitle);
                break;
            case 1:
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msgs[1]));
                break;
            default:
                player.sendMessage(message);
        }

    }
}
