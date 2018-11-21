package utils;

import MainScript.MainHandler;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.input.mouse.MiniMapTileDestination;
import org.osbot.rs07.script.MethodProvider;

public final class EasyEntManipulator extends MethodProvider {

    public boolean isNpcValid(NPC npc) {
        DebugLog("Testing if an NPC is valid");
        if (npc != null && map.canReach(npc) && map.isWithinRange(npc, 9) && !combat.isFighting()) {
            int id = npc.getId();
            if (id != -1) {
                for (NPC i : getNpcs().get(npc.getX(), npc.getY())) {
                    if (i.getId() == id)
                        DebugLog("NPC is valid");
                    return true;
                }
            }
        }
        DebugLog("NPC is NOT valid");
        if (npc == null) {DebugLog("NPC is null");}
        if (!map.canReach(npc)) {DebugLog("NPC is unreachable");}
        if (combat.isFighting()) {DebugLog("you are in combat");}
        if (!map.isWithinRange(npc, 15)) {DebugLog("NPC is >15 away");}

        return false;
    }
    public boolean isGroundItemValid(GroundItem item) {
        DebugLog("Testing if an Item is valid: "+ item);
        if (item != null && map.canReach(item) && item.isOnScreen() && item.getPosition().distance(myPlayer().getPosition()) <= 5) {
            int id = item.getId();
            if (id != -1) {
                for (GroundItem i : getGroundItems().get(item.getX(), item.getY())) {
                    if (i.getId() == id)
                        DebugLog("Item is valid");
                    return true;
                }
            }
        }
        DebugLog("Item is NOT valid");
        DebugLog("Reason:");
        if(item == null){DebugLog("Item is null:");}
        if(!map.canReach(item)){DebugLog("Item is unreachable:");}
        if(!item.isOnScreen()){DebugLog("Item is off screen:");}
        if(item.getPosition().distance(myPlayer().getPosition()) > 5){DebugLog("Item is too far away:");}
        return false;
    }
    public boolean isTableItemValid(GroundItem item) {
        DebugLog("Testing if an Item is valid: "+ item);
        if (item != null && item.isOnScreen() && item.getPosition().distance(myPlayer().getPosition()) <= 5) {
            if(map.canReach(item.getPosition().translate(0,1)) ||
                    map.canReach(item.getPosition().translate(0,-1)) ||
                    map.canReach(item.getPosition().translate(1,0)) ||
                    map.canReach(item.getPosition().translate(-1,0))) {
                int id = item.getId();
                if (id != -1) {
                    for (GroundItem i : getGroundItems().get(item.getX(), item.getY())) {
                        if (i.getId() == id)
                            DebugLog("Item is valid");
                        return true;
                    }
                }
            }
        }
        DebugLog("Item is NOT valid");
        DebugLog("Reason:");
        if(item == null){DebugLog("Item is null:");}
        if(!(map.canReach(item.getPosition().translate(0,1)) ||
                map.canReach(item.getPosition().translate(0,-1)) ||
                map.canReach(item.getPosition().translate(1,0)) ||
                map.canReach(item.getPosition().translate(-1,0)))){DebugLog("Item is unreachable:");}
        if(!item.isOnScreen()){DebugLog("Item is off screen:");}
        if(item.getPosition().distance(myPlayer().getPosition()) > 5){DebugLog("Item is too far away:");}
        return false;
    }
    public boolean isObjectValid(RS2Object object) {
        DebugLog("Testing if an Object is valid");
        if (object != null && map.canReach(object)) {
            int id = object.getId();
            if (id != -1) {
                for (RS2Object i : getObjects().get(object.getX(), object.getY())) {
                    if (i.getId() == id)
                        DebugLog("Object is valid");
                    return true;
                }
            }
        }
        DebugLog("Object is NOT valid");
        if(object == null){DebugLog("Object is null"); return false;}
        if(!map.canReach(object)){DebugLog("Can't reach Object");}
        return false;
    }

    public String interact(NPC n, String action) throws InterruptedException {
        DebugLog("Interacting with NPC: " + action);

        String status = "";

        if (map.isWithinRange(n, 6)) {
            status = "Interacting with " + n.getName();
            n.interact(action);
        }
        else if (n.getPosition().distance(myPlayer()) >= 7) {
            status = "Walking to " + n.getName();
            walking.walk(n.getPosition());
        }
        else if (walking.walk(n)) {
            status = "Walking to " + n.getName();
            sleep(random(900, 1200));
        }
        return status;
    }
    public String interact(GroundItem g, String action) throws InterruptedException {
        DebugLog("Interacting with Ground item: " + action);

        String status = "";

        if (g.getPosition().distance(myPlayer()) <= 5) {
            status = "Interacting with " + g.getName();
            g.interact(action);
        }
        else if (g.getPosition().distance(myPlayer()) >= 3) {
            status = "Walking to " + g.getName();
            walking.walk(g.getPosition());
        }
        else if (walking.walk(g)) {
            status = "Walking to " + g.getName();
            sleep(random(900, 1200));
        }
        return status;
    }
    public String interact(RS2Object o, String action) throws InterruptedException {
        DebugLog("Interacting with Object: " + action);

        String status = "";

        if (o.isVisible()) {
            status = "Interacting with " + o.getName();
            o.interact(action);
        }
        else if (o.getPosition().distance(myPlayer()) >= 3) {
            status = "Walking to " + o.getName();
            walking.walk(o.getPosition());
        }
        else if (walking.walk(o)) {
            status = "Walking to " + o.getName();
            sleep(random(900, 1200));
        }
        return status;
    }

    protected void DebugLog(String message){
        if(MainHandler.VERBOSE){log(message);
        }
        return;
    }

    public Boolean OpenNearestClosedDoor(int SearchRange) {
        RS2Object door = getObjects().getAll().stream().filter(o -> o.hasAction("Open") && map.canReach(o) && map.isWithinRange(o, SearchRange) && o.getName().equals("Door")).findFirst().orElse(null);
        if (door != null && map.canReach(door)) {
            door.interact("Open");
            Sleep.sleepUntil(() -> door.hasAction("Close"),3000);
            return true;
        }
        return false;
    }

}
