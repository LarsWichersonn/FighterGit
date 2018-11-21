package Quester;

import org.osbot.S;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import utils.EasyEntManipulator;
import utils.Sleep;

import static org.osbot.rs07.api.ui.EquipmentSlot.AMULET;

public final class RestlessGhost extends Quest {

    public EasyEntManipulator entityHelper = new EasyEntManipulator();

    enum QuestLocation {
        START_LOCATION (new Area(3243, 3213, 3243, 3207)),
        START_LOCATION_SUPER (new Area(3240, 3215, 3247, 3204)),
        URHNEY_LOCATION (new Area(3145, 3176, 3150, 3176)),
        URHNEY_LOCATION_SUPER (new Area(3145, 3176, 3150, 3174)),
        COFFIN_LOCATION (new Area(3250, 3191, 3251, 3192)),
        COFFIN_LOCATION_SUPER (new Area(3247, 3195, 3252, 3190)),
        ALTER_LOCATION (new Area(3121, 9568, 3121, 9565)),
        ALTER_LOCATION_SUPER (new Area(3121, 9569, 3111, 9564)),
        RUNAWAY (new Area(3106, 9576, 3108, 9576));

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

    public RestlessGhost() {
        super(Quests.Quest.THE_RESTLESS_GHOST, 107);
    }

    @Override
    public boolean isComplete() {
        if(questStatus()==5){
            return true;
        }
        return false;
    }

    @Override
    public boolean onLoop() throws InterruptedException {
        DebugLog("In Restless Ghost quest");
        switch(questStatus()){
            case 0:
                DebugLog("Case 0");
                StartQuest();
                return true;
            case 1:
                DebugLog("Case 1");
                TalktoUrhney();
                return true;
            case 2:
                DebugLog("Case 2");
                ChatWithGhost();
                return true;
            case 3:
                DebugLog("Case 3");
                GetSkull();
                return true;
            case 4:
                DebugLog("Case 4");
                ReturnSkull();
                return true;
            case 5:
                DebugLog("Case 5");
                return false;
        }
        return false;
    }

    private void ReturnSkull() throws InterruptedException {
        if (!QuestLocation.COFFIN_LOCATION_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to Coffin");
            getWalking().webWalk(QuestLocation.COFFIN_LOCATION.randomPosition());
        } else {
            RS2Object Coffin = getObjects().getAll().stream()
                    .filter(n -> n.getName().equals("Coffin") && (n.getId() == 15061 || n.getId() == 2145))
                    .findFirst().orElse(null);

            if (Coffin.hasAction("Open")){
                entityHelper.interact(Coffin,"Open");
                Sleep.sleepUntil(() -> Coffin.hasAction("Close"),500);
            }

            if (Coffin.hasAction("Close")) {
                getInventory().interact("use", "Ghost's skull");
                Sleep.sleepUntil(() -> getInventory().isItemSelected(), 500);
                if (Coffin != null){
                    entityHelper.interact(Coffin,"Use");
                    Sleep.SleepNormal(15000,2000,false);
                }
            }

        }
    }

    private void GetSkull() {
        if (!QuestLocation.ALTER_LOCATION_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to Alter");
            getWalking().webWalk(QuestLocation.ALTER_LOCATION.randomPosition());
        } else if (getConfigs().get(173)==0){
            DebugLog("Waiting for some run energy");
            Sleep.SleepNormal(15000,1000,false);
            getSettings().setRunning(true);
        } else {
            RS2Object Alter = getObjects().getAll().stream()
                    .filter(n -> n.getName().equals("Altar") && n.getId() == 2146)
                    .findFirst().orElse(null);

            if(Alter!=null){
                Alter.interact("Search");
                Sleep.sleepUntil(() -> questStatus() == 4,500);
                getWalking().webWalk(QuestLocation.RUNAWAY.randomPosition());
            }
        }
    }

