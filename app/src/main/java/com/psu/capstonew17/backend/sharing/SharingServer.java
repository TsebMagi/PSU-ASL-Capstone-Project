package com.psu.capstonew17.backend.sharing;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import java.util.List;
import java.util.ArrayList;

public class SharingServer extends Service {
    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel wifiChannel;
    private BroadcastReceiver wifiReceiver;
    private List <WifiP2pDevice> listOfPeers = new ArrayList<WifiP2pDevice>();         //host list of peers from PeerListListener

    //find peers to connect with
    private WifiP2pManager.PeerListListener peerListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            listOfPeers.clear();    //empty peer list
            listOfPeers.addAll(wifiP2pDeviceList.getDeviceList());  //repopulate with new devices
            if(listOfPeers.size() ==0){System.out.println("No devices within range");}  //need to modify for android device
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        wifiManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        wifiChannel = wifiManager.initialize(this, getMainLooper(), null);

        // create the broadcast receiver to manage P2P state
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO: handle events
                String action = intent.getAction();
                if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
                {
                    //handle case
                }
                else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
                {
                    //handle case
                }
                else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
                {
                    //handle case
                }
                else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
                {
                    //if empty, find peers
                    if(wifiManager == null){wifiManager.requestPeers(wifiChannel,peerListener);}
                }
            }
        };
        registerReceiver(wifiReceiver, intentFilter);
    }

   // @Override
    public void connect(){      //connect to device
        WifiP2pDevice aDevice = listOfPeers.get(0); //get device in front of list
        WifiP2pConfig configuration = new WifiP2pConfig();
        configuration.deviceAddress = aDevice.deviceAddress;    //MAC address IDing device
        configuration.wps.setup= WpsInfo.PBC;           //wifi protected setup push button config
        wifiManager.connect(wifiChannel, configuration, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //if/else cases in broadcast reciver will provide noitificaiton for successful connect
            }

            @Override
            public void onFailure(int i) {
                System.out.println("Failed to connect");
            }
        });
    }
    //TODO: Implement peer connect
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}