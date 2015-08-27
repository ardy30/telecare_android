/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ebti.mobile.dohtelecare.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ebti.mobile.dohtelecare.service.GetBlueToothDeviceDataService;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BTWatchBPhomeConnService {
    // Debugging
    private static final String TAG = "BTWatchBPhomeConnService";

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BTWBPHSecure";
    private static final String NAME_INSECURE = "BTWBPHInsecure";

    // Unique UUID for this application    	// mb-ebti
    private static final UUID MY_UUID_SECURE =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        //UUID.fromString("0aa4fc20-29f0-11e1-9314-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
        UUID.fromString("1ac9d3f0-29f0-11e1-9314-0800200c9a66");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private Context mContext;
    //private BloodGlucoseActivity	mMainUi;
    private GetBlueToothDeviceDataService	mMainUi;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 10;       // we're doing nothing
    public static final int STATE_LISTEN = 11;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 12; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 13;  // now connected to a remote device
    
    /**
     * Constructor. Prepares a new BHDC session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    //public BTAsVoice2GSConnService(BloodGlucoseActivity mainui, Handler handler) {
    public BTWatchBPhomeConnService(GetBlueToothDeviceDataService mainui, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
        mMainUi = mainui;
        mContext = mainui.getApplicationContext();
    }

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
       Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(GetBlueToothDeviceDataService.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }




    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        Log.d(TAG, "BTWatchBPhomeConnService.start()");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            Log.d(TAG, " start() .... Secure Accept Thread .... ");
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }
        if (mInsecureAcceptThread == null) {
            Log.d(TAG, " start() .... Insecure Accept Thread .... ");
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
    }




    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */

    public synchronized void connect(BluetoothDevice device, boolean secure) {
       Log.d(TAG, "connect ()");
       Log.d(TAG, "connect () : connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }





    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {

        Log.d(TAG, "BTWatchBPhomeConnService.connected()");
 
       Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
        	mConnectThread.cancel(); 
        	mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
        	mConnectedThread.cancel(); 
        	mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(GetBlueToothDeviceDataService.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(GetBlueToothDeviceDataService.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
       Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
    	Log.d(TAG, "connectionFailed ()");
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(GetBlueToothDeviceDataService.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(GetBlueToothDeviceDataService.TOAST, "藍芽連線失敗");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BTWatchBPhomeConnService.this.start();
    }
    
    /*  not used --> can be removed */
    /*
    void ebti_issue_toast_msg (String inmsg)
    {
	    Message msg = mHandler.obtainMessage(BHDevActivity.MESSAGE_TOAST);
	    Bundle bundle = new Bundle();
	    bundle.putString(BHDevActivity.TOAST, inmsg);
	    msg.setData(bundle);
	    mHandler.sendMessage(msg);
	}
	*/
    
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(GetBlueToothDeviceDataService.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(GetBlueToothDeviceDataService.TOAST, "藍芽連線中斷");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BTWatchBPhomeConnService.this.start();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        @SuppressLint("NewApi")
		public AcceptThread(boolean secure) {
            Log.d(TAG, "AcceptThread () : inparam:"+secure);            
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure":"Insecure";

            // Create a new listening server socket
            try {
                if (secure) {
                	Log.i(TAG, "SET MY_UUID_SECURE!!");
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
                        MY_UUID_SECURE);
                } else {
                	Log.i(TAG, "SET MY_UUID_INSECURE");
                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                            NAME_INSECURE, MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }
            mmServerSocket = tmp;
            Log.d(TAG, "new AcceptThread () : Socket Type: " + mSocketType +
                    "mmServerSocket= " + mmServerSocket);
         }

        @Override
		public void run() {
            Log.d(TAG, "run () : Socket Type: " + mSocketType +
                    "BEGIN mAcceptThread" + this + "  mmServerSocket= " + mmServerSocket);
            Log.d(TAG, "run () : mmServerSocket= " + mmServerSocket);
            Log.d(TAG, "run () : mState= " + mState);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

			int ctr = 0;
            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
				ctr ++;
                Log.d(TAG, "accept socket retry ctr= "+ctr);
				
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                    Log.d(TAG, "Socket Type: " + mSocketType + "accept() succeeded" + socket);
                 } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    break;
                }
                Log.d(TAG, "Socket Type: " + mSocketType + "accept() succeeded" + socket);

                Log.d(TAG, "mState" + mState);


                // If a connection was accepted
                if (socket != null) {
                    synchronized (BTWatchBPhomeConnService.this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            connected(socket, socket.getRemoteDevice(),
                                    mSocketType);
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Could not close unwanted socket", e);
                            }
                            break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);
        }

        public void cancel() {
            Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */

	private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

		public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID_SECURE);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(
                            MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }

        @Override
		public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                Log.e(TAG, "unable to connect() " + mSocketType +
                        " socket during connection failure", e);
               // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BTWatchBPhomeConnService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        int indexBuffer = 0;
        

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        
        
        @Override
		public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            byte[] outbuf;
            int i;
            byte[] recbuffer = new byte[4098];
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    //Log.i(TAG, "mConnectedThread : waiting for input ...");
                    bytes = mmInStream.read(buffer);
                    
                   //Log.i(TAG, "echo received number of bytes= "+bytes);
                    //outbuf = new byte[bytes+1];
                    //outbuf[0] = (byte) bytes;  // length of input message
                    String strbuffer ="";
                    for (i=0;i<bytes;i++)
                    {
                    	if(indexBuffer == 4098){
                    		indexBuffer = 0;
                    	}
                    	//if (D) Log.i(TAG, "flybeeMinuteData echo received: buffer["+i+"]="+buffer[i]);
                    	//outbuf[i+1] = buffer[i];
                    	strbuffer += Integer.toHexString(buffer[i]) + " ";
                    	Log.i(TAG, "read buffer[" + i + "] : " + Integer.toHexString(buffer[i]));
                    	recbuffer[indexBuffer] = buffer[i];
                    	indexBuffer++;
                    	//rebuffer += buffer[i]+",";
                    }
                    Log.i(TAG, "get strbuffer : " + strbuffer);
                    
                    if(indexBuffer == ((recbuffer[2] & 0xFF) + ((recbuffer[3] & 0xFF) << 4) + 3)){
                    	strbuffer ="";
            			Log.i(TAG, "indexBuffer : " + indexBuffer + ", read data length : " + ((recbuffer[2] & 0xFF) + ((recbuffer[3] & 0xFF) << 4) + 3) );
            			//sleep(1000);
            			//送出確認接收的command
            			//write(CMD_REQUEST_DUMPING_BUFFER_END);
            			
            			outbuf = new byte[indexBuffer + 1];
            			if(indexBuffer < 4098){
                			for(int j=0;j<indexBuffer;j++){
                				outbuf[j] = recbuffer[j];
                				strbuffer += Integer.toHexString(outbuf[j]) + ", ";
                			}
            			}else{
            				outbuf = recbuffer;
            			}
            			Log.i(TAG, "connect read indexBuffer : " + indexBuffer + ", strbuffer : " + strbuffer);
            			mMainUi.mSynchMsgQueue.offer(outbuf);
                    }
                    /*
                    String echoReceived = "";
                    for (i=0;i<bytes;i++)
                    {
                    	echoReceived += Integer.toHexString(buffer[i]) + ", ";
                    	outbuf[i+1] = buffer[i];
                    }
                    Log.i(TAG, "echo received : " + echoReceived);
                    // Send the obtained bytes to the UI Activity
                    //BHDCActivity.mSynchMsgQueue.offer(new String(buffer,0,bytes));
                    mMainUi.mSynchMsgQueue.offer(outbuf);
                    //mHandler.obtainMessage(BHDevActivity.MESSAGE_READ, bytes, -1, buffer)
                    //        .sendToTarget();
                    */
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    // Start the service over to restart listening mode
                    BTWatchBPhomeConnService.this.start();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                //mHandler.obtainMessage(BHDevActivity.MESSAGE_WRITE, -1, -1, buffer)
                //        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}

