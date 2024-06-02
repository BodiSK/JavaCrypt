package scheme.bfv;


import utils.structures.Polynomial;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * A class representing a relinearization key tuple.
 * It is used to perform the relinearization step that is necessary to prevent the exponential growth of the ciphertext.
 */
public class RelinearizationKeys implements Serializable {

    private BigInteger base;
    private List<List<Polynomial>> keys;

    public RelinearizationKeys(BigInteger base, List<List<Polynomial>> keys) {
        this.base = base;
        this.keys = keys;
    }

    public BigInteger getBase() {
        return base;
    }

    public List<List<Polynomial>> getKeys() {
        return keys;
    }
}
