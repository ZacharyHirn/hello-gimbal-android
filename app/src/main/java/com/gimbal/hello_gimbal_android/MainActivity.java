package com.gimbal.hello_gimbal_android;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gimbal.android.BeaconManager;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Visit;

import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconSighting;


public class MainActivity extends ActionBarActivity {

    private PlaceManager placeManager;
    private PlaceEventListener placeEventListener;
    private ArrayAdapter<String> listAdapter;
    private ListView listView;

    private BeaconEventListener beaconEventListener;
    private BeaconManager beaconManager;

    private int rssi1, rssi2, rssi3;
    private int minRSSI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rssi1 = 0;
        rssi2 = 0;
        rssi3 = 0;
        minRSSI = 1000;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(listAdapter);

        listAdapter.add("Setting Gimbal API Key");
        listAdapter.notifyDataSetChanged();
        Gimbal.setApiKey(this.getApplication(), "17ec8de5-fa08-47db-b399-cff8a44c64ac");

        beaconManager = new BeaconManager();

        beaconEventListener = new BeaconEventListener(){
            //return rssi for beacons sighted
            @Override
            public void onBeaconSighting(BeaconSighting sighting) {

                if(sighting.getBeacon().getName().equals("beacon 1")) {
                    rssi1 = Math.abs(sighting.getRSSI());
                }else if(sighting.getBeacon().getName().equals("beacon 2")) {
                    rssi2 = Math.abs(sighting.getRSSI());
                }else if(sighting.getBeacon().getName().equals("beacon 3")) {
                    rssi3 = Math.abs(sighting.getRSSI());
                }

                //if none are equal to zero find minimum
                if((rssi1 & rssi2 & rssi3) != 0){
                    minRSSI = Math.min( Math.min(rssi1, rssi2), rssi3);
                    //rssi1 is closest
                    if(rssi1 == minRSSI){
                        listAdapter.add(String.format("Beacon 1 loudest, has RSSI %d", sighting.getRSSI()));
                        listAdapter.notifyDataSetChanged();
                    } else if(rssi2 == minRSSI){
                        listAdapter.add(String.format("Beacon 2 loudest, has RSSI %d", sighting.getRSSI()));
                        listAdapter.notifyDataSetChanged();
                    } else if(rssi3 == minRSSI){
                        listAdapter.add(String.format("Beacon 3 loudest, has RSSI %d", sighting.getRSSI()));
                        listAdapter.notifyDataSetChanged();
                    }
                } else {

                    //listAdapter.add(String.format("Not all beacons initialized"));
                    //listAdapter.notifyDataSetChanged();

                }
            }
        };

        /*

        placeEventListener = new PlaceEventListener() {
            @Override
            public void onVisitStart(Visit visit) {
                //listAdapter.add(String.format("Start Visit for %s", visit.getPlace().getName()));
                //listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onVisitEnd(Visit visit) {
                //listAdapter.add(String.format("End Visit for %s", visit.getPlace().getName()));
                //listAdapter.notifyDataSetChanged();
            }
        };

        */

        //placeManager = PlaceManager.getInstance();
        //placeManager.addListener(placeEventListener);
        //placeManager.startMonitoring();



        beaconManager.addListener(beaconEventListener);
        beaconManager.startListening();

        CommunicationManager.getInstance().startReceivingCommunications();
    }

}