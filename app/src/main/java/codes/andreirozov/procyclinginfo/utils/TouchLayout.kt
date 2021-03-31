package codes.andreirozov.procyclinginfo.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class TouchLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    /* Catch all touch event, when filter's card on screen
     (Must have, because without this function all touch events going to recyclerView and viewPager)
     */
    /*override fun onTouchEvent(event: MotionEvent): Boolean {

        return true
    }*/
}
