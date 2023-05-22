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
import java.util.UUID
import java.util.function.Consumer


@SuppressLint("MissingPermission")
class BLEService : Service() {

    companion object {
        const val GATT_CONNECTED = "wonderreader.gatt.connected"
        const val GATT_DISCONNECTED = "wonderreader.gatt.disconnected"
        const val BLETAG = "BT Service"
    }

    private val binder = LocalBinder()
    private var btAdapter : BluetoothAdapter? = null
    private var btGatts : ArrayList<BluetoothGatt> = ArrayList()
    private var scanning : Boolean = false

    private val btGattCallback = object : BluetoothGattCallback() {
        private fun enableIndications(gatt: BluetoothGatt?, c: BluetoothGattCharacteristic?) {
            if (c == null || gatt == null) return
            gatt.setCharacteristicNotification(c, true)
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(BLETAG, "GATT Connected " + gatt?.device?.address)
                gatt?.discoverServices()
                broadcastUpdate(GATT_CONNECTED)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(BLETAG, "GATT Disconnected " + gatt?.device?.address)
                broadcastUpdate(GATT_DISCONNECTED)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Log.i(BLETAG, "Service discovered")
            enableIndications(gatt, gatt?.getService(UUID.fromString("0000abab-0000-1000-8000-00805f9b34fb"))?.getCharacteristic(UUID.fromString("0000bbbb-0000-1000-8000-00805f9b34fb")))
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            Log.i(BLETAG, "Characteristic read value is: " + String(value, Charsets.UTF_8))
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            Log.i(BLETAG, "Characteristic indication value is: " + String(value, Charsets.UTF_8))
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


    private var callbacks: HashMap<String, ArrayList<Consumer<*>>> = HashMap()

    fun <T> onReceive(event: String, callback: Consumer<T>) {
        callbacks[event]?.add(callback) ?: {
            val arr = ArrayList<Consumer<*>>()
            arr.add(callback)
            callbacks[event] = arr
        }
    }

    // Use this function to broadcast activities of bluetooth updates
    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    fun initialize(): Boolean {
        val btService = getSystemService(Context.BLUETOOTH_SERVICE) ?: return false
        val btManager = btService as BluetoothManager
        btAdapter = btManager.adapter
        return true
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
        Log.i(BLETAG, "Start scan")

        btAdapter?. let { adapter ->
            val scanner = adapter.bluetoothLeScanner
            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    val device = result?.device
                    if (device != null && device.name == name) {
                        Log.i(BLETAG, "Found device '" + device.name + "(" + device.address + ")'")
                        // Check if it's on the list or not
                        if (btGatts.find { bt -> bt.device.address == device.address } != null) return
                        Log.i(BLETAG, "Connecting to GATT device " + device.address)
                        btGatts.add(device.connectGatt(this@BLEService, false, btGattCallback))
                    }
                }
            }
            scanner.startScan(scanCallback)
            scanning = true
            Handler(mainLooper).postDelayed({
                Log.i(BLETAG, "Stop scan")
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