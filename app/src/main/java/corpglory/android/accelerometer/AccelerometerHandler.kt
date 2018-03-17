package corpglory.android.accelerometer

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener


/**
 * Created by soultoxik on 11.03.2018.
 */


class AccelerometerHandler : SensorEventListener {
    val listener: AccelerometerEventListener


    constructor(listener: AccelerometerEventListener) {
        this.listener = listener
        this.gravity = DoubleArray(3)
        this.linear_acceleration = DoubleArray(3)
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

        val state = AccelerometerState(
                linear_acceleration[0],
                linear_acceleration[1],
                linear_acceleration[2],
                System.currentTimeMillis() * 1000 * 1000
                )

        listener.onStateReceived(state)
    }
}