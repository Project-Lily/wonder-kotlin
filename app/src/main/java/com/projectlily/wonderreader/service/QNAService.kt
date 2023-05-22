package com.projectlily.wonderreader.service

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.UUID
import java.util.function.Consumer

@SuppressLint("MissingPermission")
class QNAService : Service() {
    companion object {
        private val QNA_SERVICE_UUID = UUID.fromString("0000abab-0000-1000-8000-00805f9b34fb")
        private val QNA_CHARACTERISTIC_TEST_UUID = UUID.fromString("0000bbbb-0000-1000-8000-00805f9b34fb")
        private val QNA_CHARACTERISTIC_QNA_UUID =
            UUID.fromString("0000cccc-0000-1000-8000-00805f9b34fb")

        private const val TAG = "QNA Service"
        private val CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = "00002902-0000-1000-8000-00805f9b34fb"
    }

    private val binder = LocalBinder()
    private var btService: BLEService? = null
    private var callbacks: HashMap<String, ArrayList<Consumer<*>>> = HashMap()

    private val btIntentFilter = IntentFilter().apply {
        addAction(BLEService.GATT_CONNECTED)
        addAction(BLEService.GATT_DISCONNECTED)
        addAction(BLEService.GATT_SERVICES_DISCOVERED)
        addAction(BLEService.GATT_NOTIFICATION)
        addAction(BLEService.GATT_READ)
    }

    private val btServiceReceiver = object : BroadcastReceiver() {
        fun getQNACharacteristic(address: String): BluetoothGattCharacteristic? {
            return btService?.getGatt(address)?.getService(QNA_SERVICE_UUID)?.getCharacteristic(
                QNA_CHARACTERISTIC_QNA_UUID
            ) ?: return null
        }
        var counter = 0
        override fun onReceive(c: Context, intent: Intent) {
            when (intent.action) {
                BLEService.GATT_SERVICES_DISCOVERED -> {
                    val address = intent.getStringExtra(BLEService.GATT_INTENT_ADDRESS) ?: return
                    val gatt = btService?.getGatt(address) ?: return
                    val characteristic = getQNACharacteristic(address) ?: return

                    // Enable indication. This whole song and dance must be done to enable indication
                    val indicateResult = gatt.setCharacteristicNotification(characteristic, true)
                    val descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIGURATION_UUID))
                    descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                    Log.i(TAG, "Success Indicate ($address) " + (gatt.writeDescriptor(descriptor) && indicateResult))
                }
                BLEService.GATT_NOTIFICATION -> {
                    val address = intent.getStringExtra(BLEService.GATT_INTENT_ADDRESS) ?: return
                    val gatt = btService?.getGatt(address) ?: return
                    val characteristic = getQNACharacteristic(address) ?: return

                    // Read indication
                    val data = intent.getByteArrayExtra(BLEService.GATT_INTENT_DATA) ?: return
                    Log.i(TAG, String(data, Charsets.UTF_8))

                    // Read the characteristic in chunks
                    Log.i(TAG, "Read chunk: " + gatt.readCharacteristic(characteristic))
                    counter++
                }
                BLEService.GATT_READ -> {
                    if (counter > 20) return
                    val data = intent.getByteArrayExtra(BLEService.GATT_INTENT_DATA) ?: return
                    Log.i(TAG, "Read this: " + String(data, Charsets.UTF_8))

                    if ((data.find { b -> b == 0.toByte() }) != null) {
                        Log.i(TAG, "Done reading chunk");
                    } else {
                        // If a null terminator character is not found, then continue reading
                        val address = intent.getStringExtra(BLEService.GATT_INTENT_ADDRESS) ?: return
                        val gatt = btService?.getGatt(address) ?: return
                        val characteristic = getQNACharacteristic(address) ?: return
                        Log.i(TAG, "Read chunk: " + gatt.readCharacteristic(characteristic))
                    }
                }
            }
        }
    }

    private val btServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
            btService = (service as BLEService.LocalBinder).getService()
            btService?.scanAndConnect("Wonder Reader 2")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            btService = null
        }
    }

    fun <T> onReceive(event: String, callback: Consumer<T>) {
        callbacks[event]?.add(callback) ?: {
            val arr = ArrayList<Consumer<*>>()
            arr.add(callback)
            callbacks[event] = arr
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Created service")
        registerReceiver(btServiceReceiver, btIntentFilter)
        val intent = Intent(this, BLEService::class.java)
        bindService(intent, btServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        callbacks.clear()
        unregisterReceiver(btServiceReceiver)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService(): QNAService {
            return this@QNAService
        }
    }
}