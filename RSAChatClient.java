/** Primitive chat client.
 * This client connects to a server so that messages can be typed and forwarded
 * to all other clients.
 * Adapted from Dr. John Ramirez's CS1501
 *@author Urjeet Deshmukh, November 18th 2020
 */

import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.*;
import java.text.*;



public class RSAChatClient extends JFrame implements Runnable, ActionListener {

    public static final int PORT = 4444;

    ObjectOutputStream myWriter;
    ObjectInputStream myReader;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
	  Socket connection;
    SymmetricCipher cipher;
    String encryptionType; 
    static int toExit = 0;

    public RSAChatClient()
    {
      try{

        myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
        serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
        InetAddress addr = InetAddress.getByName(serverName);
        connection = new Socket(addr, PORT);   // Connect to server with new Socket

       /* myReader =
             new BufferedReader(
                 new InputStreamReader(
                     connection.getInputStream()));   // Get Reader and Writer

        myWriter =
             new PrintWriter(
                 new BufferedWriter(
                     new OutputStreamWriter(connection.getOutputStream())), true);

        myWriter.println(myName);   // Send name to Server.  Server will need
                                    // this to announce sign-on and sign-off
                                    // of clients
        */

        myWriter = new ObjectOutputStream(connection.getOutputStream());  // Creates ObjectOutputStream on Socket
        myWriter.flush(); // Calls flush() to prevent deadlock

        myReader = new ObjectInputStream(connection.getInputStream());  // Creates ObjectInputStream on Socket 


        BigInteger E = (BigInteger)myReader.readObject();  // Receives the server's public key, E, as a BigInteger object
        BigInteger N = (BigInteger)myReader.readObject();  // receives the server's public mod value, N, as a BigInteger object
        encryptionType = (String)myReader.readObject();   //
        System.out.println("Server's Public Key: " + E +"\n\nServer's Public Mod Value: " + N); // preferred symmetric cipher as a String object
        System.out.println("\nEncrytpion Type: " + encryptionType);   // "Sub" or "Add"

        if(encryptionType.equalsIgnoreCase("sub")) {    // Checks for which type of cipher and creates respecctive cipher
            cipher = new Substitution();
            System.out.println("Cipher Type: Substitution");
        } else if(encryptionType.equalsIgnoreCase("add")) {
            cipher = new Additive(); 
            System.out.println("Cipher Type: Additive");
        }

        BigInteger key = new BigInteger(1, cipher.getKey());  // Gets key from cipher and converts to BigInteger
        BigInteger encryptKey = key.modPow(E, N);   // RSA-encrypts the BigInteger version of key using E and N
        System.out.println("\nThe Symmetric Key is: " + encryptKey);
        
        myWriter.writeObject(encryptKey); // Sends RSA-encrypted BigInteger key to server
        myWriter.flush(); // Calls flush() to prevent deadlock

        myWriter.writeObject(cipher.encode(myName));  // Encrypts name and sends it to server
        myWriter.flush(); // Calls flush() to prevent deadlock


        this.setTitle(myName);      // Set title to identify chatter

        Box b = Box.createHorizontalBox();  // Set up graphical environment for
        outputArea = new JTextArea(8, 30);  // user
        outputArea.setBackground(Color.WHITE);
        outputArea.setEditable(false);
        outputArea.setWrapStyleWord(true);
        outputArea.setLineWrap(true);
        b.add(new JScrollPane(outputArea));

        outputArea.append("Welcome to the Chat Group, " + myName + "\n");

        inputField = new JTextField("");  // This is where user will type input
        inputField.addActionListener(this);

        prompt = new JLabel("Type your messages below:");
        Container c = getContentPane();

        c.add(b, BorderLayout.NORTH);
        c.add(prompt, BorderLayout.CENTER);
        c.add(inputField, BorderLayout.SOUTH);

        Thread outputThread = new Thread(this);  // Thread is to receive strings
        outputThread.start();                    // from Server

	      addWindowListener(
              new WindowAdapter()
              {
                  public void windowClosing(WindowEvent e)
                  { 
                    try{
                      myWriter.writeObject(cipher.encode("CLIENT CLOSING"));
                      myWriter.flush();
                      connection.close();
                      //System.exit(0);
                    }catch(IOException exc){
                      System.out.println("Error in Closing Client.");
                      toExit = -1;
                    }
                    System.exit(toExit);
                  }
              }
          );

        setSize(500, 200);
        setVisible(true);

        }catch (Exception e){
            System.out.println("Problem starting client!");
        }
    }

    public void run()
    {
        while (true)
        {
             try {
                byte[] currMsg = (byte[])myReader.readObject();
                System.out.println("\nArray of Bytes Received: " + Arrays.toString(currMsg));
                String decodeMsg = cipher.decode(currMsg);
                /*System.out.print("Decrypted Array of Bytes: [");
                for (int i = 0; i < decodeMsg.length() - 1; i++){
                  System.out.print((int)decodeMsg.charAt(i) + ", ");
                }
                System.out.println((int) decodeMsg.charAt(decodeMsg.length() - 1) + "]");*/
                System.out.println("Decrypted Array of Bytes: " + Arrays.toString(decodeMsg.getBytes()));
                System.out.println("Corresponding String: " + decodeMsg);

                String timeStamp = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a").format(new Date());  // Includes time stamp of when chat was sent
                outputArea.append(" " + decodeMsg + " " + "(" + timeStamp + ")" + "\n");
			         // outputArea.append(currMsg+"\n");
             }
             catch (Exception e)
             {
                System.out.println(e +  ", closing client!");
                break;
             }
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e)
    {
        String currMsg = e.getActionCommand();      // Get input value
        inputField.setText("");

        try{
          //myWriter.println(myName + ":" + currMsg);   // Add name and send it
          myWriter.writeObject(cipher.encode(myName + ":" + currMsg)); // Send encrypted message to server 
          myWriter.flush(); // Calls flush() to prevent deadlock 

          System.out.println("\nOriginal String Message: " + myName + ":" + currMsg);
          System.out.println("Corresponding Array of Bytes: " + Arrays.toString((myName + ":" + currMsg).getBytes()));
          System.out.println("Encrypted Array of Bytes: " + Arrays.toString(cipher.encode(myName + ":" + currMsg)));
        }catch (Exception exc){
          System.out.println("Message Sending to Server Failed.");
        }
        
    }                                             

    public static void main(String [] args)
    {
         RSAChatClient JR = new RSAChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}
