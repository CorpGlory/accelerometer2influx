package corpglory.android.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import org.jetbrains.anko.button
import org.jetbrains.anko.editText
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout


class MainActivity : AppCompatActivity(), AccelerometerEventListener {
    lateinit var sensorManager: SensorManager
    var accelerometerEventListener: AccelerometerHandler? = null
    var database: DatabaseConnection? = null

    lateinit var sensor: Sensor

    lateinit var outView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            val addrView = editText("209.205.120.226:8086") {
                hint = "Name"
                textSize = 24f
            }

            button("Start / refresh") {
                textSize = 26f
                onClick {
                    startListen(addrView.text.toString())
                }
            }

            outView = textView("") {
                id = View.generateViewId()
            }
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
    }

    private fun startListen(addr: String) {
        val host = addr.substring(0, addr.indexOf(':'))
        val port = Integer.parseInt(addr.substring(addr.indexOf(':') + 1))

        accelerometerEventListener = AccelerometerHandler(this)


        database = DatabaseConnection(host, port)
        database?.start()

        if (accelerometerEventListener != null) {
            sensorManager.unregisterListener(accelerometerEventListener)
        }
        sensorManager.registerListener(accelerometerEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(accelerometerEventListener)
    }

    override fun onStateReceived(state: AccelerometerState) {
        val result = "vals " +
                "k0=${state.x.format(2).replaceFirst(",", ".")}" +
                ",k1=${state.y.format(2).replaceFirst(",", ".")}" +
                ",k2=${state.z.format(2).replaceFirst(",", ".")}" +
                " ${state.time}\n"

        outView.text = result
        database?.sendMsg(result)
    }

    fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)
}
