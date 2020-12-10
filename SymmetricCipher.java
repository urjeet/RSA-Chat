/**  Interface for symmetric ciphers
 * Implemented by both Additive and Substitution classes.
 */
public interface SymmetricCipher
{
	// Return an array of bytes that represent the key for the cipher
	public byte [] getKey();

	// Encode the string using the key and return the result as an array of
	// bytes.  Note that you will need to convert the String to an array of bytes
	// prior to encrypting it. String S could have an arbitrary
	// length, so the cipher has to "wrap" when encrypting.
	public byte [] encode(String S);

	// Decrypt the array of bytes and generate and return the corresponding String.
	public String decode(byte [] bytes);
}
