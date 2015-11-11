package com.garrytrue.workwithwebsocket.a.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.fragments.FragmentClientMode;
import com.garrytrue.workwithwebsocket.a.fragments.FragmentSelectWorkMode;
import com.garrytrue.workwithwebsocket.a.interfaces.IBtnClickListener;
import com.garrytrue.workwithwebsocket.a.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.a.services.ServerService;
import com.garrytrue.workwithwebsocket.a.utils.Utils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity implements IBtnClickListener {
    private RelativeLayout mContainer;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        if (savedInstanceState != null && !TextUtils.isEmpty(savedInstanceState.getString
                (getString(R.string.bundle_key_current_fragment_tag)))) {
            handleSavedState(savedInstanceState.getString
                    (getString(R.string.bundle_key_current_fragment_tag)));
        } else {
            showSelectModeFragment();
        }

    }

    private void handleSavedState(String tag) {
        Fragment fr = getFragmentManager().findFragmentById(getFragmentContainerId());
        if (fr != null && !TextUtils.isEmpty(fr.getTag())) {
            String currentTag = fr.getTag();
            Log.d(TAG, "handleSavedState: CURRENT_TAG " + fr.getTag());
            fr = getFragmentManager().findFragmentByTag(currentTag);
            Log.d(TAG, "handleSavedState: NEW_TAG " + fr.getTag());
            if (fr.isAdded()) {
                Log.d(TAG, "handleSavedState: Fragment is addided");
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.show(fr).commit();
            }
        } else {
            if (tag.equals(getString(R.string.fragment_select_work_mode_tag))) {
                showSelectModeFragment();
            } else if (tag.equals(getString(R.string.fragment_client_mode_tag))) {
                showClientModeFragment();
            } else if (tag.equals(getString(R.string.fragment_server_mode_tag))) {
                showServerModeFragment();
            }
        }
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContainer = (RelativeLayout) findViewById(R.id.fragment_container);
        Log.d(TAG, "initUI: WIFI_ADDRESS " + wifiIpAddress(this));
    }

    public int getFragmentContainerId() {
        return mContainer.getId();
    }

    private void showSelectModeFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(getFragmentContainerId(), FragmentSelectWorkMode.newInstance(), getString(R.string
                .fragment_select_work_mode_tag));
        ft.commit();
        Utils.hideKeyboard(this, mContainer.getWindowToken());
    }

    private void showClientModeFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(getFragmentContainerId(),
                FragmentClientMode.newInstance(), getString(R.string.fragment_client_mode_tag));
        ft.commit();
        Utils.hideKeyboard(this, mContainer.getWindowToken());
    }

    private void showServerModeFragment() {
// TODO: 08.11.15 Implement server fragment
    }

    // Handle btn click from fragments
    @Override
    public void onClick(final int id) {
        switch (id) {
            case R.id.btn_start_client:
                showClientModeFragment();
                break;
            case R.id.btn_start_server:
                showServerModeFragment();
                startServirService();
        }
    }

    private void startServirService() {
        Intent intent = new Intent(this, ServerService.class);
        intent.putExtra(getString(R.string.bundle_key_inet_address), new PreferencesManager
                (this).getServerAddress());
        startService(intent);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getFragmentManager().findFragmentByTag(getString(R.string
                .fragment_select_work_mode_tag)) != null) {
            outState.putString(getString(R.string.bundle_key_current_fragment_tag), getString(R.string
                    .fragment_select_work_mode_tag));
        } else if (getFragmentManager().findFragmentByTag(getString(R.string
                .fragment_client_mode_tag)) != null) {
            outState.putString(getString(R.string.bundle_key_current_fragment_tag), getString(R.string
                    .fragment_client_mode_tag));
        } else if (getFragmentManager().findFragmentByTag(getString(R.string
                .fragment_server_mode_tag)) != null) {
            outState.putString(getString(R.string.bundle_key_current_fragment_tag), getString(R.string
                    .fragment_server_mode_tag));
        }
        super.onSaveInstanceState(outState);
    }

    protected String wifiIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }


//    private void runServer() throws UnknownHostException {
//        mServer = new WebSocketServer() {
//            @Override
//            public void onOpen(WebSocket conn, ClientHandshake handshake) {
//
//            }
//
//            @Override
//            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
//
//            }
//
//            @Override
//            public void onMessage(WebSocket conn, String message) {
//
//            }
//
//            @Override
//            public void onMessage(WebSocket conn, ByteBuffer buffer) {
//
//            }
//
//            @Override
//            public void onError(WebSocket conn, Exception ex) {
//
//            }
//        };
//    }

//    private void connectWebSocket() {
//        URI uri;
//        try {
//            uri = new URI("ws://192.168.0.100:10000");
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            Snackbar.make(mServerAddress, "Problem", Snackbar.LENGTH_SHORT).show();
//            return;
//        }
//
//        mWebSocketClient = new WebSocketClient(uri) {
//            @Override
//            public void onOpen(ServerHandshake serverHandshake) {
//                Log.d(TAG, "onOpen() called with: " + "serverHandshake = [" + serverHandshake + "]");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        hideProgress();
//                    }
//                });
//                try {
//                    Cipher cipher = Cipher.getInstance("AES");
//                    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//                    keyGenerator.init(128);
//                    // // TODO: 07.11.15 Need sent first secret key after that send encrypted image
//                    SecretKey secretKey = keyGenerator.generateKey();
//                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
////                    // TODO: 07.11.15 Add input byte array(image) to doFinal()
//                    byte[] encrypted = cipher.doFinal();
//                } catch (NoSuchAlgorithmException e) {
//                    e.printStackTrace();
//                } catch (NoSuchPaddingException e) {
//                    e.printStackTrace();
//                } catch (InvalidKeyException e) {
//                    e.printStackTrace();
//                } catch (BadPaddingException e) {
//                    e.printStackTrace();
//                } catch (IllegalBlockSizeException e) {
//                    e.printStackTrace();
//                }
//
//                mWebSocketClient.send("Open Connection " + Build.MANUFACTURER + " " + Build.MODEL);
//            }
//
//            @Override
//            public void onMessage(String s) {
//                final String message = s;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTvLabel.setText(mTvLabel.getText() + "\n" + message);
//                    }
//                });
//            }
//
//            @Override
//            public void onClose(int i, String s, boolean b) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        hideProgress();
//                    }
//                });
//                Log.d(TAG, "onClose() called with: " + "i = [" + i + "], s = [" + s + "], b = [" + b + "]");
//            }
//
//            @Override
//            public void onError(Exception e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        hideProgress();
//                    }
//                });
//                Log.d(TAG, "onError() called with: " + "e = [" + e.getMessage() + "]");
//            }
//        };
//        mWebSocketClient.connect();
//    }

}
