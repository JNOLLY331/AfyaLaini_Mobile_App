package com.jnolly.AfyaLaini.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.jnolly.AfyaLaini.R

object ToastUtils {
    
    private var lastToast: Toast? = null
    
    fun showSuccess(context: Context, message: String) {
        showCustomToast(context, message, R.drawable.bg_toast_success)
    }
    
    fun showError(context: Context, message: String) {
        showCustomToast(context, message, R.drawable.bg_toast_error)
    }
    
    fun showWarning(context: Context, message: String) {
        showCustomToast(context, message, R.drawable.bg_toast_warning)
    }
    
    fun showInfo(context: Context, message: String) {
        showCustomToast(context, message, R.drawable.bg_toast_info)
    }
    
    private fun showCustomToast(context: Context, message: String, backgroundRes: Int) {
        lastToast?.cancel()
        
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)
        val text = layout.findViewById<TextView>(R.id.tvToastMessage)
        text.text = message
        layout.setBackgroundResource(backgroundRes)
        
        lastToast = Toast(context).apply {
            duration = Toast.LENGTH_LONG
            view = layout
            setGravity(Gravity.BOTTOM, 0, 120)
        }
        lastToast?.show()
    }
}