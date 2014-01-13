package it.torvergata.mp.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Base64;
import android.util.Log;

public class CryptoSha256 {
	
	public CryptoSha256(){
		
	}
	
	public String encrypt (String password) throws NoSuchAlgorithmException {
		
		    MessageDigest digest=null;
		    String hash = null;
		    try {
		        digest = MessageDigest.getInstance("SHA-256");
		        digest.update(password.getBytes());

		        hash = bytesToHexString(digest.digest());

		        Log.i("Password Crittograta", hash);
		       
		    } catch (NoSuchAlgorithmException e1) {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
		    }

			return hash;
	}

	private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
