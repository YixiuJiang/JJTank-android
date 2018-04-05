package com.jjstudio.jjtank

import android.bluetooth.BluetoothDevice

/**
 * Created by Charlie Jiang on 2/03/2018.
 */
data class Tank(val id: Long, val title: String, val uuid: String?, val status: StatusEnum, val bluetoothDevice: BluetoothDevice?)

enum class StatusEnum(val value : Int)
{
    Connected(1),
    Disconnected(0)
}