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


class MainActivity : AppCompatActivity() {

    lateinit var sensorManager: SensorManager
    var customEventListener: CustomEventListener? = null

    lateinit var sensor: Sensor
    lateinit var graphanaService: GraphanaService

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

        customEventListener = CustomEventListener(this@MainActivity, addr, outView)
        if (customEventListener != null) {
            sensorManager.unregisterListener(customEventListener)
        }
        sensorManager.registerListener(customEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(customEventListener)
    }
}
