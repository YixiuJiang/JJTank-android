package com.jjstudio.jjtank

import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import android.bluetooth.BluetoothGattCharacteristic
import java.util.*


class SplashActivity : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds
    lateinit var bleManager: BluetoothManager
    lateinit var bleAdapter: BluetoothAdapter
    lateinit var bleScanner: BluetoothLeScanner
    lateinit var bluetoothGatt: BluetoothGatt
    lateinit var tank: Tank
    internal val tanks = ArrayList<Tank>()
    lateinit var tankInfoTextView: TextView
    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            toast("Scanning tanks...")
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        tankInfoTextView = this.tankInfos
        bleManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bleAdapter = bleManager.adapter
        //Initialize the Handler
        mDelayHandler = Handler()
        //Navigate with delay
        val rv: RecyclerView = this.tankListView
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        var tankAdapter = TankAdapter(tanks, listener)
        rv.adapter = tankAdapter
        startScanning()
    }

    public override fun onDestroy() {
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }

    fun startScanning() {
        toast("Scanning tanks...")

        AsyncTask.execute(object : Runnable {
            override fun run() {
                bleScanner = bleAdapter.bluetoothLeScanner
                bleScanner.startScan(leScanCallback)
            }
        })

    }

    fun stopScanning() {
        toast("Stop scanning tanks...")
        AsyncTask.execute(object : Runnable {
            override fun run() {
                bleScanner.stopScan(leScanCallback)
            }
        })
    }

    private val leScanCallback = object : ScanCallback(), AnkoLogger {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            addScanResult(result)
            info("I found a ble device ${result?.device?.address}")
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach { result -> addScanResult(result) }
        }

        override fun onScanFailed(errorCode: Int) {
            info("Bluetooth LE scan failed. Error code: $errorCode")
        }

        fun addScanResult(scanResult: ScanResult?) {
            val bleDevice = scanResult?.device
            val deviceAddress = bleDevice?.address
            val deviceName = bleDevice?.name
            if (tanks.filter { tank -> tank.uuid.equals(deviceAddress) }.isEmpty() && !deviceName.isNullOrBlank() && deviceName!!.startsWith("JJtk")) {
                tanks.add(Tank(" $deviceName -  $deviceAddress", deviceAddress, StatusEnum.Disconnected, bleDevice))
                info("Found LE device: $deviceAddress")
                tankInfoTextView.append("\nfound device... $deviceName -  $deviceAddress")
            }
        }
    }
    private val leConnectCallback = object : BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_CONNECTED) {
                //Discover services
                gatt!!.discoverServices()
            } else if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_DISCONNECTED) {
                toast("Tank ${tank.title} disconnected")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            bluetoothGatt = gatt!!
                    mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

//            val ch =  BluetoothGattCharacteristic (
//                    UUID.fromString (tank.uuid)
//                    , BluetoothGattCharacteristic.PROPERTY_NOTIFY or BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE
//                    , BluetoothGattDescriptor.PERMISSION_WRITE or BluetoothGattCharacteristic.PERMISSION_READ)
//            ch.value = byteArrayOf(0x03.toByte())
//            bluetoothGatt.writeCharacteristic(ch)
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }
    }

    private val listener = object : RecyclerViewClickListener {
        override fun onClick(view: View?, position: Int) {
            tank = tanks[position]
            toast("You select tank $tank")
            tank.bluetoothDevice!!.connectGatt(view!!.context, false, leConnectCallback)
            bleScanner.stopScan(leScanCallback)
        }
    }

}