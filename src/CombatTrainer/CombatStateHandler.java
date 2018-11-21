package CombatTrainer;

import org.osbot.rs07.api.Widgets;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;
import utils.RandomString;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static org.osbot.rs07.script.MethodProvider.random;

public class CombatStateHandler {

    private CombatLevelHandler combatLevelHandler;
    private CombatStates combatStates;
    private RandomString randomString;
    private long timeLastSwitch;
    public int randomSwitchState;
    public static boolean shouldSwitchState;
    public static long lastSwitch;
    public static long holdStateFor;
    public Script sI;
    public ArrayList <State> enumStates;

    public CombatStateHandler(Script sI) {
        this.sI = sI;
        combatStates = new CombatStates(sI);
        enumStates = new ArrayList<>();
        enumStates.add(State.MAGECHECK);
        enumStates.add(State.IDLE);
        enumStates.add(State.MOVECAMERA);
        enumStates.add(State.SKILLCHECK);
        enumStates.add(State.RANDOMTYPER);
        setHoldStateFor();
    }

    private enum State {
        IDLE, MOVECAMERA, SKILLCHECK, MAGECHECK, RANDOMTYPER;
    }

    private State getState () {
        if (shouldSwitchState()) {
            randomSwitchState = (int )(Math.random() * enumStates.size() + 1);
            sI.log("Random state " + randomSwitchState);
            if (randomSwitchState == 1) {
                sI. log("Moving to camera state");
                return State.MOVECAMERA;
            } else if (randomSwitchState == 2) {
                sI.log("Moving to idle state");
                return State.IDLE;
            }else if (randomSwitchState == 3) {
                sI.log("Moving to magecheck state");
                return State.MAGECHECK;
            } else if (randomSwitchState == 4) {
                sI.log("Moving to randomTyper");
                return State.RANDOMTYPER;
            } else {
                sI. log("Moving to Skill check state");
                return State.SKILLCHECK;
            }
        } else {
            if (randomSwitchState == 1) {
                sI.log("Moving to camera state should not switch state movecamera");
                return State.MOVECAMERA;
            } else if (randomSwitchState == 2) {
                sI.log("Moving to idle state should not switch state idle");
                return State.IDLE;
            } else if (randomSwitchState == 3) {
                    sI.log("Moving to magecheck state should not switch state magecheck");
                    return State.MAGECHECK;
            } else if (randomSwitchState == 4) {
                sI.log("Moveing to randomtyper state should not switch state randomtyper");
                return State.RANDOMTYPER;
            } else {
                sI.log("Moving to Skill check state should not switch state skillcheck");
                return State.SKILLCHECK;
            }
        }
    }

    public void doRandom() throws InterruptedException {
        switch (getState()) {
            case MOVECAMERA:
                sI.log("doing camerastate");
                combatStates.moveCamera();
                break;
            case IDLE:
                sI.log("doing idle state");
                combatStates.idle();
                break;
            case SKILLCHECK:
                combatStates.skillOrMageCheck();
                break;
            case MAGECHECK:
                combatStates.skillOrMageCheck();
                break;
            case RANDOMTYPER:
                if (random(0, 10) == 9) {
                    RandomString gen = new RandomString(random(3, 14), ThreadLocalRandom.current());
                    sI.getKeyboard().typeString(String.valueOf(gen), true);
                }
            default: break;
        }
    }

    public boolean shouldSwitchState () {
        timeLastSwitch = System.currentTimeMillis();
        //sI.log((lastSwitch+holdStateFor) - (timeLastSwitch) +  " Time left to swtich state");
        if (lastSwitch +holdStateFor <= timeLastSwitch) {
            sI.log("im going to switch state");
            lastSwitch = System.currentTimeMillis();
            shouldSwitchState = true;
            holdStateFor = (int)(Math.random() * 30000 + 30000);
            return shouldSwitchState;
        } else {
            sI.log("Not time to switch state yet");
            shouldSwitchState = false;
            return shouldSwitchState;
        }
    }

    public void setHoldStateFor () {
        holdStateFor = (int)(Math.random() * 30000 + 30000);
    }
}
