package dai.hung.pompipiTaskView.models.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import dai.hung.pompipiTaskView.models.ResultInterface;
import dai.hung.pompipiTaskView.models.auth.TokenRefresh;
import dai.hung.pompipiTaskView.state.AuthState;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A RestRequest class that is based on the OkHTTP
 * A runnable
 * @author Ravel Tanjaya
 * @version 1.1.0
 */
public class RestRequest implements Runnable {
    protected ObjectMapper mapper = new ObjectMapper();
    protected Gson gson = new Gson();
    protected String baseUrl = "https://taskview-6358e.firebaseio.com/";
    protected OkHttpClient client = new OkHttpClient();

    /**
     * A HTTP verb enum
     * contains all needed http verb
     */
    public enum HttpVerb {
        PUT,
        GET,
        POST,
        DELETE,
        PATCH
    }

    String location;
    Map<String,String> data;
    String error;
    HttpVerb httpVerb;
    ResultInterface resultInterface;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    /**
     * A Contructor for http request
     * This is used for request with body such as post
     * @param location location of data
     * @param data map data that will be converted to json
     * @param httpVerb http verb
     * @param resultInterface callback
     */
    public RestRequest(String location, Map data, HttpVerb httpVerb, ResultInterface resultInterface) {
        this.location = location;
        this.resultInterface = resultInterface;
        this.data = data;
        this.httpVerb = httpVerb;
    }

    /**
     * A Contructor for http request
     * This is used for request without body such as get
     * @param location location of data
     * @param httpVerb http verb
     * @param resultInterface callback
     */
    public RestRequest(String location, HttpVerb httpVerb, ResultInterface resultInterface) {
        this.location = location;
        this.resultInterface = resultInterface;
        this.httpVerb = httpVerb;
    }

    /**
     * Run the rest request with the parameter from the class.
     * @return Map of data or error
     * @throws JsonProcessingException
     */
    public Map doRequest() throws JsonProcessingException {
        Request.Builder builder = new Request.Builder().url(
                this.baseUrl + location + ".json?auth=" + AuthState.getToken()
        );

        if (data != null) {
            RequestBody body = RequestBody.create(mapper.writeValueAsString(data), JSON);
            //RequestBody body = RequestBody.create(gson.toJson(data), JSON);
            switch (httpVerb) {
                case PUT:
                    builder.put(body);
                    break;
                case POST:
                    builder.post(body);
                    break;
                case DELETE:
                    System.out.println("Delete may not have request body");
                    break;
                case PATCH:
                    builder.patch(body);
                    break;
                default:
                    System.out.println("GET resquest may not have body");
                    break;
            }
        }
        if(httpVerb == HttpVerb.DELETE){
            builder.delete();
            builder.delete();
        }
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();

            if(httpVerb == HttpVerb.PUT){
                Map<String,String> temp = new HashMap<>();
                List<String> d = mapper.readValue(res, List.class);
                if(d !=null){
                    for(int i=0;i<d.size();i++){
                        temp.put(Integer.toString(i),d.get(i));
                    }
                }
                res = gson.toJson(temp);
            }
            Map result = mapper.readValue(res, Map.class);
            if (result != null && result.containsKey("error")) {
                if (result.get("error").equals("Auth token is expired")) {
                    //Run the auth token refresh to get a fresh auth token.
                    error = "Auth token is expired";
                    ExecutorService executor = Executors.newFixedThreadPool(1);
                    executor.execute(new TokenRefresh(
                            AuthState.getRefreshToken(), new ResultInterface() {
                        @Override
                        public void onFinish(Map result, String error) {
                           try{
                                resultInterface.onFinish(
                                        doRequest(), null
                                );
                           }catch (Exception ex){
                               System.out.println(ex);
                           }
                        }
                    }
                    ));
                } else {
                    error = result.get("error").toString();
                }
            }
            return result;
        } catch (IOException e) {
            error = e.toString();
        }
        return null;
    }


    /**
     * Runnable run mehtod,
     * will be executed asyncronously.
     * will refresh token before returning value if token is expired.
     */
    @Override
    public void run() {
        try{
            Map result = doRequest();
            if(error==null){
                resultInterface.onFinish(result, null);
            } else if(error == "Auth token is expired"){
                System.out.println("Auth token is expired");
            }else{
                resultInterface.onFinish(result, error);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

