import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import yorickbm.towerdefence.Mobs.ArenaMob;

import java.util.List;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class ZombieMonster extends ArenaMob {

	public ZombieMonster() {
    	super.tick_speed = 0.2f;
        super.entityType = EntityType.ZOMBIE;
    }

    @Override
    public ArenaMob spawn(Location location, BlockFace direction) {
        super.spawn(location, direction);
        Zombie z = (Zombie) super.entity;

        z.setAware(false);
        //z.setSilent(true);
        z.setAdult();

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET, 1);
        z.getEquipment().setHelmet(helmet);

        return this;
    }
}
