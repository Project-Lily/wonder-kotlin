package com.projectlily.wonderreader.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat


@SuppressLint("MissingPermission")
class BLEService : Service() {

    companion object {
        const val GATT_CONNECTED = "wonderreader.gatt.connected"
        const val GATT_DISCONNECTED = "wonderreader.gatt.disconnected"
        const val GATT_SERVICES_DISCOVERED = "wonderreader.gatt.service.discovered"
        const val GATT_NOTIFICATION = "wonderreader.gatt.notification"
        const val GATT_READ = "wonderreader.gatt.read"

        const val GATT_INTENT_DATA = "data"
        const val GATT_INTENT_ADDRESS = "address"

        private const val TAG = "BT Service"
    }

    private val binder = LocalBinder()
    var btAdapter : BluetoothAdapter? = null
    private var btGatts : ArrayList<BluetoothGatt> = ArrayList()
    private var scanning : Boolean = false

    override fun onCreate() {
        super.onCreate()
        val btService = getSystemService(Context.BLUETOOTH_SERVICE)
        val btManager = btService as BluetoothManager
        btAdapter = btManager.adapter
    }

    private val btGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "GATT Connected " + gatt?.device?.address)
                gatt?.discoverServices()
                broadcastUpdate(GATT_CONNECTED)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "GATT Disconnected " + gatt?.device?.address)
                broadcastUpdate(GATT_DISCONNECTED)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Log.i(TAG, "Service discovered")
            if (gatt != null) {
                val intent = Intent(GATT_SERVICES_DISCOVERED)
                intent.putExtra(GATT_INTENT_ADDRESS, gatt.device.address)
                sendBroadcast(intent)
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            Log.i(TAG, "Characteristic read value is: " + String(value, Charsets.UTF_8))
            val intent = Intent(GATT_READ)
            intent.putExtra(GATT_INTENT_DATA, value)
            intent.putExtra(GATT_INTENT_ADDRESS, gatt.device.address)
            sendBroadcast(intent)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            Log.i(TAG, "Characteristic indication value is: " + String(value, Charsets.UTF_8))
            val intent = Intent(GATT_NOTIFICATION)
            intent.putExtra(GATT_INTENT_DATA, value)
            intent.putExtra(GATT_INTENT_ADDRESS, gatt.device.address)
            sendBroadcast(intent)
        }

        // For this, I thank my man mxkmn
        // https://stackoverflow.com/a/75783347/12709867
        @Suppress("DEPRECATION")
        @Deprecated(
            "Used natively in Android 12 and lower",
            ReplaceWith("onCharacteristicRead(gatt, characteristic, characteristic.value, status)")
        )
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) = onCharacteristicRead(gatt, characteristic, characteristic.value, status)

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        @Suppress("DEPRECATION")
        @Deprecated(
            "Used natively in Android 12 and lower",
            ReplaceWith("onCharacteristicChanged(gatt, characteristic, characteristic.value)")
        )
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) = onCharacteristicChanged(gatt, characteristic, characteristic.value)
    }

    fun getDevices() : List<BluetoothDevice> {
        return btGatts.map { bt -> bt.device }
    }

    fun getGatt(address: String) : BluetoothGatt? {
        return btGatts.find { gatt -> gatt.device.address == address }
    }

    // Use this function to broadcast activities of bluetooth updates
    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    fun scanAndConnect(name: String, length: Long = 5000L): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        // If still scanning, don't do anything
        if (scanning) return false
        Log.i(TAG, "Start scan")

        btAdapter?. let { adapter ->
            val scanner = adapter.bluetoothLeScanner
            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    val device = result?.device
                    if (device != null && device.name == name) {
                        Log.i(TAG, "Found device '" + device.name + "(" + device.address + ")'")
                        // Check if it's on the list or not
                        if (btGatts.find { bt -> bt.device.address == device.address } != null) return
                        Log.i(TAG, "Connecting to GATT device " + device.address)
                        btGatts.add(device.connectGatt(this@BLEService, false, btGattCallback))
                    }
                }
            }
            scanner.startScan(scanCallback)
            scanning = true
            Handler(mainLooper).postDelayed({
                Log.i(TAG, "Stop scan")
                scanning = false
                btAdapter?.bluetoothLeScanner?.flushPendingScanResults(scanCallback)
                btAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
            }, length)
            return true
        } ?: run {
            return false
        }
    }

    private fun close() {
        btGatts.forEach { bt -> bt.close() }
        btGatts.clear()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService(): BLEService {
            return this@BLEService
        }
    }
}