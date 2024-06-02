package utils.structures;


import java.io.Serializable;

/**
 * A wrapper class for a Public Key instance.
 * An instance of the class is needed whenever there is an operation that needs to be performed on the ciphertext.
 * Holds:
 *      a tuple of polynomials corresponding to the two parts of the public key
 */
public class PublicKey implements Serializable {

    private Polynomial pk0;
    private Polynomial pk1;

    public PublicKey(Polynomial pk0, Polynomial pk1) {
        this.pk0 = pk0;
        this.pk1 = pk1;
    }

    public Polynomial getPk0() {
        return pk0;
    }

    public Polynomial getPk1() {
        return pk1;
    }

    @Override
    public String toString() {
        return "PublicKey " +
                 pk0 +
                ", " + pk1 +
                ')';
    }
}