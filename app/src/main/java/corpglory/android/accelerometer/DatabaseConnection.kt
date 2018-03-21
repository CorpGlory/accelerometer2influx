package corpglory.android.accelerometer

import android.util.Log
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.Point
import java.util.*
import org.influxdb.dto.Point.measurement
import org.influxdb.InfluxDB.ConsistencyLevel
import org.influxdb.dto.BatchPoints
import java.util.concurrent.TimeUnit



/**
 * Created by evsluzh on 17.03.18.
 */

class DatabaseConnection : Thread {
    //val socket: DatagramSocket = DatagramSocket()
    // val socketAddress: SocketAddress
    val buffer: Queue<AccelerometerState> = LinkedList<AccelerometerState>()
    val sendBufferSize = 10
    val maxPossibleBufferSize = 1024
    private var influxDB: InfluxDB? = null
    private val url: String
    private val login: String
    private val password: String
    private val databaseName: String

    constructor(url: String, login: String, password: String, databaseName: String) : super() {
        this.url = url
        this.login = login
        this.password = password
        this.databaseName = databaseName
    }

    fun sendMsg(state: AccelerometerState) {
        if (buffer.size == maxPossibleBufferSize) {
            buffer.remove()
        }
        buffer.add(state)
    }

    override fun run() {
        try {
            if (login.isNotEmpty()) {
                influxDB = InfluxDBFactory.connect(url, login, password)
            } else {
                influxDB = InfluxDBFactory.connect(url)
            }
        } catch (e:Exception ) {
            Log.i("db", e.toString())
        }
        // influxDB?.setDatabase(databaseName)

        while (!isInterrupted) {
            send()
        }
    }

    private fun send() {
        if (buffer.size >= sendBufferSize) {
            var sendBuffer = ArrayList<String>(sendBufferSize)

            val batchPoints = BatchPoints
                    .database(databaseName)
                    .tag("async", "true")
                    .retentionPolicy("aRetentionPolicy")
                    .consistency(ConsistencyLevel.ALL)
                    .build()

            for (i in 0..sendBufferSize-1) {
                val state = buffer.poll()
                val point = Point.measurement("accelerometer")
                        .time(state.time, TimeUnit.NANOSECONDS)
                        .addField("x", state.x)
                        .addField("y", state.y)
                        .addField("z", state.z)
                        .build()
                batchPoints.point(point)
            }
            influxDB?.write(batchPoints)
            // Log.i("send", sendBuffer.joinToString(separator = ""))
            // sendUDP(sendBuffer.joinToString(separator = ""))
            // influxDB?.write(sendBuffer)
        }
    }

}