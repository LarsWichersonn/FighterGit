package Exchange;

import java.util.Random;

import org.osbot.rs07.api.def.ItemDefinition;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

public class GrandExchange {
    private final Script sI;
    private final Area GRAND_EXCHANGE_CENTER = new Area(3160, 3493, 3169, 3485);
    private RS2Widget collectWidget;
    private RS2Widget buyIconWidget1;
    private RS2Widget buyIconWidget2;
    private RS2Widget buyIconWidget3;
    private RS2Widget buyTypeTextWidget; // Text of which item you wanna buy (chatbox)
    private RS2Widget buyingItemTextWidget; // Text in buy screen (top of screen)
    private RS2Widget searchIndexWidget;
    private RS2Widget pricePerItemWidget;
    private RS2Widget setPriceButton;
    private RS2Widget setPriceText;
    private RS2Widget quantityItemWidget;
    private RS2Widget setAmountButton;
    private RS2Widget confirmButton;


    private final int[] buyOfferWidget = { 465, 7, 26 };
    private final int[] itemSelectWidget = { 465, 24, 21 };
    private final int[] preSlectionWidget = { 162, 38, 0 };
    private final int[] preNumberWidget = { 162, 32 };
    private final int[] searchTextWidget = { 162, 33 };
    private final int[] chatboxWidget = { 162, 42 };
    private final int[] priceWidget = { 465, 24, 39 };
    private final int[] amountWidget = { 465, 24, 32 };
    private final int[] setPriceWidget = { 465, 24, 12 };
    private final int[] setAmountWidget = { 465, 24, 7 };
    private final int[] confirmWidget = { 465, 24, 54 };
    private final int[] setAllWidget = { 465, 24, 6 };

    public GrandExchange(Script sI) {
        this.sI = sI;
    }

    public void setupParams () {
        collectWidget = sI.getWidgets().get(465,6,1);
        buyIconWidget1 = sI.getWidgets().get(465,7,26);
        buyIconWidget2 = sI.getWidgets().get(465,8,26);
        buyIconWidget3 = sI.getWidgets().get(465,9,26);
        buyTypeTextWidget = sI.getWidgets().get(162,45);
        buyingItemTextWidget = sI.getWidgets().get(465, 24,25);
        searchIndexWidget = sI.getWidgets().get(162,53, 0);
        pricePerItemWidget = sI.getWidgets().get(465,24,39);
        setPriceButton = sI.getWidgets().get(465,24,12);
        setPriceText = sI.getWidgets().get(162,45);
        quantityItemWidget = sI.getWidgets().get(465,24,32);
        setAmountButton = sI.getWidgets().get(465,24,7);
        confirmButton = sI.getWidgets().get(465,27,0);
    }



    public void openGE() {
        RS2Object geBooth = sI.getObjects().closest("Grand Exchange booth");
        NPC exchangeWorker = sI.getNpcs().closest("Grand Exchange Clerk");

        int random = new Random().nextInt(10);
        if (geBooth != null && random < 5) {
            geBooth.interact("Exchange");
            new ConditionalSleep(2500, 3000) {
                @Override
                public boolean condition() {
                    return sI.getGrandExchange().isOpen();
                }
            }.sleep();
        }
        if (exchangeWorker != null && random >= 5) {
            exchangeWorker.interact("Exchange");
            new ConditionalSleep(2500, 3000) {
                @Override
                public boolean condition() {
                    return sI.getGrandExchange().isOpen();
                }
            }.sleep();
        }
    }

    public void collectItems(boolean bank) {
        if (sI.getGrandExchange().isOpen() && collectWidget != null) {
            if (bank) {
                collectWidget.interact("Collect to bank");
            } else {
                collectWidget.interact("Collect to inventory");
            }
            new ConditionalSleep(2500, 3000) {
                @Override
                public boolean condition() {
                    return !collectWidget.isVisible();
                }
            }.sleep();
        }
    }

    public void createBuyOffer(String itemName, int price, int amount) {
        if (sI.getGrandExchange().isOpen()) {
            if (!sI.getGrandExchange().isBuyOfferOpen()) {
                initBuyOffer();
            }
            else if (!buyingItemTextWidget.getMessage().equals(itemName)) {
                selectBuyItem(itemName);
            }
            else if (!pricePerItemWidget.getMessage().replace((","),"").equals(price + " coins")) {
                sI.log(!pricePerItemWidget.getMessage().equals(price + " coins"));
                sI.log(pricePerItemWidget.getMessage() + price + " coins");
                setPrice(price);
            }
            else if (!quantityItemWidget.getMessage().replace((","),"").equals(amount + "")) {
                setAmount(amount);
            }
            else if (confirmButton != null && buyingItemTextWidget.getMessage().equals(itemName) && pricePerItemWidget.getMessage().replace((","),"").equals(price + " coins") && quantityItemWidget.getMessage().replace((","),"").equals(amount + "")) {
                confirmButton.interact();
                new ConditionalSleep(2500, 3000) {
                    @Override
                    public boolean condition() {
                        return !sI.getGrandExchange().isBuyOfferOpen();
                    }
                }.sleep();
            }
        }
        setupParams();
    }

