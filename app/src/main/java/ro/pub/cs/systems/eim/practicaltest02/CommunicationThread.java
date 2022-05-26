package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class CommunicationThread extends Thread {
    private ServerThread serverThread = null;
    private Socket socket = null;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "BufferReader or PrintWriter are null!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (reqType / key / value)");
            String key = bufferedReader.readLine();
            String value = bufferedReader.readLine();
            String reqType = bufferedReader.readLine();
            if (reqType != null && reqType.toLowerCase(Locale.ROOT).equals("get")) {
                if (key == null || key.isEmpty()) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (reqType / key)!");
                    return;
                }
            } else {
                if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (reqType / key / value)!");
                    return;
                }
            }

            HashMap<String, String> data = serverThread.getData();
            WorldTimeApiInformation worldTimeApiInformation = null;
            String result = null;
            String valueFromData = null;
            if (data.containsKey(key)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                valueFromData = data.get(key);
                result = valueFromData;
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";


                String unixtime = null;
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }



                Log.d(Constants.TAG, "req type" + reqType);
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else {
                    Log.i(Constants.TAG, pageSourceCode);
                }

                JSONObject content = new JSONObject(pageSourceCode);
                unixtime = content.getString("unixtime");

                switch (reqType.toLowerCase(Locale.ROOT)) {
                    case "get":
                        if (unixtime != null && Long.valueOf(System.currentTimeMillis() / 1000L + 60).compareTo(Long.valueOf(unixtime)) <= 0) {
                            result = data.get(key);
                        } else {
                            result = "The key is not valid anymore";
                        }
                        break;
                    case "put":
                        worldTimeApiInformation = new WorldTimeApiInformation(key, value);
                        worldTimeApiInformation.setValue(value);

                        serverThread.setData(key, value);
                        result = "{" + key + ", " + value + "}";
                        break;
                }
            }

            printWriter.println(result);
            printWriter.flush();
        } catch (IOException | JSONException e) {
            Log.e(Constants.TAG, "An exception has occurred: " + e.getMessage());
        }
    }
}
