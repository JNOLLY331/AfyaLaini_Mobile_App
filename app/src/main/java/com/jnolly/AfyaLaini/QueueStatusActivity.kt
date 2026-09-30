package com.jnolly.AfyaLaini

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class QueueStatusActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null
    private var myQueueNumber = 0
    private var liveDot: View? = null

    private val liveHandler = Handler(Looper.getMainLooper())
    private val liveRunnable = object : Runnable {
        override fun run() {
            liveDot?.let { dot ->
                dot.alpha = if (dot.alpha == 1f) 0.3f else 1f
            }
            liveHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue_status)

        setupToolbar()
        startLiveIndicator()

        val doctorId = intent.getStringExtra("doctorId") ?: return finish()
        val date = intent.getStringExtra("date") ?: return finish()
        myQueueNumber = intent.getIntExtra("queueNumber", 0)

        findViewById<TextView>(R.id.tvMyNumber).text = myQueueNumber.toString()

        val queueStatusRef = db.collection("queueStatus").document("${doctorId}_$date")
        listenerRegistration = queueStatusRef.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
            val nowServing = snapshot.getLong("nowServing")?.toInt() ?: 0
            updateUi(nowServing)
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun startLiveIndicator() {
        liveDot = findViewById(R.id.vLiveDot)
        liveHandler.postDelayed(liveRunnable, 1000)
    }

    private fun updateUi(nowServing: Int) {
        val ahead = (myQueueNumber - nowServing - 1).coerceAtLeast(0)
        
        findViewById<TextView>(R.id.tvNowServing).text = nowServing.toString()
        findViewById<TextView>(R.id.tvAhead).text = ahead.toString()
        findViewById<TextView>(R.id.tvEta).text = "≈ ${ahead * 5} minutes wait"

        val cardYourTurn = findViewById<CardView>(R.id.cardYourTurn)
        val tvYourTurn = findViewById<TextView>(R.id.tvYourTurn)
        
        if (myQueueNumber <= nowServing) {
            cardYourTurn.visibility = View.VISIBLE
            tvYourTurn.text = "It's your turn! Please proceed to the doctor's office."
        } else {
            cardYourTurn.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        liveHandler.removeCallbacks(liveRunnable)
        listenerRegistration?.remove()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}