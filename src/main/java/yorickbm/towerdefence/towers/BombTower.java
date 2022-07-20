package yorickbm.towerdefence.towers;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import yorickbm.towerdefence.API.Annotations.TowerLevel;
import yorickbm.towerdefence.towers.Schematic.TowerSchematic;

import java.util.List;
import java.util.Random;

public class BombTower extends Tower {

    public BombTower() {
        super();

        super.icon = Material.TNT;
        super.Name = "Bomb Tower";
        super.Description = "Bomb all near by mobs! Dealing damage";
        super.Range = 5;

        super.schematic = new TowerSchematic("");
    }

    @TowerLevel(level = 1, costs = 42)
    public void trigger_lvl1(List<Entity> entities) {

        Random rand = new Random();
        Entity victem = entities.get(0);

        victem = entities.get(rand.nextInt(entities.size()));
        victem.getWorld().createExplosion(victem.getLocation().getX(), victem.getLocation().getY(), victem.getLocation().getZ(),
                3, false, false);

        super.setCooldown(12);

    }

    @TowerLevel(level = 2, costs = 67)
    public void trigger_lvl2(List<Entity> entities) {
        Random rand = new Random();
        Entity victem = entities.get(0);

        victem = entities.get(rand.nextInt(entities.size()));
        victem.getWorld().createExplosion(victem.getLocation().getX(), victem.getLocation().getY(), victem.getLocation().getZ(),
                5, false, false);

        super.setCooldown(12);
    }

    @TowerLevel(level = 3, costs = 96)
    public void trigger_lvl3(List<Entity> entities) {
        Random rand = new Random();
        Entity victem = entities.get(0);

        victem = entities.get(rand.nextInt(entities.size()));
        victem.getWorld().createExplosion(victem.getLocation().getX(), victem.getLocation().getY(), victem.getLocation().getZ(),
                5, false, false);

        victem = entities.get(rand.nextInt(entities.size()));
        victem.getWorld().createExplosion(victem.getLocation().getX(), victem.getLocation().getY(), victem.getLocation().getZ(),
                5, false, false);

        super.setCooldown(8);
    }

    @TowerLevel(level = 4, costs= 110)
    public void trigger_lvl4(List<Entity> entities) {
        Random rand = new Random();
        Entity victem = entities.get(0);

        victem = entities.get(rand.nextInt(entities.size()));
        victem.getWorld().createExplosion(victem.getLocation().getX(), victem.getLocation().getY(), victem.getLocation().getZ(),
                5, false, false);

        victem = entities.get(rand.nextInt(entities.size()));
        victem.getWorld().createExplosion(victem.getLocation().getX(), victem.getLocation().getY(), victem.getLocation().getZ(),
                5, false, false);

        super.setCooldown(4);
    }

}
