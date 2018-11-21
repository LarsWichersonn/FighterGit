package Quester;

import com.thoughtworks.xstream.mapper.Mapper;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.Walking;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.event.InteractionEvent;
import org.osbot.rs07.utility.ConditionalSleep;
import utils.EasyEntManipulator;
import utils.Sleep;

public class ImpCatcher extends Quest {

    public EasyEntManipulator entityHelper = new EasyEntManipulator();

    enum QuestLocation {
        START_LOCATION (new Area(3102, 3165, 3105, 3162).setPlane(2)),
        ORB_LOCATION (new Area(2997, 3316, 3012, 3300)),
        IMP_FIGHT (new Area(3029, 3325, 2981, 3281)),
        IMP_LOCATION (new Area(3004, 3316, 3009, 3309));

        // 2994, 3314, 3011, 3292

        //private final int adamantArrowID = 890;
        //private final int adamantArrowID = 890;
        // private final int adamantArrowID = 890;
        //private final int adamantArrowID = 890;
        public final int BlueWizardHead = 579;





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

    public ImpCatcher() {
        super(Quests.Quest.IMP_CATCHER, 160); //todo config number "start quest: http://prntscr.com/ld8mgk,
    }


    @Override
    public boolean onLoop() throws InterruptedException {
        DebugLog("In Imp catcher quest");
        switch(questStatus()){
            case 0:
                DebugLog("Case 0");
                StartQuest();
                return true;
            case 1:
                FightingImps();
                DebugLog("Case 1");
                return true;
            case 2:
                DebugLog("Case 2");
                return true;
            case 40:
                return true;
            case 50:
                return true;
            case 60:
                return true;
            case 100:
                DebugLog("Case 100 selected");
                return false;
        }
        return false;
    }
    private void TalktoWizardMizgogToEndQuest() {
        if (!QuestLocation.START_LOCATION.entireArea().contains(myPosition())) {
            DebugLog("Walking to Wizard tower");
            getWalking().webWalk(QuestLocation.START_LOCATION.randomPosition());
        } else {
            DebugLog("Talking to Wizard Mizgog");
            ConverseWithCharacter(5005, "Wizard Mizgog","Click here to continue","Give me a quest please.","I'll try.");
            Sleep.SleepNormal(10000,200, true);
        }
    }

    private void OrbLocation() {
        if (!QuestLocation.ORB_LOCATION.entireArea().contains(myPosition())) {
            DebugLog("Walking to Imps");
            getWalking().webWalk(QuestLocation.ORB_LOCATION.randomPosition());
        } else { QuestLocation.ORB_LOCATION.entireArea().contains(myPlayer());
            Sleep.SleepNormal(1000,200, true);
        }
    }


    private void StartQuest() {
        if (!QuestLocation.START_LOCATION.entireArea().contains(myPosition())) {
            DebugLog("Walking to Wizard tower");
            getWalking().webWalk(QuestLocation.START_LOCATION.randomPosition());
        } else {
            DebugLog("Is on Location");
            if (QuestLocation.START_LOCATION.entireArea().contains(myPosition()));
            TalktoWizardMizgogToEndQuest();
            Sleep.SleepNormal(1000,200, true);
        }
    }
    private void FightingImps() throws InterruptedException {
        if (inventory.contains("White bead") && inventory.contains("Red bead") && inventory.contains("Yellow bead") && inventory.contains("Black bead"))  {
            DebugLog("Got all beads");
            TalktoWizardMizgogToEndQuest();
            Sleep.SleepNormal(1000, 200, true);
        } else if (getGroundItems().closest(579, 1470, 1472, 1474, 1476) != null){
            DebugLog("True going to loot state");
            loot();
            sleep(700+random(500));
        } else {
            impHandler();
            DebugLog("Going for nearest Imp");
            sleep(700+random(500));
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


    public void handleFight (NPC npc) throws InterruptedException {
        if (!getCombat().isFighting())  {
            if (npc != null) {
                if (npc.interact("Attack"))  {
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

    public int impHandler () throws InterruptedException {
        if (QuestLocation.IMP_FIGHT.entireArea().contains(myPosition())) {
            toggleRun();
            DebugLog("in Imp handler");
            NPC npc = getNpcs().closest((Filter<NPC>) npc1 -> npc1 != null && (npc1.getName().equals("Imp") && npc1.exists() && getMap().canReach(npc1) && npc1.getHealthPercent() > 0 && npc1.getInteracting() == null));
            if (npc != null) {
                DebugLog("NPC = " + npc);
                handleFight(npc);
                sleep(5000);
            } else if (!myPlayer().isMoving() && !myPlayer().isUnderAttack() && !myPlayer().isAnimating()) {
                getWalking().webWalk(QuestLocation.IMP_LOCATION.randomPosition());
                DebugLog("REBOOT to imp Location 2.0");
                sleep(10000);
            }
        } else if (!myPlayer().isMoving() && !myPlayer().isUnderAttack() && !myPlayer().isAnimating() && !QuestLocation.IMP_FIGHT.entireArea().contains(myPosition())) {
            DebugLog("REBOOT to imp Location 1.0");
            getWalking().webWalk(QuestLocation.IMP_LOCATION.randomPosition());
            sleep(6000);
        }
        return random(50, 500);
    }
    //  public int impHandler () throws InterruptedException {
    //      if (QuestLocation.IMP_FIGHT.entireArea().contains(myPosition())) {
    //          toggleRun();
    //          DebugLog("in Imp handler");
    //          NPC npc = getNpcs().closest((Filter<NPC>) npc1 -> npc1 != null && (npc1.getName().equals("Imp") && npc1.exists() && getMap().canReach(npc1) && npc1.getHealthPercent() > 0 && npc1.getInteracting() == null));
    //          handleFight(npc);
    //      } else if (!myPlayer().isMoving() && !myPlayer().isUnderAttack()) {
    //          DebugLog("REBOOT to imp Location 1.0");

    //          getWalking().webWalk(QuestLocation.IMP_LOCATION.randomPosition());

    //          sleep(6000);

    //          }
    //      return random(50, 500);
    //  }


    public void toggleRun () throws InterruptedException {
        if (settings.getRunEnergy() > 30 && settings.isRunning()) {
            log("Toggeling run");
            settings.setRunning(true);
            sleep(700+random(500));
        }
    }


    private void loot() {
        final GroundItem BlueWizardHeadOrBeads = getGroundItems().closest(579, 1470, 1472, 1474, 1476);
        if (BlueWizardHeadOrBeads != null) {
            BlueWizardHeadOrBeads.interact("Take");


            new ConditionalSleep(5000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return !BlueWizardHeadOrBeads.exists();
                }
            }.sleep();

        } else {
            // moet nog (als de drop er wel is maar niet op kan klikken)

        }
    }


//       if (BlueWizardHead != null && BlueWizardHead.interact("Take")) {
//           // Sleep for 5 seconds or until the arrows no longer exist (Either we picked them up, or someone else did)
//           new ConditionalSleep(5000) {
//               @Override
//               public boolean condition() throws InterruptedException {
//                   return !BlueWizardHead.exists();
//               }
//           }.sleep();
//       }
//   }






    @Override
    public boolean isComplete() {
        if(questStatus()==2){ //todo number
            return true;
        }
        return false;
    }

    @Override
    public void PassBotReferance() {
        entityHelper.exchangeContext(getBot());
    }


}
