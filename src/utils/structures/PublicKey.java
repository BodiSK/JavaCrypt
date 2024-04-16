package utils.structures;


/**
 * A wrapper class for a Public Key instance
 *
 */
public class PublicKey {

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

//TODO fix namings
//TODO there might be a need to add the modulus of the space from which the PubliKey is taken