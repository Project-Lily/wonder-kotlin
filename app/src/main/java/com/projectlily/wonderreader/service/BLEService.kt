package com.projectlily.wonderreader.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
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
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.util.UUID

@SuppressLint("MissingPermission")
class BLEService : Service() {

    private val binder = LocalBinder()
    private var btAdapter : BluetoothAdapter? = null
    private var btGatt : BluetoothGatt? = null

    private val btGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("BLESERVICE", "GATT Connected")
                gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("BLESERVICE", "GATT Disconnected")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Log.i("BLESERVICe", "Service discovered")
            val c = gatt?.getService(UUID.fromString("0000abab-0000-1000-8000-00805f9b34fb"))?.getCharacteristic(UUID.fromString("0000bbbb-0000-1000-8000-00805f9b34fb"));
            Log.i("BLESERVICE", " value : " + c?.value.toString() + " Characteristic read: " + gatt?.readCharacteristic(c))
            gatt?.services?.forEach{ a ->
                run {
                    Log.i("BLESERVICE", "Services: " + a.uuid.toString())
                    a.characteristics.forEach { b ->
                        run {
                            Log.i("MyApp", "Characteristics: " + b.uuid.toString() + " value: " + b.value)
                            gatt.readCharacteristic(b)
                        }
                    }
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            Log.i("BLESERVICE", "Characteristic read value is: " + String(value, Charsets.UTF_8))
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

    fun scanAndConnect(name: String): Boolean {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        btAdapter?. let { adapter ->
            val scanner = adapter.bluetoothLeScanner
            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    val device = result?.device
                    if (device != null && device.name == name) {
                        btGatt = device.connectGatt(this@BLEService, false, btGattCallback)
                        scanner.stopScan(this)
                    }
                }
            }
            scanner.startScan(scanCallback)
            return true
        } ?: run {
            return false
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): BLEService {
            return this@BLEService
        }
    }
}