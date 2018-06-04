/**
 * 
 */
package gov.ca.calpers.psr.automation;

/**
 * @author burban
 *
 */
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * The Class TripleDES.
 */
public class TripleDES {

    /** The Constant UNICODE_FORMAT. */
    private static final String UNICODE_FORMAT = "UTF8";
    
    /** The Constant DESEDE_ENCRYPTION_SCHEME. */
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    
    /** The ks. */
    private KeySpec ks;
    
    /** The skf. */
    private SecretKeyFactory skf;
    
    /** The cipher. */
    private Cipher cipher;
    
    /** The array bytes. */
    byte[] arrayBytes;
    
    /** The my encryption key. */
    private String myEncryptionKey;
    
    /** The my encryption scheme. */
    private String myEncryptionScheme;
    
    /** The key. */
    SecretKey key;

    /**
     * Instantiates a new triple des.
     *
     * @throws Exception the exception
     */
    public TripleDES() throws Exception {
        myEncryptionKey = "SheSellsSeaShellsByTheSeaShore";
        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
        ks = new DESedeKeySpec(arrayBytes);
        skf = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = Cipher.getInstance(myEncryptionScheme);
        key = skf.generateSecret(ks);
    }


    /**
     * Encrypt.
     *
     * @param unencryptedString the unencrypted string
     * @return the string
     */
    public String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.encodeBase64(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }


    /**
     * Decrypt.
     *
     * @param encryptedString the encrypted string
     * @return the string
     */
    public String decrypt(String encryptedString) {
        String decryptedText=null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decodeBase64(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText= new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }


}