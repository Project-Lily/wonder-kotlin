package com.projectlily.wonderreader.services

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
import android.os.Build
import android.os.IBinder
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.util.LinkedList
import java.util.Queue
import java.util.UUID
import java.util.Vector
import java.util.function.Consumer

@SuppressLint("MissingPermission")
class QNACommunicationService : Service() {
    companion object {
        const val QNA_READY = "wonderreader.ready"

        private val QNA_SERVICE_UUID = UUID.fromString("0000abab-0000-1000-8000-00805f9b34fb")
        private val QNA_CHARACTERISTIC_TEST_UUID =
            UUID.fromString("0000bbbb-0000-1000-8000-00805f9b34fb")
        private val QNA_CHARACTERISTIC_QNA_UUID =
            UUID.fromString("0000cccc-0000-1000-8000-00805f9b34fb")

        private const val TAG = "QNA Service"
        private const val CLIENT_CHARACTERISTIC_CONFIGURATION_UUID =
            "00002902-0000-1000-8000-00805f9b34fb"

        private const val WRITE_CHUNK_SIZE = 20
    }

    private val binder = LocalBinder()
    private var btService: BLEService? = null
    private var callbacks: HashMap<String, ArrayList<Consumer<JSONObject>>> = HashMap()

    // TODO: This does not support multiple BT devices!
    private val packageBuilder: Vector<Byte> = Vector()
    private val writeQueue: Queue<String> = LinkedList()

    private val btIntentFilter = IntentFilter().apply {
        addAction(BLEService.GATT_CONNECTED)
        addAction(BLEService.GATT_DISCONNECTED)
        addAction(BLEService.GATT_SERVICES_DISCOVERED)
        addAction(BLEService.GATT_NOTIFICATION)
        addAction(BLEService.GATT_READ)
        addAction(BLEService.GATT_WRITE)
    }

    private val btServiceReceiver = object : BroadcastReceiver() {
        private var counter = 0
        private var startTime: Long = 0L

        private fun onDataComplete() {
            Log.i(
                TAG,
                "Chunk load complete in $counter transfers in ${System.currentTimeMillis() - startTime}ms"
            )
            val strData = String(packageBuilder.toByteArray(), Charsets.UTF_8)
            val jsonData = JSONObject(strData)
            val event = try {
                jsonData.getString("event")
            } catch (_: JSONException) {
                ""
            }
            callbacks[event]?.forEach(Consumer { consumer -> consumer.accept(jsonData) })
        }

        override fun onReceive(c: Context, intent: Intent) {
            when (intent.action) {
                BLEService.GATT_SERVICES_DISCOVERED -> {
                    val address = intent.getStringExtra(BLEService.GATT_INTENT_ADDRESS) ?: return
                    val gatt = btService?.getGatt(address) ?: return
                    val characteristic = getQNACharacteristic(address) ?: return

                    // Enable indication. This whole song and dance must be done to enable indication
                    val indicateResult = gatt.setCharacteristicNotification(characteristic, true)
                    val descriptor = characteristic.getDescriptor(
                        UUID.fromString(CLIENT_CHARACTERISTIC_CONFIGURATION_UUID)
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Log.i(
                            TAG,
                            "Success Indicate ($address) ${
                                gatt.writeDescriptor(
                                    descriptor,
                                    BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                                )
                            } $indicateResult"
                        )
                    } else {
                        descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                        Log.i(
                            TAG,
                            "Success Indicate ($address) " + (gatt.writeDescriptor(descriptor) && indicateResult)
                        )
                    }
                    val qnaReadyIntent = Intent(QNA_READY)
                    sendBroadcast(qnaReadyIntent)
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

                    // Reset packageBuilder
                    packageBuilder.clear()
                    counter = 0
                    startTime = System.currentTimeMillis()
                }

                BLEService.GATT_READ -> {
                    val data = intent.getByteArrayExtra(BLEService.GATT_INTENT_DATA) ?: return
                    packageBuilder.addAll(data.asIterable())

                    if (data[data.size - 1] == 0.toByte()) {
                        packageBuilder.removeAt(packageBuilder.size - 1)
                        onDataComplete()
                    } else {
                        // If a null terminator character is not found, then continue reading
                        val address =
                            intent.getStringExtra(BLEService.GATT_INTENT_ADDRESS) ?: return
                        val gatt = btService?.getGatt(address) ?: return
                        val characteristic = getQNACharacteristic(address) ?: return
                        Log.i(
                            TAG,
                            "Read chunk ($counter): " + gatt.readCharacteristic(characteristic)
                        )
                    }
                    counter++
                }

                BLEService.GATT_WRITE -> {
                    // See if there is more to write
                    val address = intent.getStringExtra(BLEService.GATT_INTENT_ADDRESS) ?: return
                    val gatt = btService?.getGatt(address) ?: return
                    val characteristic = getQNACharacteristic(address) ?: return
                    if (writeQueue.size > 0) {
                        // Get string
                        val data = writeQueue.remove()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            gatt.writeCharacteristic(
                                characteristic,
                                data.toByteArray(),
                                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                            )
                        } else {
                            characteristic.value = data.toByteArray()
                            gatt.writeCharacteristic(characteristic)
                        }
                    }
                }
            }
        }
    }

    private fun getQNACharacteristic(address: String): BluetoothGattCharacteristic? {
        return btService?.getGatt(address)?.getService(QNA_SERVICE_UUID)?.getCharacteristic(
            QNA_CHARACTERISTIC_QNA_UUID
        ) ?: return null
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

    fun onAnswerReceive(callback: Consumer<JSONObject>) {
        val checkArr = callbacks["answer"]
        if (checkArr == null) {
            val arr = ArrayList<Consumer<JSONObject>>()
            arr.add(callback)
            callbacks["answer"] = arr
        } else {
            checkArr.add(callback)
        }
    }

    fun removeOnAnswerReceive(callback: Consumer<JSONObject>): Boolean {
        return callbacks["answer"]?.remove(callback) == true
    }

    // TODO: @Aric, @JJ, you might wanna change the datatype for this. Just send a json as string
    // thru bluetooth yeah?
    fun sendQuestion(question: String): Boolean {
        try {
            btService?.getDevices()?.forEach { devices ->
                val characteristic = getQNACharacteristic(devices.address)
                    ?: throw NullPointerException("No Characteristic")
                val gatt =
                    btService?.getGatt(devices.address) ?: throw NullPointerException("No GATT")

                // Split the question to chunks
                val chunks = (question.length / WRITE_CHUNK_SIZE) + 1
                for (i in 0 until chunks) {
                    if (i + 1 == chunks) {
                        // If this is the last packet
                        // Add a null terminator
                        writeQueue.add(question.substring(i * WRITE_CHUNK_SIZE) + '\u0001')
                    } else {
                        writeQueue.add(
                            question.substring(
                                i * WRITE_CHUNK_SIZE,
                                (i + 1) * WRITE_CHUNK_SIZE
                            )
                        )
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    gatt.writeCharacteristic(
                        characteristic,
                        writeQueue.remove().toByteArray(Charsets.UTF_8),
                        BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                    )
                } else {
                    val data = writeQueue.remove()
                    Log.i(TAG, "Writing data: $data")
                    characteristic.value = data.toByteArray(Charsets.UTF_8)
                    gatt.writeCharacteristic(characteristic)
                }
            }
        } catch (e: NullPointerException) {
            return false
        }
        return true
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Created service")
        Log.d("BLE Time", "We're connected")
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
        fun getService(): QNACommunicationService {
            return this@QNACommunicationService
        }
    }
}