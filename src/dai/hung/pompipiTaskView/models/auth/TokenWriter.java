package dai.hung.pompipiTaskView.models.auth;

import dai.hung.pompipiTaskView.state.AuthState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TokenWriter {
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
        System.out.println("Successfully wrote to the file.");
    }

    public static void deleteFile(){
        String path = ".data.dat";
        File jwtFile = new File(path);
        jwtFile.delete();
    }

    public static boolean readStateFromFile() throws FileNotFoundException {
        String path = ".data.dat";
        File jwtFile = new File(path);
        if(jwtFile.canRead()){
            Scanner scan = new Scanner(jwtFile);
            String token = scan.nextLine();
            String refreshToken = scan.nextLine();
            String email = scan.nextLine();
            AuthState.login(email,token,refreshToken);
            return true;
        }
        return  false;
    }
}
