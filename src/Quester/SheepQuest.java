package Quester;

import MainScript.MainHandler;
import org.osbot.rs07.api.Quests;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;

import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;

import org.osbot.rs07.api.ui.Tab;
import utils.EasyEntManipulator;
import utils.Sleep;

public final class SheepQuest extends Quest {

    public EasyEntManipulator entityHelper = new EasyEntManipulator();

    enum QuestLocation {
        INSIDE_FREDS (new Area(3189, 3274, 3190, 3271)),
        START_OF_QUEST (new Area(3184, 3279, 3192, 3270)),
        SHEEP_FIELD (new Area(3193, 3274, 3210, 3257)),
        SPINNING_ROOM (new Area(3209, 3215, 3212, 3213).setPlane(1));

        final Area area;

        QuestLocation(Area area) {
            this.area = area;
        }

        protected Area entireArea(){
            return this.area;
        }

        protected Position randomPosition(){
            return this.area.getRandomPosition();
        }
    }

    public SheepQuest() {
        super(Quests.Quest.SHEEP_SHEARER, 179);
    }

    @Override
    public boolean onLoop() throws InterruptedException {
        DebugLog("In sheep quest");
        switch(questStatus()){
            case 0:
                DebugLog("Case 0 selected");
                BeginQuest();
                return true;
            case 1:
                DebugLog("Case 1 selected");
                ShearSheep();
                return true;
            case 20:
            case 21:
                DebugLog("Case 20 selected");
                DebugLog("Complete");
                return false;
        }
        return false;
    }

    @Override
    public boolean isComplete() {
        if(questStatus()==21){
            return true;
        }
        return false;
    }

    private void ShearSheep() throws InterruptedException {
        DebugLog("Trying to shear sheep & the like.");
        Sleep.SleepNormal(1000,100,true);

        switch(chooseShearSheepState()) {
            case 0:
                DebugLog("State error, TODO: create debug loop");
                Sleep.SleepNormal(5000, 100, true);
                break;
            case 1:
                ConverseWithFred2("Click here to continue");
                break;
            case 2:
                getWalking().webWalk(QuestLocation.INSIDE_FREDS.randomPosition());
                Sleep.SleepNormal(500, 150, false);
                break;
            case 3:
                SpinWool();
                break;
            case 4:
                getWalking().webWalk(QuestLocation.SPINNING_ROOM.randomPosition());
                Sleep.SleepNormal(500, 150, false);
                break;
            case 5:
                TakeShears();
                break;
            case 6:
                depositAllButShears();
                break;
            case 7:
                getWalking().webWalk(Banks.LUMBRIDGE_UPPER);
                Sleep.SleepNormal(500, 150, false);
                break;
            case 8:
                ShearNearestSheep();
                break;
            case 9:
                getWalking().webWalk(QuestLocation.SHEEP_FIELD.randomPosition());
                Sleep.SleepNormal(500, 150, false);
                break;
        }

    }

    private void BeginQuest() throws InterruptedException {
        DebugLog("Trying to Begin Quest");
        Sleep.SleepNormal(1000,100,true);
        if (!getTabs().getOpen().equals(Tab.INVENTORY)) {
            inventory.tabs.open(Tab.INVENTORY);
        }
        if(!QuestLocation.START_OF_QUEST.area.contains(myPosition())) {
            DebugLog("Walking to Freds House");
            getWalking().webWalk(QuestLocation.INSIDE_FREDS.randomPosition());
            Sleep.SleepNormal(500, 100, true);
        } else {
            ConverseWithFred("Click here to continue",
                    "I'm looking for a quest.",
                    "Yes okay. I can do that.",
                    "Of course!",
                    "I'm something of an expert actually");
        }
    }

    public void PassBotReferance(){
        entityHelper.exchangeContext(getBot());
    }

