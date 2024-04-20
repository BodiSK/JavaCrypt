package utils.structures;


/**
 * A wrapper class for a Secret Key instance
 *
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


//import java.security.PrivateKey;
