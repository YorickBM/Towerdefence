package yorickbm.towerdefence.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import yorickbm.towerdefence.TowerDefence;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class ConfigManager {
    private File Setupf;
    private FileConfiguration Setup;

    public ConfigManager Initialize(String file) {
        try {
            File dataFolder = TowerDefence.getPlugin().getDataFolder();
            if (!dataFolder.exists()) dataFolder.mkdir();

            this.Setupf = new File(dataFolder, file);
            if (!this.Setupf.exists()) LoadDefaults();

            this.Reload();
        } catch(Exception ex) { }

        return this;
    }

    public ConfigManager LoadDefaults() {
        TowerDefence.getPlugin().saveResource(Setupf.getName(), false);
        return this;
    }

    public ConfigManager Save() {
        try {
            this.Setup.save(this.Setupf);
        } catch (IOException e) {
        }
        return this;
    }

    public FileConfiguration GetData() {
        return this.Setup;
    }

    public ConfigManager Reload() {
        this.Setup = YamlConfiguration.loadConfiguration(this.Setupf);
        return this;
    }

}
