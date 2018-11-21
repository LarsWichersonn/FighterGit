package Quester;

import MainScript.MainHandler;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.Tabs;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;
import utils.Sleep;

import java.awt.event.KeyEvent;

public abstract class Quest extends MethodProvider {

    //Quest Variables
    private final Quests.Quest thisQuest;
    private int configID;

    //Core
    public abstract boolean onLoop() throws InterruptedException;

    protected Quest(final Quests.Quest quest, final int ConfigID) {
        thisQuest = quest;
        configID = ConfigID;
    }

    public abstract boolean isComplete();

    //Core Methods

    protected final int questStatus(){
        return getConfigs().get(configID);
    }

    protected boolean isStarted() {
        return getConfigs().get(configID) != 0;
    }

    protected boolean openQuestTab(){
        Tabs thisTabs = new Tabs();
        if(thisTabs.getOpen() != Tab.QUEST){
            return thisTabs.open(Tab.QUEST);
        } else {
            return true;
        }
    }

    //Universal Methods

    /*
     * Credit to Explv for this part. Shamelessly stol-Borrowed!
     * from his open source tutorial island.
     */

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

    //modified version for other quests

    protected boolean selectOptionWithText(String widgetText) {
        RS2Widget messageWidget = getWidgetWithText(widgetText);
        if (messageWidget == null) {
            return false;
        }
        if (messageWidget.getMessage().contains(widgetText)) {
            messageWidget.interact();
            Sleep.sleepUntil(() -> !messageWidget.isVisible(), 1000);
            return true;
        }
        return false;
    }

    private RS2Widget getWidgetWithText(String text) {
        return getWidgets().singleFilter(getWidgets().getAll(),
                widget -> widget.isVisible()
                        && (widget.getMessage().contains(text))
        );
    }

    //public method to pass on getBot() referance to other classes
    public abstract void PassBotReferance();

    protected void DebugLog(String message){
        if(MainHandler.VERBOSE){log(message);}
        return;
    }

}