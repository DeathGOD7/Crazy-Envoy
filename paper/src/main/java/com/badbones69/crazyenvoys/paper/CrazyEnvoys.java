package com.badbones69.crazyenvoys.paper;

import com.badbones69.crazyenvoys.paper.api.CrazyManager;
import com.badbones69.crazyenvoys.paper.api.FileManager;
import com.badbones69.crazyenvoys.paper.api.FileManager.Files;
import com.badbones69.crazyenvoys.paper.api.enums.Messages;
import com.badbones69.crazyenvoys.paper.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.paper.api.events.EnvoyEndEvent.EnvoyEndReason;
import com.badbones69.crazyenvoys.paper.api.objects.CoolDownSettings;
import com.badbones69.crazyenvoys.paper.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.paper.api.objects.EnvoySettings;
import com.badbones69.crazyenvoys.paper.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.paper.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.paper.commands.EnvoyCommand;
import com.badbones69.crazyenvoys.paper.commands.EnvoyTab;
import com.badbones69.crazyenvoys.paper.controllers.EditControl;
import com.badbones69.crazyenvoys.paper.controllers.EnvoyControl;
import com.badbones69.crazyenvoys.paper.controllers.FireworkDamageAPI;
import com.badbones69.crazyenvoys.paper.controllers.FlareControl;
import com.badbones69.crazyenvoys.paper.support.MetricsHandler;
import com.badbones69.crazyenvoys.paper.support.libraries.PluginSupport;
import com.badbones69.crazyenvoys.paper.support.SkullCreator;
import com.badbones69.crazyenvoys.paper.support.placeholders.PlaceholderAPISupport;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnvoys extends JavaPlugin implements Listener {

    private static CrazyEnvoys plugin;

    private final PluginManager pluginManager = getServer().getPluginManager();

    private FileManager fileManager;

    private Methods methods;

    // Envoy Required Classes.
    private EditorSettings editorSettings;
    private EnvoySettings envoySettings;
    private FlareSettings flareSettings;
    private CoolDownSettings coolDownSettings;
    private LocationSettings locationSettings;

    private CrazyManager crazyManager;

    private SkullCreator skullCreator;

    private FireworkDamageAPI fireworkDamageAPI;

    @Override
    public void onEnable() {
        plugin = this;

        fileManager = new FileManager();

        fileManager.logInfo(true)
                .registerCustomFilesFolder("/tiers")
                .registerDefaultGenerateFiles("Basic.yml", "/tiers", "/tiers")
                .registerDefaultGenerateFiles("Lucky.yml", "/tiers", "/tiers")
                .registerDefaultGenerateFiles("Titan.yml", "/tiers", "/tiers")
                .setup();

        pluginManager.registerEvents(fireworkDamageAPI = new FireworkDamageAPI(), this);

        methods = new Methods();

        locationSettings = new LocationSettings();
        editorSettings = new EditorSettings();
        coolDownSettings = new CoolDownSettings();
        envoySettings = new EnvoySettings();
        flareSettings = new FlareSettings();

        crazyManager = new CrazyManager();

        skullCreator = new SkullCreator();

        crazyManager.load();

        Messages.addMissingMessages();

        FileConfiguration config = Files.CONFIG.getFile();

        String metricsPath = config.getString("Settings.Toggle-Metrics");

        boolean metricsEnabled = config.getBoolean("Settings.Toggle-Metrics");

        String countDownSetting = config.getString("Settings.Crate-Countdown.Toggle");

        String envoyLocationBroadcast = config.getString("Settings.Envoy-Locations-Broadcast");

        if (metricsPath == null) {
            config.set("Settings.Toggle-Metrics", true);
            Files.CONFIG.saveFile();
        }

        if (countDownSetting == null) {
            config.set("Settings.Crate-Countdown.Toggle", false);
            config.set("Settings.Crate-Countdown.Time", 120);
            config.set("Settings.Crate-Countdown.Message", "&cReady to claim.");
            config.set("Settings.Crate-Countdown.Message-Seconds", " seconds.");
            Files.CONFIG.saveFile();
        }

        if (envoyLocationBroadcast == null) {
            config.set("Settings.Envoy-Locations-Broadcast", false);
            Files.CONFIG.saveFile();
        }

        if (metricsEnabled) {
            MetricsHandler metricsHandler = new MetricsHandler();

            metricsHandler.start();
        }

        enable();
    }

    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            if (editorSettings.isEditor(player)) {
                editorSettings.removeEditor(player);
                editorSettings.removeFakeBlocks();
            }
        }

        if (crazyManager.isEnvoyActive()) {
            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.SHUTDOWN);

            getServer().getPluginManager().callEvent(event);

            crazyManager.endEnvoyEvent();
        }

        crazyManager.unload();
    }

    private void enable() {
        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(new EditControl(), this);
        pluginManager.registerEvents(new EnvoyControl(), this);
        pluginManager.registerEvents(new FlareControl(), this);

        if (PluginSupport.PLACEHOLDER_API.isPluginEnabled()) {
            getLogger().warning("We no longer support placeholders using {}");
            getLogger().warning("We only support %% placeholders i.e %crazyenvoys_cooldown%");
            new PlaceholderAPISupport().register();
        }

        registerCommand(getCommand("crazyenvoys"), new EnvoyTab(), new EnvoyCommand());
    }

    private void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    public static CrazyEnvoys getPlugin() {
        return plugin;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Methods getMethods() {
        return methods;
    }

    // Envoy Required Classes.
    public EditorSettings getEditorSettings() {
        return editorSettings;
    }

    public FlareSettings getFlareSettings() {
        return flareSettings;
    }

    public EnvoySettings getEnvoySettings() {
        return envoySettings;
    }

    public CoolDownSettings getCoolDownSettings() {
        return coolDownSettings;
    }

    public LocationSettings getLocationSettings() {
        return locationSettings;
    }

    public CrazyManager getCrazyManager() {
        return crazyManager;
    }

    public SkullCreator getSkullCreator() {
        return skullCreator;
    }

    public FireworkDamageAPI getFireworkDamageAPI() {
        return fireworkDamageAPI;
    }
}