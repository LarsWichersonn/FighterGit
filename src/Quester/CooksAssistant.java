package Quester;

import MainScript.MainHandler;
import org.osbot.rs07.api.Chatbox;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Tab;
import utils.EasyEntManipulator;
import utils.Sleep;

public class CooksAssistant extends Quest  {

    int CurrentyLookingFor = 0;
    public boolean hasAllItems;

    public EasyEntManipulator entityHelper = new EasyEntManipulator();

    enum QuestLocation {
        LUMBY_KITCHEN (new Area(3205, 3217, 3212, 3212)),
        CHEFS_LOCATION (new Area(3209, 3215, 3208, 3213)),
        BUCKET (new Area(3216, 9624, 3213, 9623)),
        BUCKET_SUPER (new Area(3207, 9626, 3220, 9614)),
        EGG (new Area(3227, 3300, 3233, 3297)),
        EGG_SUPER (new Area(3227, 3300, 3233, 3297)),
        MILK (new Area(3252, 3275, 3254, 3273)),
        MILK_SUPER (new Area(3253, 3277, 3265, 3268)),
        WHEAT (new Area(3156, 3300, 3159, 3298)),
        WHEAT_SUPER (new Area(3154, 3302, 3161, 3296)),
        TOP_OF_MILL (new Area(3165, 3308, 3165, 3305).setPlane(2)),
        TOP_OF_MILL_SUPER (new Area(3163, 3310, 3170, 3303).setPlane(2)),
        BOTTOM_OF_MILL (new Area(3165, 3305, 3169, 3305));

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

    public CooksAssistant() {
        super(Quests.Quest.COOKS_ASSISTANT, 29);
        hasAllItems = false;
    }

    @Override
    public boolean onLoop() throws InterruptedException {
        DebugLog("In Cooks Assistant quest");
        Sleep.SleepNormal(1000,200, true);
        switch(questStatus()){
            case 0:
                DebugLog("Case 0 selected");
                StartQuest();
                return true;
            //return true;
            case 1:
                DebugLog("Case 1 selected");
                getThings();
                return false;
            case 2:
                DebugLog("Case 2 selected");
                //Quest complete
                return false;
        }
        return false;
    }

    private void getThings() {
        long Empty_pot;
        long Pot_with_flour;
        long Bucket;
        long Bucket_of_milk;
        long Egg;

        int RequiredSlots = 3;

        //Test if we have the things that we need
        inventory = getInventory();

        //counting items
        Empty_pot = getInventory().getAmount("Pot");
        Pot_with_flour = getInventory().getAmount("pot of flour");
        Bucket = getInventory().getAmount("bucket");
        Bucket_of_milk = getInventory().getAmount("bucket of milk");
        Egg = getInventory().getAmount("egg");
        if (!getTabs().getOpen().equals(Tab.INVENTORY)) {
            inventory.tabs.open(Tab.INVENTORY);
        }
        if (Pot_with_flour >= 1 && Bucket_of_milk >= 1 && Egg >= 1 || hasAllItems) {
            hasAllItems = true;
            DebugLog("Turning in Quest...");
            StartQuest();
            return;
        }

        //checks required slots
        if (Empty_pot > 1 || Pot_with_flour > 1){RequiredSlots = RequiredSlots - 1;}
        if (Bucket > 1 || Bucket_of_milk > 1){RequiredSlots = RequiredSlots - 1;}
        if (Egg > 1){RequiredSlots = RequiredSlots - 1;}

        //banks if required
        if(inventory.getEmptySlotCount() < RequiredSlots){
            try {
                //this banks everything.
                goBank();
            } catch (InterruptedException e) {
                log("Warning... Banking error. Please report this bug.");
            }
        }

        //ensure that er need a pot or a bucket, and gets
        if (Pot_with_flour == 0 || Bucket_of_milk == 0){
            if (Empty_pot == 0 && Pot_with_flour == 0){
                DebugLog("Getting new pot");
                getPot();
                return;
            } else if (Bucket == 0 && Bucket_of_milk == 0){
                DebugLog("Getting new Bucket");
                getBucket();
                return;
            }
        }

        //does it a different way each time this code is run
        if (CurrentyLookingFor == 0) {
            CurrentyLookingFor = random(1, 3);
            DebugLog("Choosing new thing to get");
        }

        switch (CurrentyLookingFor) {
            case 1:
                DebugLog("Getting Egg");
                CurrentyLookingFor = getEgg();
                break;
            case 2:
                DebugLog("Getting Milk");
                CurrentyLookingFor = getMilk();
                break;
            case 3:
                DebugLog("Getting Flour");
                CurrentyLookingFor = getFlour();
                break;
        }

    }