    private void initBuyOffer() {
        if (sI.getGrandExchange().isOpen() && !sI.getGrandExchange().isBuyOfferOpen()
                && (buyIconWidget1 != null || buyIconWidget2 != null || buyIconWidget3 != null)) {
            sI.log("init buy");
            if (buyIconWidget1 != null && buyIconWidget1.isVisible()) {
                sI.log("init buy 1");
                buyIconWidget1.interact();
            } else if (buyIconWidget2 != null && buyIconWidget2.isVisible()) {
                sI.log("init buy 2");
                buyIconWidget2.interact();
            } else if (buyIconWidget3 != null && buyIconWidget3.isVisible()) {
                sI.log("init buy 3");
                buyIconWidget3.interact();
            } else if (collectWidget.isVisible()) {
                collectWidget.interact();
            }
            new ConditionalSleep(2500, 3000) {
                @Override
                public boolean condition() {
                    return sI.getGrandExchange().isBuyOfferOpen() && buyTypeTextWidget.isVisible();
                }
            }.sleep();
        }
        setupParams();
    }

    private void selectBuyItem(String itemName) {
        if (sI.getGrandExchange().isBuyOfferOpen() && !buyingItemTextWidget.getMessage().equals(itemName) && buyingItemTextWidget.isVisible()) {
            if (!enteredText().equals(itemName)) {
                if (buyTypeTextWidget.getMessage().length() == 47) {
                    sI.getKeyboard().typeString(itemName, false);
                    new ConditionalSleep(3000, 3500) {
                        @Override
                        public boolean condition() {
                            return enteredText().equals(itemName);
                        }
                    }.sleep();
                } else {
                    while (!itemName.contains(enteredText())) {
                        sI.getKeyboard().typeKey('\b');
                    }
                }
            } else {
                // iterate through index children and find the itemName
                if (searchIndexWidget != null && searchIndexWidget.isVisible()) {
                    searchIndexWidget.interact();
                    new ConditionalSleep(2500, 3000) {
                        @Override
                        public boolean condition() {
                            return buyingItemTextWidget.equals(itemName);
                        }
                    }.sleep();
                }
            }
        }
        setupParams();
    }

    public void createSellOffer(String itemName, int price, int amount) {
        if (sI.getGrandExchange().isOpen()) {
            if (!sI.getGrandExchange().isSellOfferOpen()) {
                initSellOffer(itemName);
            } else if (!pricePerItemWidget.getMessage().equals(price + " coins")) {
                setPrice(price);
            } else if (!quantityItemWidget.getMessage().equals(amount + "")) {
                setAmount(amount);
            }
            if (confirmButton != null && pricePerItemWidget.getMessage().equals(price + " coins") && quantityItemWidget.getMessage().equals(amount + "")) {
                confirmButton.interact();
                new ConditionalSleep(2500, 3000) {
                    @Override
                    public boolean condition() {
                        return !sI.getGrandExchange().isSellOfferOpen();
                    }
                }.sleep();
            }
        }
        setupParams();
    }

    private void initSellOffer(String itemName) {
        setupParams();
        if (sI.getGrandExchange().isOpen() && !sI.getGrandExchange().isSellOfferOpen()) {
            if (sI.getInventory().contains(itemName)) {
                Item sellItem = sI.getInventory().getItem(itemName);
                sellItem.interact();
            }
            new ConditionalSleep(2500, 3000) {
                @Override
                public boolean condition() {
                    return sI.getGrandExchange().isSellOfferOpen();
                }
            }.sleep();
        }
    }

    public boolean isCollectAvailable() {
        setupParams();
        if (collectWidget != null) {
            if (collectWidget.isVisible()) {
                return true;
            }
        }
        return false;
    }

    private void setPrice(int itemPrice) {
        if (sI.getGrandExchange().isOpen() && sI.getGrandExchange().isOfferScreenOpen()) {
            if (pricePerItemWidget.getMessage() != (itemPrice + " coins")) {
                if (itemPrice != 0 && setPriceButton != null && !setPriceText.isVisible()) {
                    setPriceButton.interact();
                    new ConditionalSleep(2500, 3000) {
                        @Override
                        public boolean condition() {
                            return setPriceText.isVisible();
                        }
                    }.sleep();
                }
            }
            sI.log(pricePerItemWidget.getMessage() + " Price item widget text");
            sI.log(setPriceText.getMessage().replace(("*"),"").equals((String.valueOf(itemPrice) )) + " Price setprice");
            sI.log(setPriceText.getMessage() + " Price text message");
            if (!pricePerItemWidget.getMessage().equals(itemPrice +" coins") && setPriceText.isVisible() && !setPriceText.getMessage().replace(("*"),"").equals((String.valueOf(itemPrice)))) {
                sI.getKeyboard().typeString(String.valueOf(itemPrice), true);
                new ConditionalSleep(2500, 3000) {
                    @Override
                    public boolean condition() {
                        sI.log("Sleep till price change");
                        sI.log(pricePerItemWidget.getMessage() + (itemPrice + " coins") + "hoi");
                        return pricePerItemWidget.getMessage().equals(itemPrice +" coins");
                    }
                }.sleep();
            }

        }
        setupParams();
    }

