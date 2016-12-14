package implementations.util.IoT;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptographyUtils {

	private static String clusterPassword;
	
    public static byte[] crypt(byte[] msg, byte[] iv){
    	try{
    		MessageDigest sha = MessageDigest.getInstance("SHA-256");
    		byte[] key = sha.digest(clusterPassword.getBytes("UTF-8"));
    		key = Arrays.copyOf(key, 16);
    		SecretKeySpec skc = new SecretKeySpec(key, "AES");
    		Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
    		c.init(Cipher.ENCRYPT_MODE, skc, new IvParameterSpec(iv));
    		byte[] original = c.doFinal(msg);
    		return original;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    }	
	
	public static byte[] decrypt(byte[] cryped, byte[] iv){		
		try{
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] key = sha.digest(clusterPassword.getBytes("UTF-8"));
			key = Arrays.copyOf(key, 16);		
			SecretKeySpec skc = new SecretKeySpec(key, "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.DECRYPT_MODE, skc, new IvParameterSpec(iv));
			byte[] original = c.doFinal(cryped);
			return original;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] generateIV(){
		byte iv[] = new byte[16];
		try{
			SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
			randomSecureRandom.nextBytes(iv);
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return iv;
	}
	
	public static byte[] generateRegitrationKey(byte[] msg, byte[] iv){
		try{
			MessageDigest sha = MessageDigest.getInstance("SHA-256");			
			byte[] key = sha.digest(clusterPassword.getBytes("UTF-8"));
			key = Arrays.copyOfRange(key, 16, 32);						
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec macKey = new SecretKeySpec(key, "HmacSHA256");
			mac.init(macKey);
			mac.update(iv);
			mac.update(msg);
			return mac.doFinal();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;		
	}

	public static String getClusterPassword() {
		return clusterPassword;
	}

	public static void setClusterPassword(String clusterPassword) {
		CryptographyUtils.clusterPassword = clusterPassword;
	}
}
