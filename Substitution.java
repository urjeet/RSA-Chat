/**
 * Substitution Encryption Process
 * @author Urjeet Deshmukh - November 18th, 2020
 */

import java.io.*;
import java.util.*;

public class Substitution implements SymmetricCipher
{
    private byte[] key;
    private byte[] inverseMap;

    public Substitution() // Constructor with no parameters
    {
        key = new byte[256];    // Initialize two byte arrays to size 256
        inverseMap = new byte[256]; // Inverse mapping array for cipher

        ArrayList<Byte> byteList = new ArrayList<Byte>();   // ArrayList of bytes to randomize later

        for(int i = 0; i < 256; i++){   // Fill ArrayList with 0-255 bytes
            byteList.add((byte)i);
        }

        Collections.shuffle(byteList, new Random());  // Randomize the list

        for(int i = 0; i < 256; i++){   // Fill key with randomized ArrayList
            key[i] = byteList.get(i);
            inverseMap[key[i] & 0xFF] = (byte)i;    // Generate inverse key map
        }
    }

    public Substitution(byte[] key) // Constructor that takes byte array as parameter
    {
        if (key.length != 256){
            return;
        }

        this.key = key.clone();   // Clone key
        inverseMap = new byte[256];
        // No randomizing needed 
        for(int i = 0; i < 256; i++){
            inverseMap[this.key[i] & 0xFF] = (byte)i;  // Copy transposed elements into inverse key map
        }
    }

    @Override
    public byte[] encode(String strParam)
    {
        byte[] stringByte = strParam.getBytes();   // Convert string parameter to array of bytes
        byte[] encodedByte = new byte[stringByte.length];  // Encoded byte array of length of stringByte used later

        for(int i = 0; i < stringByte.length; i++){    // Iterate through all of the bytes
            encodedByte[i] = key[stringByte[i] & 0xFF]; // Substitute the appropriate bytes from the key
        }

        return encodedByte;
    }

    @Override
    public String decode(byte[] byteArray)
    {
        byte[] decodedByte = new byte[byteArray.length];    // Decoded byte array to reverse substitution

        for(int i = 0; i < byteArray.length; i++){    // Iterate through all of the bytes 
            decodedByte[i] = inverseMap[byteArray[i] & 0xFF]; // Put in transposed key elements into decoded byte array
        }
        
        String stringDecode = new String(decodedByte);  // Convert byte array to String

        return stringDecode; // Return decoded bytes as String
    }

    @Override
    public byte[] getKey()  // Used in SecureChatClient
    {
        return key;
    }
}