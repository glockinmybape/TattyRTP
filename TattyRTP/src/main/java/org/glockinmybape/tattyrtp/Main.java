package org.glockinmybape.tattyrtp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.glockinmybape.tattyrtp.commands.RtpCmd;

import java.util.logging.Logger;

public class Main extends JavaPlugin {
    private RtpManager rtpManager;

    public RtpManager getRtpManager() {
        return this.rtpManager == null ? (this.rtpManager = new RtpManager(this)) : this.rtpManager;
    }

    public void onEnable() {
        this.saveDefaultConfig();
        this.getRtpManager();
        this.getCommand("rrtp").setExecutor(new RtpCmd(this));

        Logger log = Bukkit.getLogger();
        log.info("§b");
        log.info("§b .----------------------------------------------------------. ");
        log.info("§b| .-------------------------------------------------------. |");
        log.info("§b| |             \t\t\t\t\t\t");
        log.info("§b| |            §7Плагин: §bTattyRTP§8| §7Версия: §b1.0                ");
        log.info("§b| |        §7Создан для §bTattyWorld §8- §7Разработал: §bglockinmybape\t");
        log.info("§b| |                    §bvk.com/TattyWorld");
        log.info("§b| |             \t\t\t\t\t\t");
        log.info("§b| '-------------------------------------------------------'§b|");
        log.info("§b'-----------------------------------------------------------'");
        log.info("§b");
    }
}