    private int getFlour() {
        if(getInventory().contains("pot of flour")){
            return 0;
        }

        if(getInventory().contains("Grain")){
            if (!QuestLocation.TOP_OF_MILL_SUPER.entireArea().contains(myPosition())){
                DebugLog("Walking to Mill");
                getWalking().webWalk(QuestLocation.TOP_OF_MILL.randomPosition());
            } else {

                long NumOfWheat = getInventory().getAmount("Grain");

                getInventory().interact("use","Grain");
                Sleep.sleepUntil(() -> getInventory().isItemSelected(),500);
                Sleep.SleepNormal(100,20,false);

                //no break here because i love bugs
                RS2Object Obj = getObjects().getAll().stream()
                        .filter(o ->
                                o.getName().equals("Hopper")
                                        && map.isWithinRange(o, 7))
                        .findFirst().orElse(null);

                if (Obj != null){
                    try {
                        entityHelper.interact(Obj,"Use");
                        DebugLog("Trying to use Grain on Hopper");
                        Sleep.sleepUntil(() -> (NumOfWheat - getInventory().getAmount("Grain") == 1) && (myPlayer().getAnimation() == -1),3000);

                    } catch (InterruptedException e) {
                        DebugLog("Error Hoppering wheat");
                        Sleep.SleepNormal(1000,200,false);
                    }
                } else {DebugLog("Can't find hopper");}

                RS2Object HopperControls = getObjects().getAll().stream()
                        .filter(o ->
                                o.getName().equals("Hopper controls")
                                        && map.isWithinRange(o, 7))
                        .findFirst().orElse(null);

                if (HopperControls != null){
                    //the struggle is real - the grain goes before the animation even starts.
                    DebugLog("Napping for a second");
                    Sleep.SleepNormal(1000,100,false);
                    Sleep.sleepUntil(() -> myPlayer().getAnimation() == -1,3000);
                    DebugLog("Trying to use the hopper controls");
                    HopperControls.interact("Operate");
                    Sleep.sleepUntil(() -> getChatbox().contains(Chatbox.MessageType.GAME,"You operate the hopper. The grain slides down the chute."),3000);
                } else {
                    DebugLog("Hopper not found - Hopper is null.");
                    return 3;
                }

                //Early exit if the "Use" doesn't work for whatever reason
                if (NumOfWheat - getInventory().getAmount("Grain") != 1) {
                    DebugLog("Didn't use wheat on the hopper properly!!");
                    return 3;
                }

                Sleep.SleepNormal(400,50,false);
                getWalking().webWalk(QuestLocation.BOTTOM_OF_MILL.randomPosition());

                RS2Object FlourBin = getObjects().getAll().stream()
                        .filter(o ->
                                o.hasAction("Empty")
                                        && map.isWithinRange(o, 7))
                        .findFirst().orElse(null);

                if (FlourBin != null) {
                    try {
                        entityHelper.interact(FlourBin,"Empty");
                        Sleep.sleepUntil(() -> getInventory().getAmount("pot of flour") >= 1,3000);
                    } catch (InterruptedException e) {
                        DebugLog("Error getting flour");
                        Sleep.SleepNormal(1000,200,false);
                    }
                }

            }
            return 3;
        }


        else if (!QuestLocation.WHEAT_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to Wheat field");
            getWalking().webWalk(QuestLocation.WHEAT.randomPosition());
        }

        else {
            RS2Object Obj = getObjects().getAll().stream()
                    .filter(o ->
                            QuestLocation.WHEAT_SUPER.entireArea().contains(o.getX(), o.getY())
                                    && o.hasAction("Pick")
                                    && map.isWithinRange(o, 2))
                    .findFirst().orElse(null);
            try {
                entityHelper.interact(Obj,"Pick");
                Sleep.sleepUntil(() -> getInventory().contains("Grain"),2000);
            } catch (InterruptedException e) {
                DebugLog("Error Picking wheat");
                Sleep.SleepNormal(1000,200,false);
            }
        }
        return 3;
    }

    private int getMilk() {

        if(getInventory().contains("bucket of milk")){
            return 0;
        }

        if (!QuestLocation.MILK_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to cows");
            getWalking().webWalk(QuestLocation.MILK.randomPosition());
        } else {
            DebugLog("Milking Cows");

            RS2Object Cow = getObjects().getAll().stream()
                    .filter(n -> n.getName().equals("Dairy cow"))
                    .findFirst().orElse(null);

            try {
                if(Cow != null){
                    entityHelper.interact(Cow,"Milk");
                    Sleep.sleepUntil(() -> getInventory().contains("bucket of milk"),3000);
                } else {
                    DebugLog("Can't see any cows");
                }
            } catch (Exception e) {
                DebugLog("Error milking cows");
                Sleep.SleepNormal(1000,200,false);
            }
        }
        return 2;
    }

