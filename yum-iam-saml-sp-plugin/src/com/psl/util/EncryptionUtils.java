package com.psl.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class EncryptionUtils {

	private static byte[] keyBytes;
	private static SecretKeySpec key;

	private static Cipher cipher;

	static {
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			keyBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
					0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10,
					0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
			key = new SecretKeySpec(keyBytes, "AES");

			cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} finally {

		}
	}

	public static String encryptPassword(String password) {
		try {
			byte[] input = password.getBytes();
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
			int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
			ctLength += cipher.doFinal(cipherText, ctLength);
			return new String(Base64.encodeBase64(cipherText), "UTF-8");

		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (ShortBufferException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String decryptPassword(String cipherText1) {
		try {
			byte[] cipherText = Base64.decodeBase64(cipherText1);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] plainText = new byte[cipher.getOutputSize(cipherText.length)];
			int ptLength = cipher.update(cipherText, 0, cipherText.length,
					plainText, 0);
			ptLength += cipher.doFinal(plainText, ptLength);
			return new String(plainText);

		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (ShortBufferException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;

	}

}
