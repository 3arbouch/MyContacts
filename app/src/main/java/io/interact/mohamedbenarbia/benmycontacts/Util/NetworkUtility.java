package io.interact.mohamedbenarbia.benmycontacts.Util;

import io.interact.mohamedbenarbia.benmycontacts.HttpClientFactory;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * This class is used to perform network connection with the server.
 * It principally defines two methods: POST and GET.
 */

public class NetworkUtility {


    /**
     *
     * Executes a httpPost operation to obtain a given service from the web server.
     * @param serviceURL URL of the web service
     * @param headers a HashMap<String, String> that specifies the headers needed for a given service
     * @param postRequestBody JSONObject encapsulating the necessary parameters to execute the request
     * @return response of the web service encapsulated in a httpResponse entity
     */

    public static HttpResponse postMethod(String serviceURL, HashMap<String,String> headers, JSONObject postRequestBody) {

        HttpResponse response = null;
        HttpClient client = HttpClientFactory.getInstance() ;
        HttpPost postRequest = new HttpPost(serviceURL);

        // String entity to send to the server that contains JSonObject
        StringEntity stringEntity = null;

        // Form the Json object to send to the server
        try {
            stringEntity = new StringEntity(postRequestBody.toString());
            postRequest.setEntity(stringEntity);

            // set the header of the post request
            Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                postRequest.setHeader(entry.getKey(), entry.getValue());
            }

            response = client.execute(postRequest);
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Executes a httpGet operation to obtain  agiven service from the web server.
     * @param serviceURL URL of the web service
     * @param headers a HashMap<String, String> that specifies the headers needed for a given service
     * @return
     */

    public static HttpResponse getMethod(String serviceURL, HashMap<String,String> headers) {



        HttpResponse response = null;
        HttpClient client = HttpClientFactory.getInstance() ;
        HttpGet getRequest = new HttpGet(serviceURL);


        // Form the Json object to send to the server
        try {


            // set the header of the post request
            Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                getRequest.setHeader(entry.getKey(), entry.getValue());
            }

            response = client.execute(getRequest);
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
