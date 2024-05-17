package utils.structures;

//todo check out
//import java.security.PrivateKey;

/**
 * A wrapper class for a Secret Key instance.
 * An instance of the class is needed for encryption and decryption only.
 * Holds:
 *      a single polynomial
 */
public class SecretKey {

    private Polynomial secret;

    public SecretKey(Polynomial secret) {
        this.secret = secret;
    }

    public Polynomial getSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return "SecretKey = (" + secret +
                " )";
    }
}
