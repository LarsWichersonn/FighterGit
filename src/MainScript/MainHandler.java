package MainScript;

import CombatTrainer.CombatLevelHandler;
import CombatTrainer.CombatStateHandler;
import CombatTrainer.CombatStates;
import MagicTrainer.MagicHandler;
import Quester.*;
import Wc_Fm.WcHandler;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import utils.Sleep;
import utils.Timer;

import java.awt.*;
import java.awt.event.KeyEvent;


@ScriptManifest(author = "SSL", name = "From Scratch to beast 2.0", info = "Start anywhere", version = 1.02, logo = "")
public final class MainHandler extends Script {
    public static boolean shouldSwitchState;
    public static long lastSwitch;

    public static boolean VERBOSE = true;
    public static boolean shouldWackAQuest = false;

    public static Timer					timeRunning					= new Timer();
    private long						startTime					= System.currentTimeMillis();

    //classes
    private CombatStates combatStates;
    private CombatLevelHandler combatLevelHandler;
    private CombatStateHandler combatStateHandler;
    private WcHandler wcHandler;
    private MagicHandler magicHandler;
    private Exchange.GrandExchange grandExchange;
    private Position prevLocation = null;
    private long prevLocationChange = 0;
    private static final Quest sheepQuest = new SheepQuest();
    private static final Quest romeoAndJuliet = new RomeoAndJuliet();
    private static final Quest cooksAssistant = new CooksAssistant();
    private static final Quest runeMysteries = new RuneMysteries();
    private static final Quest restlessGhost = new RestlessGhost();
    private static final Quest impcatcher = new ImpCatcher();




    public void onStart () {
        log("Starting 2.0");
        combatStates = new CombatStates(this);
        combatLevelHandler = new CombatLevelHandler(this);
        combatStateHandler = new CombatStateHandler(this);
        magicHandler = new MagicHandler(this);
        wcHandler = new WcHandler(this);
        grandExchange = new Exchange.GrandExchange(this);
        combatLevelHandler.settingUpParams();
        shouldSwitchState = true;
        lastSwitch = System.currentTimeMillis();

        sheepQuest.exchangeContext(getBot());
        sheepQuest.PassBotReferance();

        romeoAndJuliet.exchangeContext(getBot());
        romeoAndJuliet.PassBotReferance();

        cooksAssistant.exchangeContext(getBot());
        cooksAssistant.PassBotReferance();

        runeMysteries.exchangeContext(getBot());
        runeMysteries.PassBotReferance();

        restlessGhost.exchangeContext(getBot());
        restlessGhost.PassBotReferance();

        impcatcher.exchangeContext(getBot());
        impcatcher.PassBotReferance();
    }

    @Override
    public int onLoop() throws InterruptedException {
        checkActivity();
        shouldWackAQuest();
        if (pendingContinue()) {
            selectContinue();
        }
        if (shouldWackAQuest) {
            log("Quest loop");
            whackTheQuest();
        } else if (combatLevelHandler.shouldDoCombatTraining()) {
            log("Combat loop");
            combatLevelHandler.atMonster();
        } else if (wcHandler.shouldDoWoodcutting()) {
            log("Woodcutting loop");
            wcHandler.atTree();
        } else if (magicHandler.shouldDoMagic()) {
            log("Magic loop");
            magicHandler.loop();
        }
        return random(50, 500);
    }

    public boolean shouldWackAQuest () {
        int attLevel = skills.getDynamic(Skill.ATTACK);
        int strLevel = skills.getDynamic(Skill.STRENGTH);
        int defLevel = skills.getDynamic(Skill.DEFENCE);
        if (attLevel == 20 && strLevel == 20 && !sheepCompleted()) {
            log("Wacking sheep");
            return shouldWackAQuest = true;
        } else if (attLevel == 20 && strLevel == 20 && defLevel == 30 && !romeoAndJulietCompleted()) {
            log("wacking romeo and juliet");
            return shouldWackAQuest = true;
        } else if (attLevel == 20  && strLevel == 20 && defLevel == 42 && !cooksAssistanceCompleted()) {
            log("Wacking cook assistance");
            return shouldWackAQuest = true;
        } else if (attLevel == 20 && strLevel == 35 && defLevel == 42 && !runeMysteriesCompleted()) {
            log("Wacking rune mystery");
            return shouldWackAQuest = true;
        } else {
            return shouldWackAQuest = false;
        }
    }

