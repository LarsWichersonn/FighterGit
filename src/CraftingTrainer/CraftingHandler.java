package CraftingTrainer;

import org.osbot.rs07.api.Widgets;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;


/**
 * Created by larsw on 13-11-2018.
 */
public class CraftingHandler {
    public Script sI;
    public Area craftingArea;
    public boolean isAnimating;
    public RS2Widget leatherChaps;
    public RS2Widget leatherBody;
    public RS2Widget leatherVambraces;
    public RS2Widget leatherCowl;
    public RS2Widget leatherBoots;
    public RS2Widget leatherGloves;



    public CraftingHandler(Script sI) {
        this.sI = sI;
        craftingArea =  new Area(3160, 3494, 3169, 3485);
        isAnimating = false;
        updateWidgets();
    }

    public boolean shouldDoCrafting() {
        if (sI.skills.getDynamic(Skill.CRAFTING) < 19) {
            return true;
        }
        return false;
    }

    public void updateWidgets() {
        leatherChaps = sI.getWidgets().get(270,19,38);
        leatherBody = sI.getWidgets().get(270,18,38);
        leatherVambraces = sI.getWidgets().get(270,17,38);
        leatherCowl = sI.getWidgets().get(270,16,38);
        leatherBoots = sI.getWidgets().get(270,15,38);
        leatherGloves = sI.getWidgets().get(270,14,38);
    }

    public void handleCrafting () throws InterruptedException {
        int craftingLevel = sI.skills.getDynamic(Skill.CRAFTING);
        if (craftingArea.contains(sI.myPlayer())) {
            if (sI.inventory.contains("Leather") && sI.inventory.contains("Needle") && sI.inventory.contains("Thread")) {
                if (!sI.bank.isOpen()) {
                    if (craftingLevel >= 18) {
                        craft(leatherChaps);
                    } else if (craftingLevel >= 14) {
                        craft(leatherBody);
                    } else if (craftingLevel >= 11) {
                        craft(leatherVambraces);
                    } else if (craftingLevel >= 9) {
                        craft(leatherCowl);
                    } else if (craftingLevel >= 7) {
                        craft(leatherBoots);
                    } else if (craftingLevel >= 1) {
                        craft(leatherGloves);
                    }
                } else {
                    sI.bank.close();
                }
            } else {
                if (sI.bank.isOpen()) {
                    handleBanking();
                } else {
                    sI.bank.open();
                }
            }
        } else {
            sI.walking.webWalk(craftingArea);
        }
    }

    private void handleBanking() {
        if (!sI.inventory.contains("Needle")) {
            sI.bank.withdraw("Needle", 1);
        }
        if (!sI.inventory.contains("Thread")) {
            sI.bank.withdrawAll("Thread");
        }
        if (!sI.inventory.contains("Leather")) {
            sI.bank.depositAllExcept("Needle", "Thread", "Leather");
            sI.bank.withdrawAll("Leather");
        }
    }

    public void craft (RS2Widget widget) throws InterruptedException {
        updateWidgets();
        if (sI.inventory.contains("Leather")) {
            if (!sI.dialogues.isPendingContinuation()) {
                if (!sI.myPlayer().isAnimating()) {
                    if (sI.getInventory().isItemSelected()) {
                        sI.getInventory().getItem("Needle").interact("Use");
                        sI.sleep(sI.random(1000, 1200));
                        updateWidgets();
                        new ConditionalSleep(100000) {
                            @Override
                            public boolean condition() {
                                updateWidgets();
                                return widget.isVisible();
                            }
                        }.sleep();
                    } else {
                        updateWidgets();
                        if (widget != null) {
                            if (widget.isVisible()) {
                                widget.interact();
                                updateWidgets();
                                new ConditionalSleep(100000) {
                                    @Override
                                    public boolean condition() throws InterruptedException {
                                        updateWidgets();
                                        return !isBusy();
                                    }
                                }.sleep();
                            }
                        } else {
                            sI.getInventory().getItem("Leather").interact();
                            sI.sleep(sI.random(1000, 1200));
                            updateWidgets();
                            new ConditionalSleep(100000) {
                                @Override
                                public boolean condition() throws InterruptedException {
                                    updateWidgets();
                                    return sI.getInventory().isItemSelected();
                                }
                            }.sleep();
                        }
                    }
                }
            } else {
                sI.dialogues.clickContinue();
            }
        }
    }


    public boolean isBusy() throws InterruptedException {
        boolean flag = false;
        for(int i = 0; i < 4; i++) {
            if (!sI.inventory.contains("Leather") || sI.dialogues.isPendingContinuation()) {
                return false;
            }
            if(sI.myPlayer().getAnimation() != -1) {
                flag = true;
                break;
            }
            sI.sleep(sI.random(4000, 5000));
        }
        return flag;
    }
}