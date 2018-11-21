package CombatTrainer;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;

public class CombatStates {
    Camera camera;
    public Script sI;

    public CombatStates(Script sI)  {
        this.sI = sI;
        camera = new Camera(sI);
    }

    public void moveCamera () {
        int random = (int) (Math.random() * 100 + 1);
        if (random <10 ) {
            int randomCamera= (int )(Math.random() * 2 + 1);
            int randomPitch = (int )(Math.random() * 24 + 40);
            int randomYaw = (int) (Math.random() * 358 + 1);
            Position position = new Position(randomPitch, randomYaw, 0);
 //           sI.log("moving camera to yaw: " + randomYaw + " Pitch: " + randomPitch);
            if (randomCamera == 1) {
//                sI.getCamera().toPosition(position);
//              NPC npc = getNpcs().closest((Filter<NPC>) npc1 -> npc1 != null && npc1.getName().equals("Chicken"));
//              getCamera().toEntity(npc);
            } else {
                camera.moveCamera(randomYaw,randomPitch);
            }
        }
    }

    public void idle () throws InterruptedException {
        if (sI.getCombat().isFighting()) {
            int random = (int )(Math.random() * 2 + 1);
            if (random == 1 ) {
                sI.mouse.moveOutsideScreen();
            } else {
                int x = (int) (Math.random() * 749 + 1);
                int y = (int) (Math.random() * 492 + 1);
                sI.mouse.move(x,y);
            }
        }
        long lastAnimation = System.currentTimeMillis();
        int random = (int) (Math.random() * 10000 + 5000);
        sI.log("Chilling for: " + random + "miliseconds");
        while(System.currentTimeMillis() - lastAnimation <= random) {
            if(sI.myPlayer().isAnimating())
                lastAnimation = System.currentTimeMillis();
            sI.sleep(50);
        }
    }

    public void skillOrMageCheck() throws InterruptedException {
        if(sI.random(0, 500) == 10) {
            if (!sI.getTabs().getOpen().equals(Tab.SKILLS)) {
                sI.getTabs().open(Tab.SKILLS);
                sI.sleep(300+sI.random(200));
                hoverMouse();
            } else {
                hoverMouse();
            }
        } else if (sI.random(0, 1000) == 11) {
            if (!sI.getTabs().getOpen().equals(Tab.MAGIC)) {
                    sI.getTabs().open(Tab.MAGIC);
                    sI.sleep(300+sI.random(200));
                hoverMouse();
            } else {
                hoverMouse();
            }
        }
    }

    public void hoverMouse() throws InterruptedException {
        sI.log("Doing skillcheck");
        int x = (int) (Math.random() * 180 + 550);
        int y = (int) (Math.random() * 246 + 208);
        sI.mouse.move(x,y);
        sI.sleep(700+sI.random(500));
    }



    public void hoverNextTarget () {
        NPC npc = sI.getNpcs().closest((Filter<NPC>) npc1 -> npc1 != null && npc1.getName().equals("Chicken") && npc1.exists() && sI.getMap().canReach(npc1) && npc1.getHealthPercent() > 0 && npc1.getInteracting() == null && !npc1.isUnderAttack());
        npc.hover();
    }
}
