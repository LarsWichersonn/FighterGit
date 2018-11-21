package Quester;

import MainScript.MainHandler;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;

import org.osbot.rs07.api.ui.Tab;
import utils.EasyEntManipulator;
import utils.Sleep;
import utils.Stopwatch;

import java.util.stream.IntStream;

public final class RomeoAndJuliet extends Quest {

    public EasyEntManipulator entityHelper = new EasyEntManipulator();
    public boolean walkToGate;

    enum QuestLocation {
        JULIETS_HOUSE (new Area(3153, 3430, 3163, 3424).setPlane(1)),
        JULIET (new Area(3157, 3425, 3160, 3425).setPlane(1)),
        ROMEO_IN_SQUARE (new Area(3210, 3423, 3213, 3422)),
        VARROCK_SQUARE (new Area(3203, 3438, 3222, 3410)),
        CHURCH (new Area(3252, 3488, 3259, 3471)),
        CHURCH_FATHER (new Area(3254, 3483, 3255, 3481)),
        APOTHECARY (new Area(3193, 3403, 3197, 3403)),
        APOTHECARYS_SHOP (new Area(3198, 3402, 3192, 3406)),
        EAST_GATE (new Area(3272, 3429, 3275, 3427)),
        EAST_MINE (new Area(3290, 3376, 3293, 3373)),
        BERRYS (new Area(3262, 3374, 3276, 3363)),
        BERRYS_BUSH (new Area(3275, 3368, 3272, 3369));

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

    public RomeoAndJuliet() {
        super(Quests.Quest.ROMEO_JULIET, 144);
        walkToGate = true;
    }

    @Override
    public boolean onLoop() throws InterruptedException {
        DebugLog("In R&J quest");
        switch(questStatus()){
            case 0:
                DebugLog("Case 0 selected");
                StartQuest();
                return true;
            case 20:
                DebugLog("Case 20 selected");
                LetterToRomeo();
                return true;
            case 30:
                DebugLog("Case 30 selected");
                RomeoToLawrence();
                return true;
            case 40:
                DebugLog("Case 40 selected");
                Apothecary_One();
                return true;
            case 50:
                DebugLog("Case 50 selected");
                ArrangePotion();
                return true;
            case 60:
                DebugLog("Case 60 selected");
                RomeoAndDone();
                return true;
            case 100:
                DebugLog("Case 100 selected");
                //Talk to romeo
                return false;
        }
        return false;
    }

    @Override
    public boolean isComplete() {
        if(questStatus()==100){
            return true;
        }
        return false;
    }

    private void RomeoAndDone() {
        if (!QuestLocation.VARROCK_SQUARE.entireArea().contains(myPosition())) {
            DebugLog("Walking to Romeo");
            getWalking().webWalk(QuestLocation.ROMEO_IN_SQUARE.randomPosition());
        } else {
            DebugLog("Talking to Romeo");
            ConverseWithCharacter(5037, "Romeo","Click here to continue");
            Sleep.SleepNormal(1000,200, true);

            Stopwatch stopwatch = new Stopwatch();
            stopwatch.StartStopwatch();
            while(questStatus() == 60 && stopwatch.getElapsedTimeSeconds() < 120){
                if(pendingContinue()){
                    selectContinue();
                }
                Sleep.SleepNormal(100,20, false);
            }
        }
        if (isComplete()) {
            MainHandler.shouldWackAQuest = false;
        }
    }

