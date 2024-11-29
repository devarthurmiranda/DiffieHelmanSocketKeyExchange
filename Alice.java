import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Alice {
    public ServerSocket servSocket;

    public void createServerSocket(int port) throws IOException {
        servSocket = new ServerSocket(port);
    }

    public Socket acceptConnection() throws IOException {
        return servSocket.accept();
    }

    public void closeServerSocket() throws IOException {
        servSocket.close();
        System.out.println("Server socket closed!");
    }

    // Modular Exponentiation to Diffie-Hellman
    public int modularExponentiation(int base, int exponent, int modulus) {
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

    // Alice rotine
    public void dataTransfer(Socket bobSocket) throws IOException {
        final int i = 5;
        final int p = 23;
        final int sa = 3;

        try {
            ObjectOutputStream out = new ObjectOutputStream(bobSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(bobSocket.getInputStream());

            // Number exchange calculation
            final int na = modularExponentiation(i, sa, p);

            // Exchange
            out.writeInt(na);
            out.flush();

            // Read Bob exchange
            int b = in.readInt();
            System.out.println("Modular from bob: " + b);

            // Calculate Secret
            final int s = modularExponentiation(b, sa, p);
            System.out.println("Secret: " + s);

            // Encrypt message
            String msg = "This is a message from Alice, enemy missiles incoming!";
            String a_encrypted = caesar(msg, s);

            out.writeUTF(a_encrypted);
            out.flush();

            String b_encrypted = in.readUTF();
            System.out.println("Encrypted Bob message: " + b_encrypted);

            String b_decrypted = decryptCaesar(b_encrypted, s);
            System.out.println("Decrypted Bob message: " + b_decrypted);

            // Close streams
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something went wrong! Address: " + bobSocket.getInetAddress().getHostAddress());
        } finally {
            bobSocket.close();
            System.out.println("bob disconnected: " + bobSocket.getInetAddress().getHostAddress());
        }
    }
}