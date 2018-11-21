package RangeTrainer;

import CombatTrainer.CombatLevelHandler;
import CombatTrainer.CombatStateHandler;
import org.osbot.rs07.api.Equipment;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import static org.osbot.rs07.script.MethodProvider.random;

/**
 * Created by larsw on 13-11-2018.
 */
public class RangeHandler {
    public Script sI;
    public boolean atCows;
    public boolean atChickens;
    public boolean shouldChangeEquipAt;
    public int equipAt;

    public Area chickenArea;
    public Area cowArea;
    public Area cowSafeArea;
    public CombatStateHandler combatStateHandler;
    public CombatLevelHandler combatLevelHandler;

    public RangeHandler(Script sI) {
        this.sI = sI;
        atCows = false;
        atChickens = false;
        shouldChangeEquipAt = true;
        equipAt = 0;
        chickenArea = new Area(3014, 3282, 3020, 3297);
        cowArea = new Area(2916, 3292, 2938, 3267);
        cowSafeArea = new Area(2923, 3288, 2926, 3271);
        combatStateHandler = new CombatStateHandler(sI);
        combatLevelHandler = new CombatLevelHandler(sI);
    }

    public boolean shouldDoRange() {
        if (sI.skills.getDynamic(Skill.RANGED) < 80) {
            return true;
        }
        return false;
    }

    public int chickenHandler () throws InterruptedException {
        if (chickenArea.contains(sI.myPlayer())) {
            handleArrows();
            GroundItem bronzeArrow = sI.groundItems.closest("Bronze arrow");
            if (!sI.combat.isFighting() && bronzeArrow != null && bronzeArrow.exists()) {
                bronzeArrow.interact("Take");
                new ConditionalSleep(3000, 1000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return !bronzeArrow.exists();
                    }
                }.sleep();
            } else {
                combatLevelHandler.toggleRun();
                combatStateHandler.doRandom();
                NPC npc = sI.getNpcs().closest((Filter<NPC>) npc1 -> npc1 != null && npc1.getName().equals("Chicken") && npc1.exists() && sI.getMap().canReach(npc1) && npc1.getHealthPercent() > 0 && npc1.getInteracting() == null);
                combatLevelHandler.handleFight(npc);
            }
        } else {
            sI.walking.webWalk(chickenArea);
        }
        return random(50, 500);
    }

    public int cowHandler () throws InterruptedException {
        if (cowArea.contains(sI.myPlayer())) {
            handleArrows();
            GroundItem bronzeArrow = sI.groundItems.closest("Bronze arrow");
            if (!sI.combat.isFighting() && bronzeArrow != null && bronzeArrow.exists()) {
                bronzeArrow.interact("Take");
                new ConditionalSleep(3000, 1000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return !bronzeArrow.exists();
                    }
                }.sleep();
            } else {
                sI.log("Cows");
                combatLevelHandler.toggleRun();
                combatStateHandler.doRandom();
                NPC npc = sI.getNpcs().closest((Filter<NPC>) npc1 -> npc1 != null && (npc1.getName().equals("Cow") || npc1.getName().equals("Cow calf")) && npc1.exists() && sI.getMap().canReach(npc1) && npc1.getHealthPercent() > 0 && npc1.getInteracting() == null);
                combatLevelHandler.handleFight(npc);
            }
        } else {
            sI.walking.webWalk(cowSafeArea);
        }
        return random(50, 500);
    }

    public void handleArrows() {
        if (sI.inventory.contains("Bronze Arrow")) {
            Item item = sI.inventory.getItem("Bronze Arrow");
            if (shouldChangeEquipAt) {
                equipAt = (int) (Math.random() * 100 + 50);
                shouldChangeEquipAt = false;
            }
            if (item != null && item.getAmount() > equipAt) {
                item.interact();
                shouldChangeEquipAt = true;
            }
        }
    }

    public void atMonster() throws InterruptedException {
        if (sI.skills.getDynamic(Skill.RANGED) < 20) {
            atCows = false;
            atChickens = true;
            chickenHandler();
        } else {
            atCows = true;
            atChickens = false;
            cowHandler();
        }
    }
}