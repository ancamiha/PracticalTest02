package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientThread extends Thread {
    private Socket socket = null;

    private String address;
    private int port;
    private String key;
    private String value;
    private String requestType;
    private TextView getResponseTextView;

    public ClientThread(String address, int port, String key, String value, String requestType, TextView getResponseTextView) {
        this.address = address;
        this.port = port;
        this.key = key;
        this.value = value;
        this.requestType = requestType;
        this.getResponseTextView = getResponseTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "Could not create socket!");
                return;
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "BufferReader or PrintWriter are null!");
                return;
            }

            printWriter.println(key);
            printWriter.flush();
            printWriter.println(value);
            printWriter.flush();
            printWriter.println(requestType);
            printWriter.flush();

            String responseInformation;
            while ((responseInformation = bufferedReader.readLine()) != null) {
                final String finalizedResponseInformation = responseInformation;
                getResponseTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        getResponseTextView.setText(finalizedResponseInformation);
                    }
                });
            }

        } catch (IOException e) {
            Log.e(Constants.TAG, "An exception has occurred: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(Constants.TAG, "An exception has occurred: " + e.getMessage());
                    // print stacktrace
                }
            }
        }
    }
}
