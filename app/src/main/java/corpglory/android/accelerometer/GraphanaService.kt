package corpglory.android.accelerometer

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by soultoxik on 11.03.2018.
 */


interface GraphanaService {
    @POST("/write?db=accelerometer")
    fun push(@Body values: String) : Call<ResponseBody>
}