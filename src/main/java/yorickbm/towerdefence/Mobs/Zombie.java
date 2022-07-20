package yorickbm.towerdefence.Mobs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import yorickbm.towerdefence.Core;

import java.util.List;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class Zombie extends ArenaMob {

    public Zombie() {
        super.tick_speed = 0.2f;
        super.arena = arena;
    }
    public Zombie(List<Block> path, Location location) {
        super.tick_speed = 0.2f;
        super.path = path;

        spawn(location);
    }

    @Override
    public void spawn(Location location) {
        org.bukkit.entity.Zombie z = (org.bukkit.entity.Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);

        z.setAware(false);
        z.setSilent(true);
        z.setAdult();

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET, 1);
        z.getEquipment().setHelmet(helmet);

        super.entity = z;
        tickUpdater.runTaskTimer(Core.getInstance(), 20, 1);
    }
}
