/**
 * Additive Encryption Process
 * @author Urjeet Deshmukh - November 18th, 2020
 */

import java.io.*;
import java.util.*;

public class Additive implements SymmetricCipher
{
	private byte[] key; 

	public Additive()	// Constructor with no parameters
	{
		this.key = new byte[128];	// Create array of bytes
		Random random = new Random();	// Create Random generator
		random.nextBytes(key);	// Store random 128 byte additive key in array of bytes
	}	

	public Additive(byte[] key)	// Constructor that takes byte array as parameter
	{	
		if(key.length != 128){
			return;
		}else{
			this.key = key.clone();	// Use byte array parameter as its key
		}	
	}

	@Override
	public byte[] encode(String strParam)
	{
		byte[] stringByte = strParam.getBytes();	// Convert string parameter to array of bytes

		for(int i = 0; i < stringByte.length; i++){	// Add corresponding byte of the key to each index in the array of bytes
			stringByte[i] = (byte)(stringByte[i] + key[i % key.length]);	// If at end of key, start at front again
		}

		return stringByte;	// Return encrypted array of bytes
	}

	@Override
	public String decode(byte[] byteArray)
	{
		for(int i = 0; i < byteArray.length; i++){	// Subtract corresponding byte of the key from each index of the array of bytes
			byteArray[i] = (byte)(byteArray[i] - key[i % key.length]);	// If at end of key, start at front again
		}

		String stringDecode = new String(byteArray);	// Convert byte array to String

		return stringDecode;
	}

	@Override
	public byte[] getKey()	// Used in SecureChatClient
	{
		return key;
	}
}