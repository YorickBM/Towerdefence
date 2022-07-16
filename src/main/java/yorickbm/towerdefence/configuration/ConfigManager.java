package yorickbm.towerdefence.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import yorickbm.towerdefence.Core;

/*
 * Created by YorickBM, Last modified on: 14-1-2019
 */
public class ConfigManager {
    private File Setupf;
    private FileConfiguration Setup;

    public ConfigManager Initialize(String file) {
        try {
            File dataFolder = Core.getInstance().getDataFolder();
            if (!dataFolder.exists()) dataFolder.mkdir();

            this.Setupf = new File(dataFolder, file);
            if (!this.Setupf.exists()) LoadDefaults();

            this.Reload();
        } catch(Exception ex) { }

        return this;
    }

    public ConfigManager LoadDefaults() {
        Core.getInstance().saveResource(Setupf.getName(), false);
        return this;
    }

    public ConfigManager Save() {
        try {
            this.Setup.save(this.Setupf);
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