    private int getEgg() {
        if(getInventory().contains("Egg")){
            return 0;
        }

        if (!QuestLocation.EGG_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to chicken coup");
            getWalking().webWalk(QuestLocation.EGG.randomPosition());
        } else {
            DebugLog("Taking Egg");
            try {
                TakeItem("Egg",1944, 3229,3299);

            }

            catch (InterruptedException e) {

                DebugLog("Error Taking Egg");
                Sleep.SleepNormal(1000,200,false);
            }
        }
        return 1;
    }

    private void getBucket() {
        if (!QuestLocation.BUCKET_SUPER.entireArea().contains(myPosition())) {
            DebugLog("Walking to Cellar");
            getWalking().webWalk(QuestLocation.BUCKET.randomPosition());
        } else {
            DebugLog("Taking Bucket");
            try {
                TakeItem("Bucket",1925, 3216,9625);
            } catch (InterruptedException e) {
                DebugLog("Error Taking Bucket");
                Sleep.SleepNormal(1000,200,false);
            }
        }
    }

    private void getPot() {
        if (!QuestLocation.LUMBY_KITCHEN.entireArea().contains(myPosition())) {
            DebugLog("Walking to Kitchen");
            getWalking().webWalk(QuestLocation.CHEFS_LOCATION.randomPosition());
        } else {
            DebugLog("Taking Pot");
            try {
                TakeItem("Pot",1931, 3209,3214);
            } catch (InterruptedException e) {
                DebugLog("Error Taking Pot");
                Sleep.SleepNormal(1000,200,false);
            }
        }
    }

    private boolean TakeItem(String ItemName, int ItemID, int Xpos, int Ypos) throws InterruptedException {
        if(getGroundItems().get(Xpos,Ypos).isEmpty()){
            DebugLog("Can't see any" + ItemName + " - waiting 30s for respawn");
            Sleep.sleepUntil(() -> !getGroundItems().get(Xpos,Ypos).isEmpty(), 30000);
        } else if(entityHelper.isTableItemValid(getGroundItems().get(Xpos,Ypos).stream().filter((GroundItem g) -> g.getId() == ItemID).findFirst().orElse(null))){
            DebugLog(ItemName + " Spotted! Trying to take");
            try {
                entityHelper.interact(getGroundItems().get(Xpos,Ypos).stream().filter((GroundItem g) -> g.getId() == ItemID).findFirst().orElse(null),"Take");
                Sleep.sleepUntil(() -> getInventory().contains(ItemName),2000);
            } catch (Exception e) {
                e.printStackTrace();
                DebugLog("Error Taking " + ItemName + ".");
                Sleep.SleepNormal(1000,200,true);
                return false;
            }
        }
        if(getInventory().contains(ItemName)){return true;} else {return false;}
    }

    private void goBank() throws InterruptedException {
        getWalking().webWalk(Banks.LUMBRIDGE_UPPER);
        Sleep.sleepUntil(() -> Banks.LUMBRIDGE_UPPER.contains(myPosition()),15000);

        if (!getBank().isOpen()){
            getBank().open();
            Sleep.sleepUntil(() -> getBank().isOpen(),5000);
        }

        if (getBank().isOpen()) {
            if (!getBank().depositAll()) {
                DebugLog("Failure depositing all in the bank");
                //lol you better have bank space
                getInventory().dropAll();
            }
            Sleep.SleepNormal(1000, 200,true);
            //takes items out all classy like
            //includes sleep to ensure noi bugs in withdrawing
            if(getBank().contains("pot of flour")){getBank().withdraw("pot of flour",1);} else if(getBank().contains("empty pot")){getBank().withdraw("empty pot",1);}
            Sleep.SleepNormal(200,50,false);
            if(getBank().contains("bucket of milk")){getBank().withdraw("bucket of milk",1);} else if(getBank().contains("bucket")){getBank().withdraw("bucket",1);}
            Sleep.SleepNormal(200,50,false);
            if(getBank().contains("egg")){getBank().withdraw("egg",1);}
            Sleep.SleepNormal(200,50,false);

        }

    }

    @Override
    public boolean isComplete() {
        if(questStatus()==2){
            return true;
        }
        return false;
    }

    private void StartQuest(){
        if (!QuestLocation.LUMBY_KITCHEN.entireArea().contains(myPosition())) {
            DebugLog("Walking to Kitchen");
            getWalking().webWalk(QuestLocation.CHEFS_LOCATION.randomPosition());
        } else {
            DebugLog("Talking to Chef");
            ConverseWithCharacter(4626, "Cook","Click here to continue", "What's wrong?", "I'm always happy to help a cook in distress.", "Actually, I know where to find this stuff.");
            Sleep.SleepNormal(1000,200, true);
        }
        if (isComplete()) {
            MainHandler.shouldWackAQuest = false;
        }
        return;
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
