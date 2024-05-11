package scheme.bfv;


import utils.structures.Polynomial;

import java.util.List;

/**
 * A class representing a relinearization key tuple.
 * It is used to perform the relinearization step that is necessary to prevent the exponential growth of the ciphertext.
 */
public class RelinearizationKeys {

    private int base;
    private List<List<Polynomial>> keys;

    public RelinearizationKeys(int base, List<List<Polynomial>> keys) {
        this.base = base;
        this.keys = keys;
    }

    public int getBase() {
        return base;
    }

    public List<List<Polynomial>> getKeys() {
        return keys;
    }
}
