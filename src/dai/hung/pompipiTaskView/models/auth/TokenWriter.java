package dai.hung.pompipiTaskView.models.auth;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
}
