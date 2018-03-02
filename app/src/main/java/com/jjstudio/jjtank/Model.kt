package com.jjstudio.jjtank

/**
 * Created by Charlie Jiang on 2/03/2018.
 */
data class Tank(val id: Long, val title: String, val uuid: String, val status: StatusEnum)

enum class StatusEnum(val value : Int)
{
    Connected(1),
    Disconnected(0)
}