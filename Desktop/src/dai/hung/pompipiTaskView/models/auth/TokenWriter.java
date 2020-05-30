package dai.hung.pompipiTaskView.models.auth;

import dai.hung.pompipiTaskView.state.AuthState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * <h1>TokenWrite</h1>
 * A class to manage token file of the application
 * @author Ravel Tanjaya
 * @version 1.1.0
 *
 */
public class TokenWriter {
    /**
     * Write the token data to file and remove file if exists
     * @param data data to be written
     * @throws IOException
     */
    public static void writeFile(String data) throws IOException {
        String path = ".data.dat";
        File jwtFile = new File(path);
        if (jwtFile.exists()) {
            jwtFile.delete();
        }
        jwtFile.createNewFile();
        FileWriter myWriter = new FileWriter(path);
        myWriter.write(data);
        myWriter.close();
    }

    /**
     * Delete token file
     */
    public static void deleteFile(){
        String path = ".data.dat";
        File jwtFile = new File(path);
        jwtFile.delete();
    }

    /**
     * Read token data from file
     * @return true or false
     * @throws FileNotFoundException
     */
    public static boolean readStateFromFile() throws FileNotFoundException {
        String path = ".data.dat";
        File jwtFile = new File(path);
        if(jwtFile.canRead()){
            Scanner scan = new Scanner(jwtFile);
            String token = scan.nextLine();
            String refreshToken = scan.nextLine();
            String email = scan.nextLine();
            String localId = scan.nextLine();
            AuthState.login(email,token,refreshToken,localId);
            return true;
        }
        return  false;
    }
}
