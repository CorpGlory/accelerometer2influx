package corpglory.android.accelerometer

import android.util.Log
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.*

/**
 * Created by evsluzh on 17.03.18.
 */

class DatabaseConnection : Thread {
    //val socket: DatagramSocket = DatagramSocket()
    // val socketAddress: SocketAddress
    val buffer: Queue<String> = LinkedList<String>()
    val sendBufferSize = 10
    val maxPossibleBufferSize = 1024
    private val influxDB: InfluxDB
    private val databaseName: String

    constructor(url: String, login: String, password: String, databaseName: String) : super() {
        influxDB = InfluxDBFactory.connect(url, login, password)
        this.databaseName = databaseName
        influxDB.setDatabase(databaseName)
    }

    fun sendMsg(msg: String) {
        if (buffer.size == maxPossibleBufferSize) {
            buffer.remove()
        }
        buffer.add(msg)
    }

    override fun run() {
        while (!isInterrupted) {
            send()
        }
    }

    private fun send() {
        if (buffer.size >= sendBufferSize) {
            var sendBuffer = ArrayList<String>(sendBufferSize)
            for (i in 0..sendBufferSize-1) {
                sendBuffer[i] = buffer.poll()
            }
            // Log.i("send", sendBuffer.joinToString(separator = ""))
            // sendUDP(sendBuffer.joinToString(separator = ""))
            influxDB.write(sendBuffer)
        }
    }

}