    private void ChatWithGhost() throws InterruptedException {

        NPC Ghost = getNpcs().getAll().stream()
                .filter(n -> n.getId() == 922)
                .findFirst().orElse(null);

        if (!QuestLocation.COFFIN_LOCATION_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to Coffin");
            getWalking().webWalk(QuestLocation.COFFIN_LOCATION.randomPosition());
        } else if(!getEquipment().isWearingItem(AMULET,"Ghostspeak amulet")) {
            DebugLog("Equipping amulet");
            if (getInventory().contains("Ghostspeak amulet")){
                DebugLog("We have the amulet!");
                getInventory().interact("Wear","Ghostspeak amulet");
                Sleep.sleepUntil(() -> getEquipment().isWearingItem(AMULET,"Ghostspeak amulet"),1000);
            }
            else {
                DebugLog("I need to implement lost amulet here..."); //TODO check bank then talk to Urhney again for new amulet...
            }
        } else if (Ghost != null){
            DebugLog("Talking to Ghost");
            ConverseWithCharacter(922, "Restless Ghost","Click here to continue","Yep, now tell me what the problem is.");
            Sleep.SleepNormal(1000,200, true);
        } else {
            DebugLog("Looking for coffin");
            RS2Object Coffin = getObjects().getAll().stream()
                    .filter(n -> n.getName().equals("Coffin") && (n.getId() == 15061 || n.getId() == 2145))
                    .findFirst().orElse(null);
            if (Coffin != null){
                entityHelper.interact(Coffin,"Search");
            }
        }
    }

    private void TalktoUrhney() {
        if (!QuestLocation.URHNEY_LOCATION_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to Shed in swamp");
            getWalking().webWalk(QuestLocation.URHNEY_LOCATION.randomPosition());
        } else {
            DebugLog("Talking to Urhney");
            ConverseWithCharacter(923, "Father Urhney","Click here to continue","Father Aereck sent me to talk to you.","He's got a ghost haunting his graveyard.");
            Sleep.SleepNormal(1000,200, true);
        }
    }

    private void StartQuest() {
        if (!QuestLocation.START_LOCATION_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to Lumby Church");
            getWalking().webWalk(QuestLocation.START_LOCATION.randomPosition());
        } else {
            DebugLog("Talking to Aereck");
            ConverseWithCharacter(921, "Father Aereck","Click here to continue","I'm looking for a quest!","Ok, let me help then.");
            DebugLog("Done talking to Aereck");
            Sleep.SleepNormal(1000,200, true);
        }
    }

    private void ConverseWithCharacter(int CharID, String CharName, String... Options) {

        if (entityHelper.isNpcValid(npcs.getAll().stream().filter(n -> n.hasAction("Talk-to") && (n.getId() == CharID)).findFirst().orElse(null))) {
            DebugLog(CharName + " Spotted");

            try {
                entityHelper.interact(npcs.getAll().stream().filter(n -> n.hasAction("Talk-to") && (n.getId() == CharID)).findFirst().orElse(null), "Talk-to");
            } catch (InterruptedException e) {
                e.printStackTrace();
                DebugLog("Error in talking to " + CharName);
                Sleep.SleepNormal(1000,200,true);
            }

            Sleep.sleepUntil(() -> getDialogues().inDialogue(),3000);
            Sleep.SleepNormal(1000,200, true);

            if(getDialogues().inDialogue()){
                DebugLog("Talking to " + CharName);
                try {
                    getDialogues().completeDialogue(Options);
                    getDialogues().completeDialogue(Options);
                    getDialogues().completeDialogue(Options);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                    DebugLog("Error in talking to " + CharName);
                    Sleep.SleepNormal(1000,100, true);
                }
            }

        } else {
            DebugLog("Can't see " + CharName);
            Sleep.SleepNormal(1000,100, true);
        }

        return;
    }

    public void PassBotReferance() {
        entityHelper.exchangeContext(getBot());
    }

}

