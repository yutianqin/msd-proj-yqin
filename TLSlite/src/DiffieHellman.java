import java.math.BigInteger;
import java.util.Random;

public class DiffieHellman
{
    static BigInteger smallPrime = new BigInteger( String.valueOf(10) );

    // 2048 bit Diffie-Hellman group (g and N)
    static BigInteger bigPrime = new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1" +
                                                    "29024E088A67CC74020BBEA63B139B22514A08798E3404DD" +
                                                    "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245" +
                                                    "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED" +
                                                    "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D" +
                                                    "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F" +
                                                    "83655D23DCA3AD961C62F356208552BB9ED529077096966D" +
                                                    "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B" +
                                                    "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9" +
                                                    "DE2BCBF6955817183995497CEA956AE515D2261898FA0510" +
                                                    "15728E5A8AACAA68FFFFFFFFFFFFFFFF",16 );

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Generate a private secret of the value in between: 0-bigPrime
     * @return private secret which is a big integer
     */
    public static BigInteger privateSecret()
    {
        Random rand = new Random();
        return new BigInteger( bigPrime.bitLength(),rand );
    }


    /**
     * Generate the Diffie-Hellman public key
     * @return public key
     */
    public static BigInteger DH_PublicKey( BigInteger privateSecret )
    {
        return smallPrime.modPow( privateSecret, bigPrime );
    }


    /**
     * 1. received PublicKey to the power of the privateKey
     * 2. the result above mod the big prime
     * basically: Math.pow(G,privateKey) % N
     * @param theirPublicKey
     * @param myPrivateSecret
     * @return shared secret key
     */
    public static BigInteger sharedSecretKey( BigInteger theirPublicKey, BigInteger myPrivateSecret )
    {
        return theirPublicKey.modPow( myPrivateSecret, bigPrime );
    }
}
