package ar.com.kriche.minesweeper.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for SimpleTimeTracker.
 * <p>
 * NOTE ideally use <code>Clock</code> or some other mock instead of depending on <code>Thread.sleep</code>.
 *
 * @Author Kriche 2020
 */
public class SimpleTimeTrackerTest {

    private static int tick = 1000;
    private static int epsilon = 100;

    private static boolean withinEpsilon(long value) {
        return Math.abs(value) <= epsilon;
    }

    @Test
    public void givenTimerWhenStoppedThenDoNotTrackTime() throws InterruptedException {
        SimpleTimeTracker timeTracker = new SimpleTimeTracker();
        timeTracker.stop();
        Thread.sleep(tick);
        assertEquals(0, timeTracker.getAccumulatedTimeMilliseconds());
    }

    @Test
    public void givenTimerWhenStartedThenTrackTime() throws InterruptedException {
        SimpleTimeTracker timeTracker = new SimpleTimeTracker();
        timeTracker.start();
        Thread.sleep(tick);
        assertTrue(withinEpsilon(timeTracker.getAccumulatedTimeMilliseconds() - tick));
    }

    @Test
    public void givenTimerWhenStartingAndStoppingThenTrackTimeOnlyDuringStartedIntervals() throws InterruptedException {

        SimpleTimeTracker timeTracker = new SimpleTimeTracker();
        long expectedTime;

        timeTracker.start();
        Thread.sleep(tick);
        expectedTime = tick;
        assertTrue(withinEpsilon(timeTracker.getAccumulatedTimeMilliseconds() - expectedTime));
        Thread.sleep(tick);
        expectedTime += tick;
        assertTrue(withinEpsilon(timeTracker.getAccumulatedTimeMilliseconds() - expectedTime));
        timeTracker.stop();
        Thread.sleep(tick);
        assertTrue(withinEpsilon(timeTracker.getAccumulatedTimeMilliseconds() - expectedTime));

        // start stop can consequently be called:
        timeTracker.stop();
        timeTracker.stop();
        timeTracker.start();
        timeTracker.start();
        Thread.sleep(tick);
        expectedTime += tick;
        timeTracker.stop();
        timeTracker.stop();
        timeTracker.start();
        Thread.sleep(tick);
        expectedTime += tick;
        timeTracker.start();
        timeTracker.stop();
        timeTracker.start();
        assertTrue(withinEpsilon(timeTracker.getAccumulatedTimeMilliseconds() - expectedTime));

    }

}