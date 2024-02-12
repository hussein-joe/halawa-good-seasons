package com.halawa.goodseasons.common.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.jboss.security.Base64Encoder;

import com.halawa.common.exception.HalawaErrorCode;
import com.halawa.common.exception.HalawaSystemException;


public class PasswordEncryption {

    public static String encryptPassword( String passwd ) {
        try {
        	DESKeySpec keySpec = new DESKeySpec("YourSecr".getBytes("UTF8")); 
        	SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        	SecretKey key = keyFactory.generateSecret(keySpec);
        	
        	byte[] cleartext = passwd.getBytes("UTF8");      

        	Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
        	cipher.init(Cipher.ENCRYPT_MODE, key);
        	String encrypedPwd = Base64Encoder.encode(cipher.doFinal(cleartext));
        	return encrypedPwd;
        }
        catch (Exception exp) {
        	throw new HalawaSystemException(HalawaErrorCode.GENERIC_ERROR, "Failed to encrypt the password",
    				exp);
        }
    }
}