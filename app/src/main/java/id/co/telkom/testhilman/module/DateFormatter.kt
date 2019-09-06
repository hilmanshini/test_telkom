package id.co.telkom.testhilman.module

import android.util.Log
import hackernews.api.entity.Feed
import java.text.SimpleDateFormat
import java.util.*

class DateFormatter{
    companion object{
        @JvmStatic fun reformatDate(feed:Feed):String{
            var sdf:SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy");
            if(feed.time == null){
                return sdf.format(Date())
            }

            Log.e("XLOG","REFORMAT "+feed.time)
            return sdf.format(Date(feed.time*1000L))
        }
    }
}