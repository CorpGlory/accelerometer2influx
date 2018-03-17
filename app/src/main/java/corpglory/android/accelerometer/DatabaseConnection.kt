package corpglory.android.accelerometer

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.*

/**
 * Created by evgentu on 17.03.18.
 */

class DatabaseConnection : Thread {
    val socket: DatagramSocket = DatagramSocket()
    val socketAddress: SocketAddress
    val buffer: Queue<String> = LinkedList<String>()
    val sendBufferSize = 10
    val maxPossibleBufferSize = 1024

    constructor(host: String, port: Int) : super() {
        //val host = addr.substring(0, addr.indexOf(':'))
        //val port = Integer.parseInt(addr.substring(addr.indexOf(':') + 1))

        socketAddress = InetSocketAddress(host, port)
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
            var sendBuffer = arrayOfNulls<String>(sendBufferSize)
            for (i in 0..sendBufferSize - 1) {
                sendBuffer[i] = buffer.poll()
            }
            // Log.i("send", sendBuffer.joinToString(separator = ""))
            sendUDP(sendBuffer.joinToString(separator = ""))
        }
    }

    private fun sendUDP(msg: String) {
        val msg_length = msg.length
        val message = msg.toByteArray()
        val p = DatagramPacket(message, msg_length, socketAddress)
        socket.send(p)
    }

}