    private void setAmount(int itemAmount) {
        if (sI.getGrandExchange().isOpen() && sI.getGrandExchange().isOfferScreenOpen()) {
            if (quantityItemWidget.getMessage() != null && !quantityItemWidget.getMessage().equals(itemAmount + "")) {
                if (setAmountButton != null && !setPriceText.isVisible()) {
                    setAmountButton.interact();
                    new ConditionalSleep(2500, 3000) {
                        @Override
                        public boolean condition() {
                            return setPriceText.isVisible();
                        }
                    }.sleep();
                }
            }
            if (!quantityItemWidget.getMessage().equals( itemAmount + "") && setPriceText.isVisible()
                    && !setPriceText.getMessage().replace(("*"),"").equals((String.valueOf(itemAmount)))) {
                sI.getKeyboard().typeString(String.valueOf(itemAmount), true);
                new ConditionalSleep(2500, 3000) {
                    @Override
                    public boolean condition() {
                        return quantityItemWidget.getMessage().equals( itemAmount + "");
                    }
                }.sleep();
            }
        }
        setupParams();
    }

    private String enteredText() {
        String input = buyTypeTextWidget.getMessage().substring(46).replace("*", "");
        return input;
    }

    private int getPrice() {
        if (priceText() != null && priceText().getMessage() != null) {
            return Integer.parseInt(priceText().getMessage().replaceAll("[\\D]", ""));
        }
        return -1;
    }

    private int getAmount() {
        if (amountText() != null && amountText().getMessage() != null) {
            return Integer.parseInt(amountText().getMessage().replaceAll("[\\D]", ""));
        }
        return -1;
    }

    private String getItem() {
        if (itemSelection() != null) {
            return getName(itemSelection().getItemId());
        }
        return "Invalid";
    }

    private RS2Widget buyOfferSlotOne() {
        RS2Widget widget = sI.getWidgets().get(buyOfferWidget[0], buyOfferWidget[1], buyOfferWidget[2]);
        if (widget != null) {
            return widget;
        }
        return null;
    }

    private RS2Widget confirmButton() {
        RS2Widget widget = sI.getWidgets().get(confirmWidget[0], confirmWidget[1], confirmWidget[2]);
        if (widget != null) {
            return widget;
        }
        return null;
    }

    private RS2Widget preIndex() {
        RS2Widget widget = sI.getWidgets().get(preSlectionWidget[0], preSlectionWidget[1], preSlectionWidget[2]);
        if (widget != null) {
            return widget;
        }
        return null;
    }

    private RS2Widget itemSelection() {
        RS2Widget widget = sI.getWidgets().get(itemSelectWidget[0], itemSelectWidget[1], itemSelectWidget[2]);
        if (widget != null) {
            return widget;
        }
        return null;
    }

    private RS2Widget priceButton() {
        RS2Widget widget = sI.getWidgets().get(setPriceWidget[0], setPriceWidget[1], setPriceWidget[2]);
        if (widget != null) {
            return widget;
        }
        return null;
    }

    private RS2Widget priceText() {
        RS2Widget widget = sI.getWidgets().get(priceWidget[0], priceWidget[1], priceWidget[2]);
        if (widget != null) {
            return widget;
        }
        return null;
    }

    private RS2Widget chatboxText() {
        RS2Widget widget = sI.getWidgets().get(chatboxWidget[0], chatboxWidget[1]);
        if (widget != null) {
            return widget;
        }
        return null;
    }

    private RS2Widget preNumber() {
        RS2Widget widget = sI.getWidgets().get(preNumberWidget[0], preNumberWidget[1]);
        if (widget != null) {
            return widget;
        }
        return null;
    }

    private RS2Widget searchText() {
        RS2Widget widget = sI.getWidgets().get(searchTextWidget[0], searchTextWidget[1]);
        if (widget != null) {
            return widget;
        }
        return null;
    }

    private RS2Widget amountButton() {
        RS2Widget widget = sI.getWidgets().get(setAmountWidget[0], setAmountWidget[1], setAmountWidget[2]);
        if (widget != null) {
            return widget;
        }
        return null;
    }

    private RS2Widget amountText() {
        RS2Widget widget = sI.getWidgets().get(amountWidget[0], amountWidget[1], amountWidget[2]);
        if (widget != null) {
            return widget;
        }
        return null;
    }

    private String getName(int id) {
        ItemDefinition itemDef = ItemDefinition.forId(id);
        if (itemDef != null && itemDef.getName() != null) {
            return itemDef.getName();
        }
        return null;
    }

    private RS2Widget allButton() {
        RS2Widget widget = sI.getWidgets().get(setAllWidget[0], setAllWidget[1], setAllWidget[2]);
        if (widget != null) {
            return widget;
        }
        return null;
    }

}
