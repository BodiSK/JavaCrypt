package tests.utils.optimizations;

import org.junit.Before;
import org.junit.Test;
import utils.optimizations.NumberTheoreticTransform;
import utils.operations.AlgebraicOperations;
import java.math.BigInteger;
import java.util.Arrays;
import static org.junit.Assert.*;

public class TestNumberTheoreticTransform {

    private NumberTheoreticTransform transform;
    private BigInteger mod;
    private BigInteger[] coefficients;

    @Before
    public void setUp() {
        mod = new BigInteger("73");
        transform = new NumberTheoreticTransform(new BigInteger("4"), mod);

        coefficients = new BigInteger[4];
        Arrays.fill(coefficients, BigInteger.ZERO);
        coefficients[0] = new BigInteger("0");
        coefficients[1] = new BigInteger("1");
        coefficients[2] = new BigInteger("4");
        coefficients[3] = new BigInteger("5");
    }

    @Test
    public void testFindRootOfUnity() {
        BigInteger rootOfUnity = AlgebraicOperations.findRootOfUnity(new BigInteger("8"), mod);
        assertNotNull(rootOfUnity);
        assertEquals(rootOfUnity, new BigInteger("10"));
    }

    @Test
    public void testRunNumberTheoreticTransformForwardLength() {
        BigInteger[] forward = transform.runNumberTheoreticTransform(coefficients, transform.getPowersOfRootOfUnity());

        assertNotNull(forward);
        assertEquals(4, forward.length);

        assertEquals(new BigInteger("10"), forward[0]);
        assertEquals(new BigInteger("34"), forward[1]);
        assertEquals(new BigInteger("71"), forward[2]);
        assertEquals(new BigInteger("31"), forward[3]);
    }

    @Test
    public void testRunNumberTheoreticTransformInverse() {
        BigInteger[] forward = transform.runNumberTheoreticTransform(coefficients, transform.getPowersOfRootOfUnity());

        BigInteger[] scaled = Arrays.stream(forward)
                .map(el -> el.multiply(BigInteger.valueOf(-18).mod(mod)))
                .toArray(BigInteger[]::new);

        BigInteger[] inverse = transform.runNumberTheoreticTransform(scaled, transform.getInversePowersOfRootsOfUnity());

        assertNotNull(inverse);
        assertEquals(4, inverse.length);

         assertEquals(new BigInteger("0"), inverse[0]);
         assertEquals(new BigInteger("1"), inverse[1]);
         assertEquals(new BigInteger("4"), inverse[2]);
         assertEquals(new BigInteger("5"), inverse[3]);
    }
}
