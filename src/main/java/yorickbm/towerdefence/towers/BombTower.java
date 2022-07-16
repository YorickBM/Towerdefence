package yorickbm.towerdefence.towers;

import org.bukkit.Material;
import yorickbm.towerdefence.API.Annotations.TowerLevel;
import yorickbm.towerdefence.Core;

import java.util.logging.Level;

/**
 * Last modified by: YorickBM on 27-06-2022
 */
public class BombTower extends Tower {

    public BombTower() {
        super();

        super.icon = Material.TNT;
        super.Name = "Bomb Tower";
        super.Description = "Bomb all near by mobs! Dealing damage";
        super.Range = 5;
    }

    @TowerLevel(level = 1)
    public void trigger_lvl1() {
        Core.getInstance().getLogger().log(Level.CONFIG, String.format("Trigger for lvl 1 of tower %s", super.Name));
    }

    @TowerLevel(level = 2)
    public void trigger_lvl2() {
        Core.getInstance().getLogger().log(Level.CONFIG, String.format("Trigger for lvl 2 of tower %s", super.Name));
    }

}
