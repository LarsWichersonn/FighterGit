package Exchange;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.Script;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ExchangeHandler {
    private Script sI;
    public Area geArea;
    public GrandExchange grandExchange;

    public ExchangeHandler(Script sI) {
        this.sI = sI;
        grandExchange = new GrandExchange(sI);
        grandExchange.setupParams();
        geArea = new Area(3160, 3493, 3169, 3485);
    }

    public int buyinsForLowMagic() {
        try {
            if (geArea.contains(sI.myPlayer())) {
                sI.log("ge");
                if (grandExchange.isCollectAvailable()) {
                    sI.log("collect available");
                    grandExchange.collectItems(false);
                } else if (sI.inventory.contains("Coins") && !sI.inventory.contains("Staff of air") && sI.grandExchange.isOpen()) {
                    sI.log("buying Staff of air");
                    grandExchange.createBuyOffer("Staff of air", 2100, 1);
                } else if (sI.inventory.contains("Coins") && !sI.inventory.contains("Fire rune") && sI.grandExchange.isOpen()) {
                    sI.log("buying fire rune");
                    grandExchange.createBuyOffer("Fire rune", 8, 3000);
                } else if (sI.inventory.contains("Coins") && !sI.inventory.contains("Amulet of magic") && sI.grandExchange.isOpen()) {
                    grandExchange.createBuyOffer("Amulet of magic", 1000, 1);
                } else if (sI.inventory.contains("Coins") && !sI.inventory.contains("Blue wizard hat") && sI.grandExchange.isOpen()) {
                    grandExchange.createBuyOffer("Blue wizard hat", 1000, 1);
                } else if (sI.inventory.contains("Coins") && !sI.inventory.contains("Zamorak monk bottom") && sI.grandExchange.isOpen()) {
                    grandExchange.createBuyOffer("Zamorak monk bottom", 3000, 1);
                } else if (sI.inventory.contains("Coins") && !sI.inventory.contains("Blue wizard robe") && sI.grandExchange.isOpen()) {
                    grandExchange.createBuyOffer("Blue wizard robe", 4000, 1);
                } else if (sI.inventory.contains("Coins") && !sI.inventory.contains("Mind rune") && sI.grandExchange.isOpen()) {
                    sI.log("buying mind rune");
                    grandExchange.createBuyOffer("Mind rune", 6, 1500);
                } else if (sI.grandExchange.isOpen() && sI.inventory.contains("Oak logs")) {
                    sI.log("selling oak logs");
                    grandExchange.createSellOffer("Oak logs", 45, (int) sI.inventory.getAmount("Oak logs"));
                } else if(!sI.grandExchange.isOpen() && sI.inventory.contains("Oak logs") && !sI.inventory.contains("Bronze axe")) {
                    sI.log("open ge");
                    grandExchange.openGE();
                } else if (sI.inventory.contains("Bronze axe") || !sI.inventory.contains("Staff of air") && !sI.inventory.contains("Coins")) {
                    sI.log("Open bank");
                    if (!sI.bank.isOpen()) {
                        sI.bank.open();
                        return sI.random(600,850);
                    } else {
                        if (sI.inventory.contains("Bronze axe")) {
                            sI.bank.depositAll();
                        }else if (sI.bank.contains("Oak logs")) {
                            if (sI.getBank().getWithdrawMode().equals(Bank.BankMode.WITHDRAW_NOTE)){ //True if bank mode is enabled
                                sI.bank.withdrawAll("Oak logs");
                            } else sI.getBank().enableMode(Bank.BankMode.WITHDRAW_NOTE); // else enable
                        }
                    }
                }
            } else {
                sI.walking.webWalk(geArea);
            }
        } catch (Exception ie) {
            final Writer result= new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            ie.printStackTrace(printWriter);
            sI.log(result.toString());
        }
        return sI.random(600,850);
    }

    public void buyinsForCrafting() {
        if (geArea.contains(sI.myPlayer())) {

        } else {
            sI.walking.webWalk(geArea);
        }
    }

    public void buyinsForRange() {
        if (geArea.contains(sI.myPlayer())) {

        } else {
            sI.walking.webWalk(geArea);
        }
    }

}