    private int chooseShearSheepState(){
        DebugLog("picking a state...");

        if(getInventory().getAmount("Ball of wool") >= 20){
            if (QuestLocation.START_OF_QUEST.entireArea().contains(myPlayer().getPosition())){
                DebugLog("Ready to turn in");
                return 1;
            } else {
                DebugLog("Walking to Freds' place");
                return 2;
            }
        }

        if (getInventory().getAmount("Wool", "Ball of wool") >= 20){
            DebugLog("Have enough wool, ready to spin");

            if(QuestLocation.SPINNING_ROOM.entireArea().contains(myPlayer().getPosition())){
                DebugLog("in spinning room, lets spin!");
                return 3;
            } else {
                if (getMap().canReach(new Position(3209,3213,1)) && getMap().getPlane() == 1){
                    DebugLog("in the spinning room, lets spin! (buggy webwalk errorcheck)");
                    return 3;
                } else {
                    DebugLog("Walking to spinning room");
                    return 4;
                }
            }
        }

        if (getInventory().getAmount("Shears") == 0){
            if (QuestLocation.START_OF_QUEST.entireArea().contains(myPlayer().getPosition())){
                DebugLog("Need to get shears");
                return 5;
            } else {
                DebugLog("Walking to Freds' place");
                return 2;
            }
        }

        if ((getInventory().getEmptySlotCount() + getInventory().getAmount("Wool", "Ball of wool")) < 20) {
            if(Banks.LUMBRIDGE_UPPER.contains(myPosition())){
                DebugLog("Depositing in bank");
                return 6;
            } else {
                DebugLog("Walking to Bank");
                return 7;
            }
        }

        if(getInventory().getAmount("Wool", "Ball of wool") < 20){

            if(QuestLocation.SHEEP_FIELD.entireArea().contains(myPlayer().getPosition())){
                DebugLog("Shearing sheep");
                return 8;
            } else {
                DebugLog("Walking to sheep field");
                return 9;
            }
        }

        return 0;
    }

    private void ConverseWithFred(String... Options) throws InterruptedException {

        if (entityHelper.isNpcValid(npcs.getAll().stream().filter(n -> n.hasAction("Talk-to") && (n.getId() == 732)).findFirst().orElse(null))) {
            DebugLog("Fred Spotted");

            try {
                entityHelper.interact(npcs.getAll().stream().filter(n -> n.hasAction("Talk-to") && (n.getId() == 732)).findFirst().orElse(null), "Talk-to");
            } catch (InterruptedException e) {
                e.printStackTrace();
                DebugLog("Error in talking to Fred.");
                Sleep.SleepNormal(1000,200,true);
            }

            Sleep.SleepNormal(1000,200, true);

            if(getDialogues().inDialogue()){
                DebugLog("Talking to Fred");
                try {
                    getDialogues().completeDialogue(Options);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    DebugLog("Error in talking to Fred.");
                    Sleep.SleepNormal(1000,100, true);
                }
            }

        } else {
            DebugLog("Can't see fred... trying to open nearest door");
            if (entityHelper.OpenNearestClosedDoor(10)){DebugLog("Opened a door");}else{DebugLog("no door seen");}
            Sleep.SleepNormal(1000,100, true);
        }

        return;
    }

    private void ConverseWithFred2(String... Options) throws InterruptedException {

        if (entityHelper.isNpcValid(npcs.getAll().stream().filter(n -> n.hasAction("Talk-to") && (n.getId() == 732)).findFirst().orElse(null))) {
            DebugLog("Fred Spotted");

            try {
                entityHelper.interact(npcs.getAll().stream().filter(n -> n.hasAction("Talk-to") && (n.getId() == 732)).findFirst().orElse(null), "Talk-to");
            } catch (InterruptedException e) {
                e.printStackTrace();
                DebugLog("Error in talking to Fred.");
                Sleep.SleepNormal(1000,200,true);
            }

            Sleep.SleepNormal(1000,200, true);

            if(getDialogues().inDialogue()){
                DebugLog("Talking to Fred");
                try {
                    getDialogues().completeDialogue(Options);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    DebugLog("Error in talking to Fred.");
                    Sleep.SleepNormal(1000,100, true);
                }
            }

        } else {
            DebugLog("Can't see fred... trying to open nearest door");
            if (entityHelper.OpenNearestClosedDoor(10)){DebugLog("Opened a door");}else{DebugLog("no door seen");}
            Sleep.SleepNormal(1000,100, true);
        }
        if (isComplete()) {
            MainHandler.shouldWackAQuest = false;
        }

        return;
    }

