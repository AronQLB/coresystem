package net.meetlounge.core.module;


import net.meetlounge.core.Core;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {

    private final Core plugin;
    private final List<CoreModule> modules = new ArrayList<>();

    public ModuleManager(Core plugin) {
        this.plugin = plugin;
    }

    public void register(CoreModule module) {
        modules.add(module);
    }

    public void enableModules() {
        for (CoreModule module : modules) {
            try {
                module.enable();
                plugin.getLogger().info("Modul aktiviert: " + module.name());
            } catch (Exception exception) {
                plugin.getLogger().severe("Modul konnte nicht aktiviert werden: " + module.name());
            }
        }
    }

    public void disableModules() {
        for (CoreModule module : modules) {
            try {
                module.disable();
                plugin.getLogger().info("Modul deaktiviert: " + module.name());
            } catch (Exception exception) {
                plugin.getLogger().severe("Modul konnte nicht deaktiviert werden: " + module.name());
            }
        }
    }

    public void reloadModules() {
        for (CoreModule module : modules) {
            module.reload();
        }
    }
}