    public int whackTheQuest() throws InterruptedException {
        switch(WhackAQuest()){
            case 0:
                stop();
                break;
            case 1:
                sheepQuest.onLoop();
                break;
            case 2:
                romeoAndJuliet.onLoop();
                break;
            case 3:
                cooksAssistant.onLoop();
                break;
            case 4:
                runeMysteries.onLoop();
                break;
            case 5:
                restlessGhost.onLoop();
                break;
            case 6:
                impcatcher.onLoop();
                break;
        }
        return 200;
    }

    public static boolean sheepCompleted() {
        if (sheepQuest.isComplete()) {
            return true;
        }
        return false;
    }

    public static boolean romeoAndJulietCompleted() {
        if (romeoAndJuliet.isComplete()) {
            return true;
        }
        return false;
    }

    public static boolean cooksAssistanceCompleted() {
        if (cooksAssistant.isComplete()) {
            return true;
        }
        return false;
    }

    public static boolean runeMysteriesCompleted() {
        if (runeMysteries.isComplete()) {
            return true;
        }
        return false;
    }

    public static boolean restlessGhostCompleted() {
        if (restlessGhost.isComplete()) {
            return true;
        }
        return false;
    }

    public static boolean impCatcherCompleted() {
        if (impcatcher.isComplete()) {
            return true;
        }
        return false;
    }

    private int WhackAQuest() {
        if (!sheepQuest.isComplete()){
            return 1;
        }

        if (!romeoAndJuliet.isComplete()){
            return 2;
        }

        if (!cooksAssistant.isComplete()){
            return 3;
        }

        if (!runeMysteries.isComplete()){
            return 4;
        }

        if (!restlessGhost.isComplete()){
            return 5;
        }
        if (!impcatcher.isComplete()) {
            return 6;
        }
        return 0;
    }

    public int checkActivity() throws InterruptedException {
        if (!myPlayer().isVisible()) {
            return 600;
        }

        Position curLoc = myPlayer().getPosition();
        if (curLoc == prevLocation) {
            long curTime = System.currentTimeMillis();
            if (prevLocationChange == 0) {
                prevLocationChange = curTime;
            } else if (curTime - prevLocationChange > 120000) {
                log("Player hasn't moved in 1 min, logging out");
                logoutTab.logOut();
                stop();
            }
        } else {
            prevLocation = null;
            prevLocationChange = 0;
        }
        return 600;
    }

    protected boolean pendingContinue() {
        RS2Widget continueWidget = getContinueWidget();
        return continueWidget!= null && continueWidget.isVisible();
    }

    protected boolean selectContinue() {
        RS2Widget continueWidget = getContinueWidget();
        if (continueWidget == null) {
            return false;
        }
        if (continueWidget.getMessage().contains("Click here to continue")) {
            getKeyboard().pressKey(KeyEvent.VK_SPACE);
            Sleep.sleepUntil(() -> !continueWidget.isVisible(), 1000);
            return true;
        } else if (continueWidget.interact()) {
            Sleep.sleepUntil(() -> !continueWidget.isVisible(), 1000);
            return true;
        }
        return false;
    }

    private RS2Widget getContinueWidget() {
        return getWidgets().singleFilter(getWidgets().getAll(),
                widget -> widget.isVisible()
                        && (widget.getMessage().contains("Click here to continue")
                        || widget.getMessage().contains("Click to continue"))
        );
    }

    public void onPaint(Graphics2D g) {
        Graphics2D gr = g;

        long elapsed = System.currentTimeMillis() - startTime;
        g.drawString("Time running: " + timeRunning.getTimeRunningString(elapsed), 10, 150);

        Point mP = getMouse().getPosition();

// Draw a line from top of screen (0), to bottom (500), with mouse x coordinate
        g.drawLine(mP.x, 0, mP.x, 500);

// Draw a line from left of screen (0), to right (800), with mouse y coordinate
        g.drawLine(0, mP.y, 800, mP.y);

        mP = getMouse().getPosition();


// Draw a rectangle starting at x-10, y-10, with width and height of 20
        g.drawRect(mP.x - 10, mP.y - 10, 20, 20);

        mP = getMouse().getPosition();
        g.drawLine(mP.x - 5, mP.y + 5, mP.x + 5, mP.y - 5);
        g.drawLine(mP.x + 5, mP.y + 5, mP.x - 5, mP.y - 5);
    }

}
