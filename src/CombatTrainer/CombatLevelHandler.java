package CombatTrainer;

import MainScript.MainHandler;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import static org.osbot.rs07.script.MethodProvider.random;
import static org.osbot.rs07.script.MethodProvider.sleep;

public class CombatLevelHandler {
    //levels
    public int attLevel;
    public int strLevel;
    public int defLevel;
    public boolean atCows;
    public boolean atChickens;
    public Area chickenArea;
    public Area cowArea;
    public Area cowSafeArea;
    public CombatStateHandler combatStateHandler;
    public Script sI;



    public CombatLevelHandler(Script sI) {
        this.sI = sI;
    }

    public void settingUpParams () {
        combatStateHandler = new CombatStateHandler(sI);
        atCows = false;
        atChickens = false;
        attLevel = sI.skills.getDynamic(Skill.ATTACK);
        strLevel = sI.skills.getDynamic(Skill.STRENGTH);
        defLevel = sI.skills.getDynamic(Skill.DEFENCE);
        chickenArea = new Area(3014, 3282, 3020, 3297);
        cowArea = new Area(2916, 3291, 2938, 3267);
        cowSafeArea = new Area(2923, 3288, 2926, 3271);

    }

    public void checkStyle (int widgetNumber) throws InterruptedException {
        int style = sI.getConfigs().get(43);
        if (sI.getConfigs().get(43) != widgetNumber) {

            sI.log("On style "+style + " switching to " + widgetNumber);
            if (!sI.getTabs().getOpen().equals(Tab.ATTACK)) {
                sI.getTabs().open(Tab.ATTACK);
                sleep(300 + random(200));
            }
            if (widgetNumber == 0) {
                RS2Widget stab = sI.getWidgets().get(593,3);
                if (stab != null) {
                    stab.interact();
                }
            } else if (widgetNumber == 1) {
                RS2Widget lunge = sI.getWidgets().get(593,7);
                if (lunge != null) {
                    lunge.interact();
                }
            } else if (widgetNumber == 3) {
                RS2Widget block = sI.getWidgets().get(593,16);
                if (block != null) {
                    block.interact();
                }
            }
        }
    }

    public void attStrDef () throws InterruptedException {
        if (atChickens) {
            if (strLevel < 20) {
                checkStyle(1);
            } else if (attLevel < 20) {
                checkStyle(0);
            } else {
                checkStyle(3);
            }
        } else {
            if (defLevel < 42) {
                checkStyle(3);
            } else if (strLevel < 42) {
                 checkStyle(1);
            } else {
                checkStyle(0);
            }
        }
    }


    public boolean shouldDoCombatTraining () {
        attLevel = sI.skills.getDynamic(Skill.ATTACK);
        strLevel = sI.skills.getDynamic(Skill.STRENGTH);
        defLevel = sI.skills.getDynamic(Skill.DEFENCE);
        sI.log("att lvl" + attLevel + " str lvl " + strLevel + " def lvl "+defLevel);
        if (attLevel < 42 || strLevel < 42 || defLevel <42) {
            return true;
        }
        return false;
    }

    public void atMonster () throws InterruptedException {
        if (attLevel >= 20 && strLevel >= 20 && defLevel >= 20) {
            atCows = true;
            atChickens = false;
            cowHandler();
        } else {
            atCows = false;
            atChickens = true;
            chickenHandler();
        }
    }

    public int chickenHandler () throws InterruptedException {
        if (chickenArea.contains(sI.myPlayer())) {
            toggleRun();
            attStrDef();
            combatStateHandler.doRandom();
            NPC npc = sI.getNpcs().closest((Filter<NPC>) npc1 -> npc1 != null && npc1.getName().equals("Chicken") && npc1.exists() && sI.getMap().canReach(npc1) && npc1.getHealthPercent() > 0 && npc1.getInteracting() == null);
            handleFight(npc);
        } else {
            sI.walking.webWalk(chickenArea);
        }
        return random(50, 500);
    }

    public int cowHandler () throws InterruptedException {
        if (cowArea.contains(sI.myPlayer())) {
            toggleRun();
            attStrDef();
            combatStateHandler.doRandom();
            NPC npc = sI.getNpcs().closest((Filter<NPC>) npc1 -> npc1 != null && (npc1.getName().equals("Cow") || npc1.getName().equals("Cow calf")) && npc1.exists() && sI.getMap().canReach(npc1) && npc1.getHealthPercent() > 0 && npc1.getInteracting() == null);
            handleFight(npc);
        } else {
            sI.walking.webWalk(cowSafeArea);
        }
        return random(50, 500);
    }

    public void handleFight (NPC npc) {
        if (!sI.getCombat().isFighting()) {
            if (npc != null) {
                if (npc.interact("Attack"))  {
                    int randomExtraClick = (int) (Math.random() * 100 + 1);
                    if (randomExtraClick < 5) {
                        sI.log("Dubbel clicked");
                        if (npc.interact("Attack")){
                        }
                    }
                    new ConditionalSleep(3000, 1000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return npc.getHealthPercent() == 0;
                        }
                    }.sleep();
                }
            }
        }
    }

    public void toggleRun () throws InterruptedException {
        if (sI.settings.getRunEnergy() > 30 && !sI.settings.isRunning()) {
            sI.log("Toggeling run");
            sI.settings.setRunning(true);
            sleep(700+random(500));
        }
    }
    //Genie 326
}
