package ar.com.kriche.minesweeper.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Quick and dirty implementation for the sake of simplicity.
 * Not to be used in a real project:
 * Random Number Generation is too Important to be Left to Chance.
 *
 * @Author Kriche 2020
 */
@Deprecated
public class RandomHelper {

    /**
     * @param numberOfBooleansTrue must be greater than or equal to 0.
     * @param totalSize            must be must be greater than or equal to numberOfBooleansTrue.
     * @return a shuffled list of size totalSize having exactly numberOfBooleanTrue elements true and the rest false.
     */
    public static List<Boolean> shuffledBooleans(int numberOfBooleansTrue, int totalSize) {

        List<Boolean> booleans = new ArrayList<>(totalSize);

        while (totalSize > 0 && numberOfBooleansTrue > 0) {
            booleans.add(true);
            totalSize--;
            numberOfBooleansTrue--;
        }

        while (totalSize > 0) {
            booleans.add(false);
            totalSize--;
        }

        Collections.shuffle(booleans);

        return booleans;
    }
}
