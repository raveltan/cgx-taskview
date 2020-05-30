package dai.hung.pompipiTaskView.models.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * <h1>ParameterStringBuilder</h1>
 * A string builder to build url options
 * @author  Ravel Tanjaya
 * @version 1.1.0
 */
public class ParameterStringBuilder {
    /**
     * Build the url
     * @param params parameters
     * @return url result
     * @throws UnsupportedEncodingException
     */
    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
}