package yorickbm.towerdefence.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import yorickbm.towerdefence.Mobs.ArenaMob;
import yorickbm.towerdefence.TowerDefence;
import yorickbm.towerdefence.arena.Arena;

import java.util.EventListener;
import java.util.UUID;

public class EntityDeathEventListener implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent e) {

        LivingEntity entity = e.getEntity();
        if(TowerDefence.getApi().isArenaMob(entity)) {
            //Arena arena = TowerDefence.getApi().getArenaForMob(entity); Seems to cause errors on death :/
            //ArenaMob mob = arena.getMobForEntity(entity);
            //mob.destroy(false);
            //arena.removeEntity(mob);
            e.getDrops().clear();
        }

    }

}
