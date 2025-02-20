package com.badbones69.crazyenvoys.paper.support.libraries;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;

public enum PluginSupport {

    DECENT_HOLOGRAMS("DecentHolograms"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    CMI("CMI"),
    PLACEHOLDER_API("PlaceholderAPI"),
    WORLD_GUARD("WorldGuard"),
    WORLD_EDIT("WorldEdit"),
    ORAXEN("Oraxen"),
    ITEMS_ADDER("ItemsAdder");

    private final String name;

    private static final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    PluginSupport(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isPluginEnabled() {
        return plugin.getServer().getPluginManager().isPluginEnabled(name);
    }
}