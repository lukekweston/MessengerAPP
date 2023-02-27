package weston.luke.messengerappmvvm.util

import android.content.Context
import android.view.View
import android.widget.Toast


fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}


fun Context.toast(message: String){
    Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
}