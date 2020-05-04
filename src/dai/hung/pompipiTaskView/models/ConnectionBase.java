package dai.hung.pompipiTaskView.models;

import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ConnectionBase {
    public static String apiKey =
            "AIzaSyDIL0Ip-z3DwSx2X0KaCOdPbcOK2SmqjtQ";
    private static URL url;
    private static HttpURLConnection connection;
    private static ObjectMapper mapper = new ObjectMapper();

    public static Map request(String urlAddress, String connectionType, String[] ...connectionParams){
        try {
            url = new URL(urlAddress);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(connectionType);
            Map<String, String> params = new HashMap<>();
            for (String[] parameters : connectionParams) {
                params.put(parameters[0], parameters[1]);
            }
            connection.setConnectTimeout(5000);
            connection.setDoOutput(true);
            try {
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(ParameterStringBuilder.getParamsString(params));
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();
                return mapper.readValue(content.toString(),Map.class);
            }catch (Exception ex){
                InputStream errorstream = connection.getErrorStream();
                StringBuilder response = new StringBuilder();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(errorstream));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return mapper.readValue(response.toString(),Map.class);
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return null;
    }
}
