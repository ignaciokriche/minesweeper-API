package ar.com.kriche.minesweeper.util;

import java.util.Random;

/**
 * Quick and dirty implementation for the sake of simplicity.
 * Not to be used in a real project:
 * Random Number Generation is too Important to be Left to Chance.
 *
 * @Author Kriche 2020
 */
@Deprecated
public class RandomHelper {

    private static final Random rnd = new Random();

    public static boolean nextBoolean(float probability) {
        return rnd.nextFloat() < probability;
    }
}
