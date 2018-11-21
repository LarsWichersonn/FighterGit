package Wc_Fm;

import CombatTrainer.CombatStateHandler;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import static org.osbot.rs07.script.MethodProvider.random;
import static org.osbot.rs07.script.MethodProvider.sleep;

public class WcHandler {
    public Script sI;
    public int wcLevel;
    public Area woodcuttingAreaNormal;
    public Area woodcuttingAreaOak;
    public Area woodcuttingAreaBank;
    public Area woodcuttingAreaFiremakingStart;
    public Area woodcuttingAreaFiremaking;
    public boolean isDoingNormalTree;
    public boolean isDoingOakTree;
    public boolean firstTimeBanking;
    public boolean shouldGoToChopArea;
    public boolean isFiremaking;
    public boolean newTree;
    public CombatStateHandler combatStateHandler;


    public WcHandler(Script sI) {
        this.sI = sI;
        woodcuttingAreaNormal = new Area(3249, 3509, 3254, 3504);
        woodcuttingAreaOak = new Area(3274, 3437, 3283, 3422);
        woodcuttingAreaBank = new Area(3250, 3423, 3257, 3419);
        woodcuttingAreaFiremakingStart = new Area(3281, 3517, 3281, 3515);
        woodcuttingAreaFiremaking = new Area(3281, 3517, 3255, 3515);
        isDoingNormalTree = false;
        isDoingOakTree = false;
        isFiremaking = false;
        shouldGoToChopArea = true;
        shouldFirstTimeBank();
        combatStateHandler = new CombatStateHandler(sI);
        newTree = true;
    }

    public void atTree() throws InterruptedException {
        if (wcLevel < 15) {
            isDoingNormalTree = true;
            isDoingOakTree = false;
            handleNormalTree();
        } else {
            isDoingNormalTree = false;
            isDoingOakTree = true;
            handleOakTree();
        }
    }

    public void handleOakTree() throws InterruptedException {
        combatStateHandler.doRandom();
        if (sI.inventory.isFull()) {
            handleInventoryFull();
        } else {
            if (!woodcuttingAreaOak.contains(sI.myPlayer())) {
                walkToArea(woodcuttingAreaOak);
            }
            chopTree("Oak");
        }

    }

    public void handleNormalTree() throws InterruptedException {
        combatStateHandler.doRandom();
        if(firstTimeBanking) {
            handleBankFirstTime();
        } else {
            if (shouldGoToChopArea == true) {
                walkToArea(woodcuttingAreaNormal);
                shouldGoToChopArea = false;
            }
            handleInventoryFull();
            if (!isFiremaking) {
                chopTree("Tree");
            }
        }
    }

    private void handleInventoryFull() throws InterruptedException {
        if (sI.inventory.isFull() || isFiremaking) {
            if(isDoingNormalTree) {
                isFiremaking = true;
                handleFiremaking();
            } else {
                handleBankOakLogs();
            }
        }
    }

    private void walkToArea(Area area) {
        sI.walking.webWalk(area);
    }

    private void chopTree(String tree) throws InterruptedException {
        sI.log(!sI.myPlayer().isAnimating() + " , " + !sI.myPlayer().isMoving() + " , " + newTree);
        if (!sI.myPlayer().isAnimating() || (!sI.myPlayer().isMoving() && newTree)) {
            RS2Object treeObject = sI.getObjects().closest(obj -> obj != null && obj.getName().equals(tree) && sI.getMap().canReach(obj));
            if(treeObject.interact("Chop down")) {
                newTree = false;
                sI.log("new tree after interact: " + newTree);
                sleep(random(800, 1200));
                if (new ConditionalSleep(3000, 1000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return !treeObject.exists();
                    }
                }.sleep()) {
                    sI.log("Conditional sleep evaluated to true!");
                    //If the conditional sleep evaluated true then we set newRock to TRUE
                    newTree = true;
                    sI.log("new tree after sleep: " + newTree);
                }
            }
        }
    }

    public void handleFiremaking() {
        if(!woodcuttingAreaFiremaking.contains(sI.myPlayer())) {
            walkToArea(woodcuttingAreaFiremakingStart);
        } else {
            lightFire();
        }
    }

    private void lightFire() {
        if(sI.inventory.contains("Logs") && wcLevel < 15) {
            if(!sI.myPlayer().isAnimating() && !sI.myPlayer().isMoving()) {
                if(sI.getInventory().isItemSelected()) {
                    sI.getInventory().getItem("Logs").interact();
                    new ConditionalSleep(1000) {
                        @Override
                        public boolean condition() {
                            return sI.myPlayer().isAnimating() && sI.myPlayer().isMoving();
                        }
                    }.sleep();
                } else {
                    sI.getInventory().getItem("Tinderbox").interact("Use");
                    new ConditionalSleep(1000) {
                        @Override
                        public boolean condition() {
                            return sI.getInventory().isItemSelected();
                        }
                    }.sleep();
                }
            } else {
                if(sI.getInventory().isItemSelected()) {
                    sI.getInventory().getItem("Logs").hover();
                    new ConditionalSleep(1000) {
                        @Override
                        public boolean condition() {
                            return sI.myPlayer().isAnimating() && sI.myPlayer().isMoving();
                        }
                    }.sleep();
                } else {
                    sI.getInventory().getItem("Tinderbox").interact("Use");
                    new ConditionalSleep(1000) {
                        @Override
                        public boolean condition() {
                            return sI.getInventory().isItemSelected();
                        }
                    }.sleep();
                }
            }
        } else {
            isFiremaking = false;
        }
    }

    public void shouldFirstTimeBank () {
        wcLevel = sI.skills.getDynamic(Skill.WOODCUTTING);
        if (wcLevel > 1 && sI.inventory.contains("Bronze axe")) {
            firstTimeBanking = false;
        } else {
            firstTimeBanking = true;
        }
    }

    public void handleBankOakLogs() throws InterruptedException {
        if (!woodcuttingAreaBank.contains(sI.myPlayer()) || !sI.bank.isOpen()) {
            goToAndOpenBank();
        } else {
            sI.bank.depositAllExcept("Bronze axe");
        }
    }

    public boolean shouldDoWoodcutting() {
        wcLevel = sI.skills.getDynamic(Skill.WOODCUTTING);
        sI.log(wcLevel + " wcLevel");
        if (wcLevel < 40) {
            return true;
        }
        return false;
    }

    public void handleBankFirstTime() throws InterruptedException {
        if (!woodcuttingAreaBank.contains(sI.myPlayer()) || !sI.bank.isOpen()) {
            goToAndOpenBank();
        } else {
            sI.bank.depositAll();
            sI.bank.depositWornItems();
            sI.bank.withdraw("Bronze axe", 1);
            sI.bank.withdraw("Tinderbox", 1);
            firstTimeBanking = false;
        }
    }

    public void goToAndOpenBank () throws InterruptedException {
        if (!woodcuttingAreaBank.contains(sI.myPlayer())) {
            sI.walking.webWalk(woodcuttingAreaBank);
        } else {
            sI.log("Open de bank");
            if (!sI.getBank().isOpen()) {
                sI.getBank().open();
                new ConditionalSleep(3000, 1000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return sI.getBank().isOpen();
                    }
                }.sleep();
            }
        }
    }
}