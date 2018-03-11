package corpglory.aihack

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.widget.TextView
import io.reactivex.subjects.PublishSubject
import android.R.attr.port
import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


/**
 * Created by soultoxik on 11.03.2018.
 */


class CustomEventListener : SensorEventListener {

    val context: Context
    val textView: TextView
    val publishSubject: PublishSubject<String>
    val thread: ThreadUDP = ThreadUDP()

    class ThreadUDP : Thread() {


        val s: DatagramSocket = DatagramSocket()
        val local: InetAddress = InetAddress.getByName("209.205.120.226")
        val buffer: Array<String> = Array(5, {""})
//        val buffer: ByteArray = ByteArray(53 * 6)
//        var buffSize = 0

        var i = 0

        var msg: String = ""

//        fun appendToBuffer(msg: String) {
//            val bytes = msg.toByteArray()
//            for(j in bytes.indices) {
//                buffer[53 * i  + j] = bytes[j]
//            }
//            buffSize += bytes.size
//            i += 1
//
//        }



        fun appendToBuffer(msg: String) {
            buffer[i] = msg
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
            val p = DatagramPacket(message, msg_lenght, local, 8089)
            s.send(p)
            Log.i("udp sent", msg)
        }

//        private fun sendUDP(buffer: ByteArray) {
//
//            val p = DatagramPacket(buffer, buffer.size, local, 8089)
//            s.send(p)
////            Log.i("udp sent", ms)
//        }
    }


    constructor(context: Context, textView: TextView, publishSubject: PublishSubject<String>) {
        this.context = context
        this.textView = textView
        this.publishSubject = publishSubject
        this.gravity = DoubleArray(3)
        this.linear_acceleration = DoubleArray(3)

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