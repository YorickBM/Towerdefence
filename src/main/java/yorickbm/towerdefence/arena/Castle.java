package yorickbm.towerdefence.arena;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import yorickbm.towerdefence.API.TDLocation;

public class Castle {

    private double _health;
    private double _maxHealth;
    private ArmorStand _dataRepresentation, _title;
    private Location _location;

    public Castle(String world, TDLocation location, double health) {
        _maxHealth = health;
        _location = location.toLocation(world);
    }

    /**
     * Generate all necessary data & visible representations for castle!
     */
    public void generate() {
        _health = _maxHealth;

        _dataRepresentation = (ArmorStand) _location.getWorld().spawnEntity(_location.clone().add(-0.5, -1.95, 0.5), EntityType.ARMOR_STAND);
        _dataRepresentation.setGravity(false);
        _dataRepresentation.setInvulnerable(true);
        _dataRepresentation.setCustomNameVisible(true);
        _dataRepresentation.setVisible(false);

        _title  = (ArmorStand) _location.getWorld().spawnEntity(_location.clone().add(-0.5, -1.65, 0.5), EntityType.ARMOR_STAND);
        _title.setGravity(false);
        _title.setInvulnerable(true);
        _title.setCustomNameVisible(true);
        _title.setCustomName("§eCastle");
        _title.setVisible(false);

        updateArmorStand();
    }

    /**
     * Display health of castle as a percentage! ■
     */
    private void updateArmorStand() {

        double damage_per_block = _maxHealth/10;
        StringBuilder builder = new StringBuilder();

        for(double i = 0; i < Math.floor(_health/damage_per_block);i++) {
            builder.append("§a■");
        }
        for(double i = Math.floor(_health/damage_per_block); i < 10;i++) {
            builder.append("§c■");
        }

        _dataRepresentation.setCustomName(builder.toString());
    }

    /**
     * Remove all data from the world
     */
    public void destory() {
        _dataRepresentation.remove();
        _title.remove();
    }

    /**
     * Apply damage to the castle
     *
     * @param damage - Amount of damage to apply!
     */
    public void applyDamage(double damage) {
        _health -= damage;
        updateArmorStand(); //Update visible representation
    }

    //A few getters & setters
    public double getHealth() { return _health; }

}
