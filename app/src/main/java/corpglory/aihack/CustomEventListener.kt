package corpglory.aihack

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.widget.TextView
import io.reactivex.subjects.PublishSubject

/**
 * Created by soultoxik on 11.03.2018.
 */


class CustomEventListener(
        val context: Context,
        val textView: TextView,
        val publishSubject: PublishSubject<String>) : SensorEventListener {
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    private val gravity = DoubleArray(3)
    private val linear_acceleration = DoubleArray(3)


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
                " ${System.currentTimeMillis() * 1000 * 1000}"

        textView.text = result

        publishSubject.onNext(result)
    }

    fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)
}