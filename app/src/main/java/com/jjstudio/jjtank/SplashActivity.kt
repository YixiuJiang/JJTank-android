package com.jjstudio.jjtank

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
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
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class SplashActivity : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds
    lateinit var bleManager: BluetoothManager
    lateinit var bleAdapter: BluetoothAdapter
    lateinit var bleScanner: BluetoothLeScanner
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
//        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
        val rv: RecyclerView = this.tankListView
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        var tankAdapter = TankAdapter(tanks)
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

    fun displayFoundTank(tankName: String) {
        toast("found tanks..." + tankName)

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
            if (tanks.filter { tank -> tank.uuid.equals(deviceAddress) }.size == 0) {
                tanks.add(Tank(deviceName + " - " + deviceAddress, deviceAddress, StatusEnum.Disconnected, bleDevice))
                info("Found LE device: $deviceAddress")
                tankInfoTextView.append("\nfound device..." + bleDevice?.name + " - " + deviceAddress)
            }
        }
    }

    companion object {
        private val REQUEST_ENABLE_BT = 1
        private val PERMISSION_REQUEST_COARSE_LOCATION = 1
    }
}