package com.jnolly.AfyaLaini

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SplashActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        startSplashAnimations()

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFinishing || isDestroyed) return@postDelayed

            try {
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    navigateToLogin()
                    return@postDelayed
                }

                FirebaseFirestore.getInstance().collection("users").document(user.uid).get()
                    .addOnSuccessListener { doc ->
                        if (isFinishing || isDestroyed) return@addOnSuccessListener
                        val role = doc.getString("role") ?: "patient"
                        val destination = if (role == "staff") StaffHomeActivity::class.java else HomeActivity::class.java
                        startActivity(Intent(this, destination))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error fetching user role", e)
                        navigateToLogin()
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Splash navigation error", e)
                navigateToLogin()
            }
        }, 1500)
    }

    private fun startSplashAnimations() {
        val logoContainer = findViewById<View>(R.id.flLogoContainer)
        val title = findViewById<TextView>(R.id.tvAppName)
        val tagline = findViewById<TextView>(R.id.tvTagline)
        val progress = findViewById<View>(R.id.progressIndicator)

        logoContainer?.alpha = 0f
        logoContainer?.scaleX = 0.75f
        logoContainer?.scaleY = 0.75f

        title?.alpha = 0f
        title?.translationY = 24f

        tagline?.alpha = 0f
        progress?.alpha = 0f

        logoContainer?.animate()
            ?.alpha(1f)
            ?.scaleX(1f)
            ?.scaleY(1f)
            ?.setDuration(600)
            ?.start()

        title?.animate()
            ?.alpha(1f)
            ?.translationY(0f)
            ?.setDuration(600)
            ?.setStartDelay(200)
            ?.start()

        tagline?.animate()
            ?.alpha(1f)
            ?.setDuration(600)
            ?.setStartDelay(400)
            ?.start()

        progress?.animate()
            ?.alpha(1f)
            ?.setDuration(600)
            ?.setStartDelay(500)
            ?.start()
    }

    private fun navigateToLogin() {
        if (!isFinishing && !isDestroyed) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}