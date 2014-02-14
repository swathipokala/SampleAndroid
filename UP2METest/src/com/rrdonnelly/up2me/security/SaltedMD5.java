package com.rrdonnelly.up2me.security;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class SaltedMD5
{
    public static String getUserToken(String passwordToHash, String salt)
    {
        String generatedToken = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            //md.update(salt.getBytes());
            try {
				md.update((passwordToHash + salt).getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //Get the hash's bytes 
            //byte[] bytes = md.digest(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append( Integer.toHexString((0x000000ff & bytes[i]) | 0xffffff00).substring(6));
            }
            //Get complete hashed password in hex format
            generatedToken = sb.toString();
            
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedToken;
    }
     
    //Add salt
    public static String getSalt() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        //Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        sr.nextBytes(salt);
        //return salt
        return salt.toString();
    }
}