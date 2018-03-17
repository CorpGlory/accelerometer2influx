package corpglory.android.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.widget.TextView
import android.util.Log
import java.net.*


/**
 * Created by soultoxik on 11.03.2018.
 */


class CustomEventListener : SensorEventListener {

    val context: Context
    val textView: TextView
    val thread: ThreadUDP

    class ThreadUDP : Thread {

        val s: DatagramSocket = DatagramSocket()
        val socketAddress: SocketAddress
        val buffer: Array<String> = Array(5, {""})
        var i = 0

        var msg: String = ""

        constructor(addr: String) : super() {
            val host = addr.substring(0, addr.indexOf(':'))
            val port = Integer.parseInt(addr.substring(addr.indexOf(':') + 1))

            socketAddress = InetSocketAddress(host, port)
        }


        fun appendToBuffer(msg: String) {
            // buffer[i] = msg
            i += 1
        }


        override fun run() {
            while (!isInterrupted) {
                if(i >= 5) {
                    sendUDP(buffer.joinToString(separator = ""))
//                    sendUDP(buffer)

                    i = 0
//                    buffSize = 0
                }
            }
        }

        private fun sendUDP(msg: String) {
            val msg_lenght = msg.length
            val message = msg.toByteArray()
            val p = DatagramPacket(message, msg_lenght, socketAddress)
            s.send(p)
        }
    }


    constructor(context: Context, addr: String, textView: TextView) {
        Log.i("here1", "here1")
        this.context = context
        this.textView = textView
        // this.publishSubject = publishSubject
        this.gravity = DoubleArray(3)
        this.linear_acceleration = DoubleArray(3)

        thread = ThreadUDP(addr)
        thread.start()
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    private val gravity: DoubleArray
    private val linear_acceleration: DoubleArray


    override fun onSensorChanged(event: SensorEvent) {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        val alpha = 0.8

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0]
        linear_acceleration[1] = event.values[1] - gravity[1]
        linear_acceleration[2] = event.values[2] - gravity[2]


        val result = "vals " +
                "k0=${linear_acceleration[0].format(2).replaceFirst(",", ".")}" +
                ",k1=${linear_acceleration[1].format(2).replaceFirst(",", ".")}" +
                ",k2=${linear_acceleration[2].format(2).replaceFirst(",", ".")}" +
                " ${System.currentTimeMillis() * 1000 * 1000}\n"

        textView.text = result

        thread.appendToBuffer(result)
//        thread.msg = result
//        publishSubject.onNext(result)
    }

    fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)
}