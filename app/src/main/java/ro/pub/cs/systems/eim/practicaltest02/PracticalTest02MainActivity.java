package ro.pub.cs.systems.eim.practicaltest02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;

//nc 192.168.251.199 2027  <- DEFAULT GATEWAY
public class PracticalTest02MainActivity extends AppCompatActivity {

    private List<String> options = Arrays.asList("put", "get");

    // Server widgets
    private EditText serverPortEditText = null;
    private Button connectButton = null;

    // Client widgets
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText requestTypeEditText = null;
    private EditText keyEditText = null;
    private EditText valueEditText = null;
    private Button sendButton = null;
    private TextView getResponseTextView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        Log.i("PracticalTest02", "[MAIN ACTIVITY] onCreate() callback method has been invoked");

        serverPortEditText = (EditText) findViewById(R.id.server_port_edit_text);
        connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String port = serverPortEditText.getText().toString().trim();
                if (port.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    serverThread = new ServerThread(Integer.parseInt(port));
                    if (serverThread.getServerSocket() == null) {
                        Log.e("MainActivity", "[MAIN ACTIVITY] Could not create server thread!");
                    } else {
                        serverThread.start();
                    }
                }
            }
        });

        clientAddressEditText = (EditText) findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText) findViewById(R.id.client_port_edit_text);
        requestTypeEditText = (EditText) findViewById(R.id.request_type);
        keyEditText = (EditText) findViewById(R.id.key);
        valueEditText = (EditText) findViewById(R.id.value);

        sendButton = (Button) findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String address = clientAddressEditText.getText().toString().trim();
                String port = clientPortEditText.getText().toString().trim();
                String key = keyEditText.getText().toString();
                String value = valueEditText.getText().toString();
                String reqType = requestTypeEditText.getText().toString().trim();

                if (!reqType.isEmpty() && !options.contains(reqType.toLowerCase(Locale.ROOT))) {
                    requestTypeEditText.setError("This type is not accepted!");
                }

                if (address.isEmpty() || port.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "[MAIN ACTIVITY] Client connection parameters should be filled!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (serverThread == null || !serverThread.isAlive()) {
                        Toast.makeText(getApplicationContext(),
                                "[MAIN ACTIVITY] There is no server to connect to!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        getResponseTextView.setText(Constants.EMPTY_STRING);
                        clientThread = new ClientThread(
                                address, Integer.parseInt(port), key, value, reqType, getResponseTextView);
                        clientThread.start();
                    }
                }
            }
        });
        getResponseTextView = (TextView) findViewById(R.id.response);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() method was invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
    }
}