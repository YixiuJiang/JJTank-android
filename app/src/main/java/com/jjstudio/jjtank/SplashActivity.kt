package com.jjstudio.jjtank

import android.app.AlertDialog
import android.app.ProgressDialog
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
import org.jetbrains.anko.*
import java.util.*


class SplashActivity : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds
    lateinit var bleManager: BluetoothManager
    lateinit var sendValue: ByteArray
    lateinit var bleAdapter: BluetoothAdapter
    lateinit var bleScanner: BluetoothLeScanner
    lateinit var bluetoothGatt: BluetoothGatt
    lateinit var tank: Tank
    lateinit var progressDialog: ProgressDialog
    internal val tanks = ArrayList<Tank>()
    lateinit var tankInfoTextView: TextView
    lateinit var bleAlert: AlertBuilder<AlertDialog>
    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            toast("Scanning tanks...")
            finish()
        }
    }

    internal val settingRunnable: Runnable = Runnable {
        val intentOpenBluetoothSettings = Intent()
        intentOpenBluetoothSettings.action = android.provider.Settings.ACTION_BLUETOOTH_SETTINGS
        startActivity(intentOpenBluetoothSettings)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bleAlert = alert("Please turn on your bluetooth setting")

        initActivity()

    }

    fun checkBluetooth(): Boolean {
        return bleAdapter != null && bleAdapter.isEnabled
    }

    public override fun onDestroy() {
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }

    fun startScanning() {
        toast("Scanning tanks...")
        progressDialog = indeterminateProgressDialog("Scanning tanks...")
        progressDialog.show()
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

    fun initActivity() {
        setContentView(R.layout.activity_splash)
        tankInfoTextView = this.tankInfos
        bleManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bleAdapter = bleManager.adapter
        mDelayHandler = Handler()

        if (!checkBluetooth()) {
//            toast("Bluetooth is not enabled, will go to bluetooth setting in 3s")
//            mDelayHandler!!.postDelayed(settingRunnable, SPLASH_DELAY)
            bleAlert.title = "Alert"
            bleAlert.yesButton {
                // dialog is now smart-casted to AlertDialog
                bleAlert.build().dismiss()
                val intentOpenBluetoothSettings = Intent()
                intentOpenBluetoothSettings.action = android.provider.Settings.ACTION_BLUETOOTH_SETTINGS
                startActivity(intentOpenBluetoothSettings)
            }
            bleAlert.show()

        } else {
            //Initialize the Handler
            //Navigate with delay
            val rv: RecyclerView = this.tankListView
            rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
            var tankAdapter = TankAdapter(tanks, listener)
            rv.adapter = tankAdapter
            startScanning()
        }
    }

    override fun onResume() {
        super.onResume()
        initActivity()
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
    private val leConnectCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_CONNECTED) {
                //Discover services
                progressDialog.hide()
                gatt?.discoverServices()
            } else if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_DISCONNECTED) {
                toast("Tank ${tank.title} disconnected")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            bluetoothGatt = gatt!!
//            mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
            var services = gatt!!.services
            var characteristics = services[0].characteristics
            var characteristic = characteristics[0]
            characteristic.setValue(hexStringToByteArray("0xC5"))
            if(gatt.writeCharacteristic(characteristic)){
                characteristic.setValue(hexStringToByteArray("80"))
                gatt.writeCharacteristic(characteristic)
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            //rewrite data
            if (!characteristic?.value!!.equals(sendValue)) {
                gatt?.writeCharacteristic(characteristic);
            }
        }
    }

    private val listener = object : RecyclerViewClickListener {
        override fun onClick(view: View?, position: Int) {
            tank = tanks[position]
            toast("You select tank $tank")
            tank.bluetoothDevice?.connectGatt(view?.context, false, leConnectCallback)
            bleScanner.stopScan(leScanCallback)
        }
    }

    fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)

        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }

        return data
    }
}