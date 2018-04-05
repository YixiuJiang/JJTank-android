package com.jjstudio.jjtank

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class SplashActivity : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds
    lateinit var bleManager: BluetoothManager
    lateinit var bleAdapter: BluetoothAdapter
    lateinit var bleScanner: BluetoothLeScanner
    val users = ArrayList<Tank>()
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
        bleManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bleAdapter = bleManager.adapter

        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
//        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)


        startScan()

    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }
    fun startScan() {
        val rv:RecyclerView = findViewById<RecyclerView>(R.id.tankListView)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val tanks = ArrayList<Tank>()
        rv.adapter = TankAdapter(tanks)
        bleScanner = bleAdapter.bluetoothLeScanner
        var bleScanCallback = BleScanCallback(tanks)
        bleScanner.startScan(bleScanCallback)
    }


    class BleScanCallback(tanks: ArrayList<Tank>) : ScanCallback(), AnkoLogger {

        var resultOfScan = tanks

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
            resultOfScan.add(Tank(1, "Merkava 4",deviceAddress,StatusEnum.Connected,bleDevice))
        }
    }
}