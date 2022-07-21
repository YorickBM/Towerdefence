package yorickbm.towerdefence.Mobs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class Monster extends ArenaMob {

    public Monster() {
        super.tick_speed = 0.2f;
        super.arena = arena;
        super.entityType = EntityType.ZOMBIE;
    }
    public Monster(List<Block> path, Location location) {
        super.tick_speed = 0.2f;
        super.path = path;
        super.entityType = EntityType.ZOMBIE;

        spawn(location);
    }

    @Override
    public Entity spawn(Location location) {
        Zombie z = (Zombie) super.spawn(location);

        z.setAware(false);
        //z.setSilent(true);
        z.setAdult();

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET, 1);
        z.getEquipment().setHelmet(helmet);

        return z;
    }
}
