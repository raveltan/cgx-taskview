package dai.hung.pompipiTaskView.models.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dai.hung.pompipiTaskView.models.ResultInterface;
import dai.hung.pompipiTaskView.models.auth.TokenRefresh;
import dai.hung.pompipiTaskView.state.AuthState;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestRequest implements Runnable {
    protected ObjectMapper mapper = new ObjectMapper();
    protected String baseUrl = "https://taskview-6358e.firebaseio.com/";
    protected OkHttpClient client = new OkHttpClient();

    public enum HttpVerb {
        PUT,
        GET,
        POST,
        DELETE,
        PATCH
    }

    String location;
    Map data;
    String error;
    HttpVerb httpVerb;
    ResultInterface resultInterface;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    public RestRequest(String location, Map data, HttpVerb httpVerb, ResultInterface resultInterface) {
        this.location = location;
        this.resultInterface = resultInterface;
        this.data = data;
        this.httpVerb = httpVerb;
    }

    public RestRequest(String location, HttpVerb httpVerb, ResultInterface resultInterface) {
        this.location = location;
        this.resultInterface = resultInterface;
        this.httpVerb = httpVerb;
    }

    public Map doRequest() throws JsonProcessingException {
        Request.Builder builder = new Request.Builder().url(
                this.baseUrl + location + ".json?auth=" + AuthState.getToken()
        );
        if (data != null) {
            RequestBody body = RequestBody.create(mapper.writeValueAsString(data), JSON);
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
        }
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            Map result = mapper.readValue(response.body().string(), Map.class);
            if (result != null && result.containsKey("error")) {
                if (result.get("error").equals("Auth token is expired")) {
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
            System.out.println(ex);
        }
    }
}

