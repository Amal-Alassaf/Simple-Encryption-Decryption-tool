
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.util.ArrayList;
import java.util.Scanner;

public class Ciphers {

    private Scanner input = new Scanner(System.in);
    private Map<Character, String> morseMap = new HashMap<>();
    private Map<String, Character> reverseMorseMap = new HashMap<>();

    Ciphers() {

    }

    public String encryptDES(String filePath, String key) throws Exception {
        // Read input from file
        StringBuilder message = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                message.append(line).append("\n");
            }
        }

        // Generate DES key
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key1 = secretKeyFactory.generateSecret(desKeySpec);

        // Encrypt data
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key1);
        byte cipherText[] = cipher.doFinal(message.toString().getBytes());

        // Return Base64 encoded ciphertext
        return (Base64.getEncoder().encodeToString(cipherText));
    }

    public void decryptFile(String filePath, String secretKey) {
        try {
            // Read the encrypted content from the file
            String cipherText = new String(Files.readAllBytes(Paths.get(filePath))).trim();

            // Remove any unintended spaces or newlines (fix for "Illegal base64 character" issue)
            cipherText = cipherText.replaceAll("\\s+", "");

            // Decode the Base64-encoded ciphertext
            byte[] encryptedBytes = Base64.getDecoder().decode(cipherText);

            // Generate the DES key
            DESKeySpec desKeySpec = new DESKeySpec(secretKey.getBytes());
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = secretKeyFactory.generateSecret(desKeySpec);

            // Initialize the cipher for decryption
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            // Perform decryption
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String decryptedText = new String(decryptedBytes);

            // Display the decrypted text
            System.out.println("Decrypted text: " + decryptedText);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid Base64 encoding. Ensure the input is correct.");
        } catch (Exception e) {
            System.out.println("Decryption error: " + e.getMessage());
        }
    }

    {
        String[][] morseTable = {
            {"a", ".-"}, {"b", "-..."}, {"c", "-.-."}, {"d", "-.."}, {"e", "."},
            {"f", "..-."}, {"g", "--."}, {"h", "...."}, {"i", ".."}, {"j", ".---"},
            {"k", "-.-"}, {"l", ".-.."}, {"m", "--"}, {"n", "-."}, {"o", "---"},
            {"p", ".--."}, {"q", "--.-"}, {"r", ".-."}, {"s", "..."}, {"t", "-"},
            {"u", "..-"}, {"v", "...-"}, {"w", ".--"}, {"x", "-..-"}, {"y", "-.--"},
            {"z", "--.."}, {"1", ".----"}, {"2", "..---"}, {"3", "...--"}, {"4", "....-"},
            {"5", "....."}, {"6", "-...."}, {"7", "--..."}, {"8", "---.."}, {"9", "----."},
            {"0", "-----"}, {" ", "/"}
        };

        for (String[] pair : morseTable) {
            morseMap.put(pair[0].charAt(0), pair[1]);  // Convert first string to char
            reverseMorseMap.put(pair[1], pair[0].charAt(0));  // Store reverse mapping
        }

        for (String[] pair : morseTable) {
            morseMap.put(pair[0].charAt(0), pair[1]);
            reverseMorseMap.put(pair[1], pair[0].charAt(0));
        }
    }

    public String morseEncode(String text) {
        StringBuilder encoded = new StringBuilder();
        for (char ch : text.toLowerCase().toCharArray()) {
            encoded.append(morseMap.getOrDefault(ch, "")).append(" ");
        }
        return encoded.toString().trim();
    }

    public String morseDecode(String morse) {
        StringBuilder decoded = new StringBuilder();
        for (String code : morse.split(" ")) {
            decoded.append(reverseMorseMap.getOrDefault(code, '?'));
        }
        return decoded.toString();
    }

    void processFile(String filePath, boolean encrypt) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String result = encrypt ? morseEncode(line) : morseDecode(line);
                System.out.println(result);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public String ceaser(String plainText, int key) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < plainText.length(); i++) {
            char ch = plainText.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) ('A' + ((ch - 'A') + key) % 26);
            } else if (ch >= 'a' && ch <= 'z') {
                ch = (char) ('a' + (ch - 'a' + key) % 26);
            }
            result.append(ch);
        }
        return result.toString();
    }

    public String ceaserDecrypt(String cipherText, int key) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cipherText.length(); i++) {
            char ch = cipherText.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) ('A' + ((ch - 'A' - key + 26) % 26));
            } else if (ch >= 'a' && ch <= 'z') {
                ch = (char) ('a' + ((ch - 'a' - key + 26) % 26));
            }
            result.append(ch);
        }
        return result.toString();
    }

}