    private void ArrangePotion() {
        if (getInventory().getAmount("Cadava potion") >= 1){
            if (!QuestLocation.JULIETS_HOUSE.entireArea().contains(myPosition())) {
                DebugLog("Walking to Juliet");
                getWalking().webWalk(QuestLocation.JULIETS_HOUSE.randomPosition());
            } else if ((entityHelper.isNpcValid(npcs.getAll().stream().filter(n -> n.hasAction("Talk-to") && (n.getId() == 6268)).findFirst().orElse(null)))) {
                DebugLog("Talking to Juliet");
                ConverseWithCharacter(6268, "Juliet","Click here to continue");
                Sleep.SleepNormal(1000,200, true);
                DebugLog("~Inbuilt talker ended~");
                Stopwatch stopwatch = new Stopwatch();
                stopwatch.StartStopwatch();
                while(questStatus() == 50 && stopwatch.getElapsedTimeSeconds() < 120){
                    if(pendingContinue()){
                        selectContinue();
                    }
                    Sleep.SleepNormal(100,20, false);
                }

            } else {
                DebugLog("Can't see Juliet... trying to open nearest door");
                if (entityHelper.OpenNearestClosedDoor(10)){DebugLog("Opened a door");}else{DebugLog("no door seen");}
                Sleep.SleepNormal(1000,100, true);
            }

        } else if(getInventory().getAmount("Cadava berries") >= 1) {
            if (!QuestLocation.APOTHECARYS_SHOP.entireArea().contains(myPosition())) {
                DebugLog("Walking back via east gate");
                getWalking().webWalk(QuestLocation.EAST_MINE.randomPosition());
                Sleep.SleepNormal(300, 50, false);
                getWalking().webWalk(QuestLocation.EAST_GATE.randomPosition());
                Sleep.SleepNormal(300, 50, false);
                getWalking().webWalk(QuestLocation.APOTHECARY.randomPosition());
            } else {
                DebugLog("Talking to Apothecary");
                ConverseWithCharacter(5036, "Apothecary","Talk about something else.", "Talk about Romeo & Juliet.", "Click here to continue");
            }
        } else {
            if (QuestLocation.BERRYS.entireArea().contains(myPosition())) {
                DebugLog("Collecting Berries");

                RS2Object Bush = getObjects().getAll().stream().filter(o ->
                        QuestLocation.BERRYS.entireArea().contains(o.getX(), o.getY())
                                && o.hasAction("Pick-from")
                                && (o.getName().equals("Cadava bush"))
                                && IntStream.of(o.getModelIds()).anyMatch(x -> x == 7813 || x== 7816))
                        .findFirst().orElse(null);

                if(entityHelper.isObjectValid(Bush)){
                    long currentBerries = getInventory().getAmount("Cadava berries");
                    Bush.interact("Pick-from");
                    getTabs().open(Tab.INVENTORY);
                    Sleep.sleepUntil(() -> (getInventory().getAmount("Cadava berries") == currentBerries + 1), 1500);
                } else {
                    getTabs().open(Tab.INVENTORY);
                    DebugLog("Waiting for berrys to respawn");
                    Sleep.SleepNormal(1000,250,false);
                }
            } else {
                DebugLog("Walking via east gate");
                if (walkToGate) {
                    getWalking().webWalk(QuestLocation.EAST_GATE.randomPosition());
                    Sleep.SleepNormal(300,50, false);
                    walkToGate = false;
                }

                getWalking().webWalk(QuestLocation.EAST_MINE.randomPosition());
                Sleep.SleepNormal(300,50, false);
                getWalking().webWalk(QuestLocation.BERRYS_BUSH.randomPosition());
            }
        }
    }

    private void Apothecary_One() {
        if (!QuestLocation.APOTHECARYS_SHOP.entireArea().contains(myPosition())) {
            DebugLog("Walking to Apothecary");
            getWalking().webWalk(QuestLocation.APOTHECARY.randomPosition());
        } else {
            DebugLog("Talking to Apothecary");
            ConverseWithCharacter(5036, "Apothecary","Talk about something else.", "Talk about Romeo & Juliet.", "Ok, thanks", "Click here to continue");
            Sleep.SleepNormal(1000,200, true);
        }
    }

    private void RomeoToLawrence() {
        if (!QuestLocation.CHURCH.entireArea().contains(myPosition())) {
            DebugLog("Walking to Father L");
            getWalking().webWalk(QuestLocation.CHURCH_FATHER.randomPosition());
        } else {
            DebugLog("Talking to Father L");
            ConverseWithCharacter(5038, "Father Lawrence","Click here to continue");
            Sleep.SleepNormal(1000,200, true);
        }
    }

    private void LetterToRomeo() {
        if (!QuestLocation.VARROCK_SQUARE.entireArea().contains(myPosition())) {
            DebugLog("Walking to Romeo");
            getWalking().webWalk(QuestLocation.ROMEO_IN_SQUARE.randomPosition());
        } else {
            DebugLog("Talking to Romeo");
            ConverseWithCharacter(5037, "Romeo","Ok, thanks.");
            Sleep.SleepNormal(1000,200, true);
        }
    }

    private void StartQuest(){
        if (!QuestLocation.JULIETS_HOUSE.entireArea().contains(myPosition())) {
            DebugLog("Walking to Juliet");
            getWalking().webWalk(QuestLocation.JULIET.randomPosition());
        } else if (QuestLocation.JULIETS_HOUSE.entireArea().contains(myPosition())){
            DebugLog("Talking to Juliet");
            ConverseWithCharacter(6268, "Juliet","Yes I've met him.", "Certainly, I'll do so straight away.");
            Sleep.SleepNormal(1000,200, true);
        } else {
            DebugLog("Can't see Juliet... trying to open nearest door");
            if (entityHelper.OpenNearestClosedDoor(10)){DebugLog("Opened a door");}else{DebugLog("no door seen");}
            Sleep.SleepNormal(1000,100, true);
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
