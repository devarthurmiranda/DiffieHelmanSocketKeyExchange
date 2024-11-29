import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Bob {


    // Modular Exponentiation to Diffie-Hellman
    public static int modularExponentiation(int base, int exponent, int modulus) {
        int result = 1;
        base = base % modulus;

        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                result = (result * base) % modulus;
            }
            exponent >>= 1;
            base = (base * base) % modulus;
        }

        return result;
    }

    // Caesar Cipher
    public static String caesar(String text, int shift) {
        StringBuilder result = new StringBuilder();
        shift = shift % 26; // Ensure shift is within the bounds of 0-25
    
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                char offsetChar = (char) ((c - 'A' + shift + 26) % 26 + 'A');
                result.append(offsetChar);
            } else if (Character.isLowerCase(c)) {
                char offsetChar = (char) ((c - 'a' + shift + 26) % 26 + 'a');
                result.append(offsetChar);
            } else {
                result.append(c); // Non-alphabetic characters remain unchanged
            }
        }
    
        return result.toString();
    }

    public static String decryptCaesar(String encryptedMsg, int shift) {
        return caesar(encryptedMsg, -shift);
    }

    // Bob Rotine
    public static void main(String[] args) {
        System.out.println("Stablishing connection...");
        final int i = 5;
        final int p = 23;
        final int sb = 4;

        try {
            Socket socket = new Socket("localhost", 1234);
            System.out.println("Connection stablished!");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            final int nb = modularExponentiation(i, sb, p);

            // Read Alice exchange
            int a = in.readInt();
            System.out.println("Modular from Alice: " + a);

            // Exchange
            out.writeInt(nb);
            out.flush();

            // Calculate secret
            final int s = modularExponentiation(a, sb, p);
            System.out.println("Secret: " + s);

            // Receiving encrypted message from Alice
            String a_encrypted = in.readUTF();
            System.out.println("Encrypted message from Alice: " + a_encrypted);

            String a_decrypted = decryptCaesar(a_encrypted, s);
            System.out.println("Decrypted message from Alice: " + a_decrypted);

            // Sending message
            String msg = "This is a message from Bob! SEND HELP NOW!";
            String b_encrypted = caesar(msg, s);
            out.writeUTF(b_encrypted);
            out.flush();

            // Close streams
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}