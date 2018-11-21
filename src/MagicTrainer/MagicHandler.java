package MagicTrainer;

import CombatTrainer.CombatLevelHandler;
import CombatTrainer.CombatStateHandler;
import Exchange.ExchangeHandler;
import org.osbot.rs07.api.Equipment;
import org.osbot.rs07.api.Widgets;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import static org.osbot.rs07.script.MethodProvider.random;
import static org.osbot.rs07.script.MethodProvider.sleep;

public class MagicHandler {
    private Script sI;
    public int magicLevel;
    public Area mageAreaSkellies;
    public CombatStateHandler combatStateHandler;
    public CombatLevelHandler combatLevelHandler;
    private ExchangeHandler exchangeHandler;
    private boolean shouldDoMagicBuyins;
    public static final String[]			MAGIC_GEAR					= new String[] { "Blue wizard hat", "Zamorak monk bottom", "Blue wizard robe", "Amulet of magic", "Staff of air"};


    public MagicHandler(Script sI) {
        this.sI = sI;
        combatStateHandler = new CombatStateHandler(sI);
        combatLevelHandler = new CombatLevelHandler(sI);
        exchangeHandler = new ExchangeHandler(sI);
        mageAreaSkellies = new Area(3253, 9911, 3254, 9911);
    }

    public int loop () throws InterruptedException {
        shouldDoMagicBuyins();
        if (shouldDoMagicBuyins) {
            exchangeHandler.buyinsForLowMagic();
        } else {
            if (mageAreaSkellies.contains(sI.myPlayer())) {
                if (!sI.equipment.contains(MAGIC_GEAR)) {
                    equipAll(MAGIC_GEAR);
                }
                if (sI.getConfigs().get(108) != 3 && magicLevel < 13) {
                    toggleAutoCastAirStrike();
                } else if (sI.getConfigs().get(108) != 9 && magicLevel >= 13 ) {
                    toggleAutoCastFireStrike();
                } else if (sI.getConfigs().get(108) == 3 && sI.getConfigs().get(43) == 4 || sI.getConfigs().get(108) == 9 && sI.getConfigs().get(43) == 4) {
                    combatStateHandler.doRandom();
                    NPC npc = sI.getNpcs().closest((Filter<NPC>) npc1 -> npc1 != null && npc1.getName().equals("Skeleton") && sI.myPosition().distance(npc1.getPosition()) < 10 && npc1.exists() && npc1.getHealthPercent() > 0);
                    combatLevelHandler.handleFight(npc);
                }
            } else {
                sI.walking.webWalk(mageAreaSkellies);
            }
        }
        return sI.random(600,850);
    }

    public void equipAll(final String [] items) throws InterruptedException {
        final String [] itemsToEquip = items;
        for (int i = 0; i < itemsToEquip.length; i++) {
            Item item = sI.inventory.getItem(itemsToEquip[i]);
            if (item != null) {
                item.interact();
                random(25,50);
            }
        }
    }

    private void toggleAutoCastAirStrike() {
        if (sI.getTabs().getOpen().equals(Tab.ATTACK)) {
            RS2Widget smartCastWidget = sI.getWidgets().get(593,27);
            RS2Widget airStrike = sI.getWidgets().get(201,1,1);
            toggleStrike(smartCastWidget, airStrike);
        } else {
            sI.getTabs().open(Tab.ATTACK);
        }
    }

    private void toggleAutoCastFireStrike() {
        if (sI.getTabs().getOpen().equals(Tab.ATTACK)) {
            RS2Widget smartCastWidget = sI.getWidgets().get(593,27);
            RS2Widget fireStrike = sI.getWidgets().get(201,1,4);
            toggleStrike(smartCastWidget, fireStrike);
        } else {
            sI.getTabs().open(Tab.ATTACK);
        }
    }

    private void toggleStrike (RS2Widget smartCastWidget, RS2Widget strikeWidget) {
        if (smartCastWidget != null && smartCastWidget.isVisible()) {
            smartCastWidget.interact();
            new ConditionalSleep(2500, 3000) {
                @Override
                public boolean condition() {
                    return strikeWidget.isVisible();
                }
            }.sleep();
        } else if (strikeWidget != null && strikeWidget.isVisible()) {
            strikeWidget.interact();
        }
    }

    public boolean shouldDoMagic() {
        magicLevel = sI.skills.getDynamic(Skill.MAGIC);
        sI.log(magicLevel + " magicLevel");
        if (magicLevel < 33) {
            return true;
        }
        return false;
    }

    public boolean shouldDoMagicBuyins() {
        if ((sI.inventory.contains("Staff of air") || sI.equipment.contains("Staff of air")) && sI.inventory.contains("Mind rune")) {
            return shouldDoMagicBuyins = false;
        } else {
            return shouldDoMagicBuyins = true;
        }
    }
}
