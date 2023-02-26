package weston.luke.messengerappmvvm.util

import android.content.Context
import android.view.View
import android.widget.Toast


fun View.show() {
    View.VISIBLE
}

fun View.hide() {
    View.GONE
}

fun View.invisible() {
    View.INVISIBLE
}


fun Context.toast(message: String){
    Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
}