    private void TakeShears() throws InterruptedException {
        if(getGroundItems().get(3192,3272).isEmpty()){
            DebugLog("Can't see any Shears - waiting 30s for respawn");
            Sleep.sleepUntil(() -> !getGroundItems().get(3192,3272).isEmpty(), 30000);
        } else if(entityHelper.isTableItemValid(getGroundItems().get(3192,3272).stream().filter((GroundItem g) -> g.getId() == 1735).findFirst().orElse(null))){
            DebugLog("Shears Spotted! Trying to take"); // PAKT DIE NOG NIET SOMS STAAT DIE BUITEN HET HUIS (coordinate veranderen eventueel
            try {
                entityHelper.interact(getGroundItems().get(3192,3272).stream().filter((GroundItem g) -> g.getId() == 1735).findFirst().orElse(null),"Take");
                Sleep.sleepUntil(() -> getInventory().contains("Shears"),2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                DebugLog("Error Taking Shears.");
                Sleep.SleepNormal(1000,200,true);
            }
        }
    }

    private void SpinWool() throws InterruptedException {

        DebugLog("Snooping around spinning room");

        if (entityHelper.isObjectValid(objects.get(3209, 3212).stream().filter(o -> o.hasAction("Spin") && o.isVisible()).findFirst().orElse(null))) {
            try {
                DebugLog("Attempting to spin spinning wheel");
                entityHelper.interact(objects.get(3209, 3212).stream().filter(o -> o.hasAction("Spin") && o.isVisible()).findFirst().orElse(null), "Spin");


            } catch (InterruptedException e) {
                e.printStackTrace();
                DebugLog("Internal Spinning Error.");
                Sleep.SleepNormal(1000, 200, true);
            }
            if (getWidgets().isVisible(270,14,38))
                widgets.get(270,  14, 38).interact();

            Sleep.sleepUntil(() -> widgets.getWidgetContainingText("Spin:") != null, 2000);
            Sleep.SleepNormal(400, 50, false);

            Sleep.sleepUntil(() -> getInventory().getAmount("Ball of wool") >= 20, 45000);
            //      //Sleep to wait for interaction to happen - if click happens
            //      Sleep.SleepNormal(500, 100, false);

            //      if (widgets.getWidgetContainingText("Ball of Wool") != null) {
            //          DebugLog("Trying to make 20 balls of wool");

            //          widgets.getWidgetContainingText("Ball of Wool").interact("Spin");

            //          Sleep.sleepUntil(() -> widgets.getWidgetContainingText("Spin:") != null, 2000);
            //          Sleep.SleepNormal(400, 50, false);


            //          DebugLog("Attempting to type...");
            //          getKeyboard().typeString("20", true);

            //          Sleep.sleepUntil(() -> getInventory().getAmount("Ball of wool") >= 20, 45000);
            //      }
        }
    }

    private void depositAllButShears() throws InterruptedException {

        if (!getBank().isOpen()){
            getBank().open();
            Sleep.sleepUntil(() -> getBank().isOpen(),5000);
        }

            if (getBank().isOpen()) { // http://prntscr.com/lh02tq (BANK LOOPT VAST)
            if (!getBank().depositAllExcept("Shears")) {
                DebugLog("Failure depositing all in the bank");
                //lol you better have bank space
                getInventory().dropAllExcept("Shears");
            }
            Sleep.SleepNormal(1000, 200,true);
        }
    }

    private void ShearNearestSheep() throws InterruptedException {
        if (!getTabs().getOpen().equals(Tab.INVENTORY)) {
            inventory.tabs.open(Tab.INVENTORY);
        }
        DebugLog("Looking for Sheps to snip");
        NPC npc = getNpcs().getAll().stream()
                .filter(n ->
                        QuestLocation.SHEEP_FIELD.entireArea().contains(n.getX(), n.getY())
                                && n.hasAction("Shear")
                                && !n.hasAction("Talk-to")
                                && map.isWithinRange(n, 7))
                .findFirst().orElse(null);
        if (npc == null){
            DebugLog("No sheps near... wandering...");
            getWalking().webWalk(QuestLocation.SHEEP_FIELD.randomPosition());
        } else if(entityHelper.isNpcValid(npc)){
            try {
                DebugLog("Shearing attempted! Stand back!");
                entityHelper.interact(npc,"Shear");
                Sleep.sleepUntil(() -> !npc.hasAction("Shear"),2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                DebugLog("Internal Shearing Error.");
                Sleep.SleepNormal(1000,100,true);
            }
        }
    }

}
