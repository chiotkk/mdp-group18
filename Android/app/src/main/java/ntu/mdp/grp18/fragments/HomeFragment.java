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
import ntu.mdp.grp18.PixelGridView;
import ntu.mdp.grp18.R;

public class HomeFragment extends Fragment {
    //Declaring variable for the map i.e. the PixelGridView
    PixelGridView map;
    // Declarations for the main screen with map
    ImageButton forwardButton, leftButton, rightButton, reverseButton;
    TextView tv_roboStatus, tv_myStringCmd;
    Button btn_update;
    ToggleButton tb_setWaypointCoord, tb_setStartCoord, tb_autoManual, tb_fastestpath, tb_exploration;
    private static final String TAG = "HomeFragment";


    //Declarations for the bluetooth connection
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
        onClickStartFastestPath();
        onClickStartExploration();
        onClickAutoOrManualUpdate();
        onClickUpdateBtn();

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

                    //TODO: send command based on protocol to RPI using btChat to tell the bot to move forward

                    Log.d(TAG, "Android Controller: Move Forward");
                    tv_roboStatus.append("Moving/n");
                    tv_myStringCmd.append("Android Controller: Move Forward");
                    map.moveForward();
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
                    tv_roboStatus.append("Moving\n");
                    tv_myStringCmd.append("Android Controller: Rotate 180 \n");
                    map.moveBackwards();
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
                    tv_roboStatus.append("Moving\n");
                    tv_myStringCmd.append("Android Controller: Turn Left\n");
                    map.rotateLeft();
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
                    tv_roboStatus.append("Moving\n");
                    tv_myStringCmd.append("Android Controller: Turn Right\n");
                    map.rotateRight();
                }
            }
        });
    }

    //setting up onClickListener for waypoint toggle button. Press the button and select the waypoint on the map
    public void onClickSetWaypoint() {
        tb_setWaypointCoord = getActivity().findViewById(R.id.tb_setWaypointCoord);
        tb_setWaypointCoord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //first check if bluetooth is connected to any device
                if (connectedDevice == null) {
                    Toast.makeText(getContext(), "Please connect to a device first", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecked) {
                        //toggle is enabled; select waypoint on the map
                        map.selectWayPoint();
                        //Change the button back to disabled once the waypoint has been set
                        tb_setWaypointCoord.toggle();
                    }
                }
            }
        });
    }

    //setting up onClickListener for starting point toggle button. Press the button and select the starting point on the map
    public void onClickSetStartPoint() {
        tb_setStartCoord = getActivity().findViewById(R.id.tb_setStartCoord);
        tb_setStartCoord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //first check if bluetooth is connected to any device
                if (connectedDevice == null) {
                    Toast.makeText(getContext(), "Please connect to a device first", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecked) {
                        //toggle is enabled: select the starting coordinates on the map
                        map.selectStartPoint();
                        setStartDirection();
                        //Change the button back to disabled once the waypoint has been set
                        tb_setStartCoord.toggle();
                    }
                }
            }
        });
    }

    // Start Exploration button
    public void onClickStartExploration() {
        tb_exploration = getActivity().findViewById(R.id.tb_exploration);
        tb_exploration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (connectedDevice == null) {
                    Toast.makeText(getContext(), "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecked) {
                        // The toggle is enabled; Start Exploration Mode
                        startExploration();
                    }
                }
            }
        });
    }

    //set up onClickListener for fastest path
    public void onClickStartFastestPath() {
        tb_fastestpath = getActivity().findViewById(R.id.tb_fastestpath);
        tb_fastestpath.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (connectedDevice == null) {
                    Toast.makeText(getContext(), "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecked) {
                        //The toggle is enabled: start fastest path mode
                        startFastestPath();
                    }
                }
            }
        });

    }

    //set up onClickListener for manual/auto update of the map
    public void onClickAutoOrManualUpdate() {
        tb_autoManual = getActivity().findViewById(R.id.tb_autoManual);
        tb_autoManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (connectedDevice == null) {
                    Toast.makeText(getContext(), "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecked) {
                        //The toggle is enabled: manual mode on

                        // Direction buttons are disabled
                        // Update button is enabled
                        btn_update.setEnabled(true);
                        forwardButton.setEnabled(false);
                        leftButton.setEnabled(false);
                        rightButton.setEnabled(false);
                        reverseButton.setEnabled(false);
                        Toast.makeText(getContext(), "Manual Mode enabled", Toast.LENGTH_SHORT).show();
                        map.setAutoUpdate(false);
                        Log.d(TAG, "Auto updates disabled.");

                    } else {
                        //Toggle is not enabled: that means auto mode is on

                        // Update button is disabled
                        // Direction buttons are enabled
                        map.refreshMap(true);
                        btn_update.setEnabled(false);
                        forwardButton.setEnabled(true);
                        leftButton.setEnabled(true);
                        rightButton.setEnabled(true);
                        reverseButton.setEnabled(true);
                        Toast.makeText(getContext(), "Auto Mode enabled", Toast.LENGTH_SHORT).show();
                        map.setAutoUpdate(true);
                        Log.d(TAG, "Auto updates enabled.");
                    }
                }
            }
        });
    }

    //update button for updating the map when manual mode is on
    public void onClickUpdateBtn() {
        // Manual Mode; Update button
        btn_update = (Button) getActivity().findViewById(R.id.update_btn);
        btn_update.setEnabled(false);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Updating....");
                map.refreshMap(true);
                Log.d(TAG, "Update completed!");
                Toast.makeText(getContext(), "Update completed", Toast.LENGTH_SHORT).show();
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

    //Setting start point direction
    public void setStartDirection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Robot Direction")
                .setItems(R.array.directions_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        map.setRobotDirection(i);
                        dialog.dismiss();
                        Log.d(TAG, "Start Point Direction set");
                    }
                });
        builder.create();
        builder.create().show();
    }

    // Function to send the  Algo the command to start Exploration
    //Triggered by the click of the explore button
    public void startExploration() {
        Toast.makeText(getContext(), "Exploration started", Toast.LENGTH_SHORT).show();
        //TODO: Send the command on the output stream to start exploration

        Log.d(TAG, "Android Controller: Start Exploration");
        tv_myStringCmd.append("Start Exploration\n");
        tv_roboStatus.append("Moving\n");
    }

    // End Exploration
    public void endExploration() {
        Log.d(TAG, "Algorithm: End Exploration");
        tv_myStringCmd.append("End Exploration\n");
        tv_roboStatus.append("Stop\n");
        Toast.makeText(getContext(), "Exploration ended", Toast.LENGTH_SHORT).show();
    }

    // Start Fastest Path
    public void startFastestPath() {
        Toast.makeText(getContext(), "Fastest Path started", Toast.LENGTH_SHORT).show();
        //TODO: Send the command to the algo using btChat to start the fastest path

        Log.d(TAG, "Android Controller: Start Fastest Path");
        tv_myStringCmd.append("Start Fastest Path\n");
        tv_roboStatus.append("Moving\n");
    }

    // End Fastest Path
    public void endFastestPath() {
        Log.d(TAG, "Algorithm: Fastest Path Ended.");
        tv_myStringCmd.append("End Fastest Path\n");
        tv_roboStatus.append("Stop\n");
        Toast.makeText(getContext(), "Fastest Path ended", Toast.LENGTH_SHORT).show();
    }

}
