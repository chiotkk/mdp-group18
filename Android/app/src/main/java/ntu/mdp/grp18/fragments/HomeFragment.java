package ntu.mdp.grp18.fragments;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.VisibilityAwareImageButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.nio.charset.Charset;
import java.util.UUID;

import ntu.mdp.grp18.MainActivity;
import ntu.mdp.grp18.R;

public class HomeFragment extends Fragment {

    // Declarations for the main screen with map
    ImageButton forwardButton, leftButton, rightButton, reverseButton;
    TextView tv_roboStatus, tv_myStringCmd;
    ToggleButton tb_setWaypointCoord, tb_setStartCoord, tb_autoManual, tb_fastestpath, tb_exploration;
    private static final String TAG = "HomeFragment";


    //Declarations for the bluetooth connection
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice myBTConnectionDevice;
    static String connectedDevice;
    boolean connectedState;
    boolean currentActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        connectedDevice = null;
        connectedState = false;
        currentActivity = true;

        //Register Broadcast Receiver for incoming bluetooth connection
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(btConnectionReceiver, new IntentFilter("btConnectionStatus"));

        //Register Broadcast Receiver for incoming Bluetooth message
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(incomingMessageReceiver, new IntentFilter("IncomingMsg"));

        //Setting up onClickListeners for navigation buttons as well as other buttons
        onClickNavigationForward();
        onClickNavigationBackward();
        onClickNavigationRight();
        onClickNavigationLeft();
        onClickSetWaypoint();
        onClickSetStartPoint();
        //onClickStartShortestPath();
        onClickStartExploration();
        //onClickAutoOrManualUpdate();

    }

    //setting up onClickListener for the forward navigation button
    public void onClickNavigationForward() {
        forwardButton = getActivity().findViewById(R.id.fwd_btn);
        forwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //CHECK IF CONNECTED TO DEVICE FIRST
                if (connectedDevice == null) {
                    Toast.makeText(getContext(), "Please Connect to a Device First!!",
                            Toast.LENGTH_SHORT).show();
                } else {

                    //TODO: send message to RPI using btChat to tell the bot to move forward

                    Log.d(TAG, "Android Controller: Move Forward");
                }

            }

        });
    }

    //setting up onClickListener for the reverse navigation button
    public void onClickNavigationBackward() {
        reverseButton = getActivity().findViewById(R.id.rev_btn);
        reverseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //CHECK IF CONNECTED TO DEVICE FIRST
                if (connectedDevice == null) {
                    Toast.makeText(getContext(), "Please Connect to a Device First!!",
                            Toast.LENGTH_SHORT).show();
                } else {

                    //TODO: send message to RPI using btChat to tell the bot to move in reverse direction

                    Log.d(TAG, "Android Controller: Move Backwards");
                }

            }

        });
    }

    //setting up onClickListener for left rotate button
    public void onClickNavigationLeft() {
        leftButton = getActivity().findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if connected to the device first
                if (connectedDevice == null) {
                    Toast.makeText(getContext(), "Please connect to a device first", Toast.LENGTH_SHORT).show();
                } else {

                    //TODO: send message to RPI using btChat to tell the bot to rotate in left direction

                    Log.d(TAG, "Android Controller: Move Left");
                }
            }
        });
    }

    //setting up onClickListener for right rotate button
    public void onClickNavigationRight() {
        rightButton = getActivity().findViewById(R.id.right_btn);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if connected to the device first
                if (connectedDevice == null) {
                    Toast.makeText(getContext(), "Please connect to a device first", Toast.LENGTH_SHORT).show();
                } else {

                    //TODO: send message to RPI using btChat to tell the bot to rotate in right direction

                    Log.d(TAG, "Android Controller: Move Right");
                }
            }
        });
    }

    //setting up onClickListener for waypoint toggle button. Press the button and select the waypoint on the map
    public void onClickSetWaypoint(){
        tb_setWaypointCoord = getActivity().findViewById(R.id.tb_setWaypointCoord);
        tb_setWaypointCoord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //first check if bluetooth is connected to any device
                if (connectedDevice == null) {
                    Toast.makeText(getContext(), "Please connect to a device first", Toast.LENGTH_SHORT).show();
                } else {
                    if(isChecked){
                        //toggle is enabled; select waypoint on the map
                        //TODO: Use the function in map to set waypoint

                        //Change the button back to disabled once the waypoint has been set
                        tb_setWaypointCoord.toggle();
                    }
                }
            }
        });
    }

    //setting up onClickListener for starting point toggle button. Press the button and select the starting point on the map
    public void onClickSetStartPoint(){
        tb_setStartCoord = getActivity().findViewById(R.id.tb_setStartCoord);
        tb_setStartCoord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //first check if bluetooth is connected to any device
                if (connectedDevice == null) {
                    Toast.makeText(getContext(), "Please connect to a device first", Toast.LENGTH_SHORT).show();
                } else {
                    if(isChecked){
                        //toggle is enabled: select the starting coordinates on the map
                        //TODO: Use the function in map to set starting coordinates and starting direction for the robot

                        //Change the button back to disabled once the waypoint has been set
                        tb_setStartCoord.toggle();
                    }
                }
            }
        });
    }

    // Start Exploration button
    public void onClickStartExploration(){
        tb_exploration = getActivity().findViewById(R.id.tb_exploration);
        tb_exploration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (connectedDevice == null) {
                    Toast.makeText(getContext(), "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecked) {
                        // The toggle is enabled; Start Exploration Mode
                        //startExploration();
                    }
                }
            }
        });
    }

    // Broadcast Receiver for Bluetooth Connection Status
    BroadcastReceiver btConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "Receiving btConnectionStatus Msg!!!");

            String connectionStatus = intent.getStringExtra("ConnectionStatus");
            myBTConnectionDevice = intent.getParcelableExtra("Device");

            if (connectionStatus.equals("disconnected")) {
                //connect device
            } else {
                //device already connected
            }
        }
    };


    // Broadcast Receiver for incoming messages
    BroadcastReceiver incomingMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String allMsg = intent.getStringExtra("receivingMsg");

            Log.d(TAG, "Receiving incoming message: " + allMsg);
        }
    };

}
