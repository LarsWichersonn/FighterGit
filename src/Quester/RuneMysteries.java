package Quester;

import MainScript.MainHandler;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Tab;
import utils.EasyEntManipulator;
import utils.Sleep;

public final class RuneMysteries extends Quest {
    // LET OP!! LOOPS LANGS DE MAGERS
    public EasyEntManipulator entityHelper = new EasyEntManipulator();

    enum QuestLocation {
        START_LOCATION (new Area(3209, 3224, 3209, 3219).setPlane(1)),
        START_LOCATION_SUPER (new Area(3208, 3225, 3213, 3218).setPlane(1)),
        WIZARD_SUPER (new Area(3107, 9574, 3096, 9566)),
        WIZARD (new Area(3101, 9572, 3102, 9571)),
        RUNESHOP_SUPER (new Area(3250, 3404, 3255, 3399)),
        EAST_GATE (new Area(3283, 3426, 3265, 3431)),
        EAST_MINE (new Area(3295, 3385, 3290, 3372)),
        EAST_MINE2TEST (new Area(3112, 3176, 3116, 3168)),

        RUNESHOP (new Area(3251, 3401, 3254, 3401));

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

    public RuneMysteries() {
        super(Quests.Quest.RUNE_MYSTERIES, 63);
    }

    @Override
    public boolean isComplete() {
        if(questStatus()==6){
            return true;
        }
        return false;
    }

    @Override
    public boolean onLoop() throws InterruptedException {
        DebugLog("In Rune Mysteries quest");
        switch(questStatus()){
            case 0:
                StartQuest();
                return true;
            case 1:
                TalismanToWizards2();
                return true;
            case 2:
            case 3:
            case 4:
                PackageToVarrock();
                return true;
            case 5:
                TalismanToWizards();
                return true;
            case 6:
                return false;
        }
        return false;
    }

    private void PackageToVarrock() {
        if (!QuestLocation.RUNESHOP_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to Aubury");
            getWalking().webWalk(QuestLocation.EAST_MINE.randomPosition());
            Sleep.SleepNormal(300, 50, false);
            getWalking().webWalk(QuestLocation.RUNESHOP.randomPosition());
        } else {
            DebugLog("Talking to Aubury");
            ConverseWithCharacter(637, "Aubury","Click here to continue","I have been sent here with a package for you.");
            Sleep.SleepNormal(1000,200, true);
        }
    }

    private void TalismanToWizards() {
        if (!QuestLocation.WIZARD_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to Sedridor");
            Sleep.SleepNormal(300,50, false);
            getWalking().webWalk(QuestLocation.WIZARD.randomPosition());
        } else {
            DebugLog("Talking to Sedridor");
            ConverseWithCharacter(5034, "Sedridor","Click here to continue","I'm looking for the head wizard.","Ok, here you are.","Yes, certainly.");
            Sleep.SleepNormal(1000,200, true);
        }
        if (isComplete()) {
            MainHandler.shouldWackAQuest = false;
        }
    }

    private void TalismanToWizards2() {
        if (!getTabs().getOpen().equals(Tab.INVENTORY)) {
            inventory.tabs.open(Tab.INVENTORY);
        }
        if (!QuestLocation.WIZARD_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to Sedridor");
            getWalking().webWalk(QuestLocation.WIZARD.randomPosition());
        } else {
            DebugLog("Talking to Sedridor");
            ConverseWithCharacter(5034, "Sedridor","Click here to continue","I'm looking for the head wizard.","Ok, here you are.","Yes, certainly.");
            Sleep.SleepNormal(1000,200, true);
        }
    }

    private void StartQuest() {
        if (!QuestLocation.START_LOCATION_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to Horacio");
            getWalking().webWalk(QuestLocation.START_LOCATION.randomPosition());
        } else {
            DebugLog("Talking to Horacio");
            ConverseWithCharacter(815, "Duke Horacio","Click here to continue","Have you any quests for me?","Sure, no problem.");
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
