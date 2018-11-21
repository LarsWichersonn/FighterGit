package utils;

import MainScript.MainHandler;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.function.BooleanSupplier;

/*
 * Credit to Explv.
 * See OSBOT 127193
 * https://osbot.org/forum/topic/127193-conditional-sleep-with-lambda-expressions/
 */
public final class Sleep extends ConditionalSleep {

    private final BooleanSupplier condition;

    public Sleep(final BooleanSupplier condition, final int timeout) {
        super(timeout);
        this.condition = condition;
    }

    public Sleep(final BooleanSupplier condition, final int timeout, final int interval) {
        super(timeout, interval);
        this.condition = condition;
    }

    public static void SleepNormal(int Mean, int SD, boolean DebugOnly){
        if (DebugOnly && !MainHandler.VERBOSE){
            return;
        }
        int Normalized = (int) new java.util.Random().nextGaussian();
        //returns abs in case you mess up and get <0.
        //also uses "ConditionalSleep" over normal sleep
        //to minimize baggage
        sleepUntil(() -> false, Math.abs(Normalized * SD + Mean));

        return;
    }

    @Override
    public final boolean condition() throws InterruptedException {
        return condition.getAsBoolean();
    }

    public static boolean sleepUntil(final BooleanSupplier condition, final int timeout) {
        return new Sleep(condition, timeout).sleep();
    }

    public static boolean sleepUntil(final BooleanSupplier condition, final int timeout, final int interval) {
        return new Sleep(condition, timeout, interval).sleep();
    }
}
