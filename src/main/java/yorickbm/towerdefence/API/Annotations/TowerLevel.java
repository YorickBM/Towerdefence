package yorickbm.towerdefence.API.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 *
 * Allows for reflectional creation of level activation methods & level data
 * For example the costs of the tower to be placed/upgraded
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TowerLevel {

    public int level();
    public float costs();

}
