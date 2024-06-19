package tech.caaa.aircraft

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MultiplayerEndActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_multiplayer_end)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val youWin = intent.getBooleanExtra("win", false)
        val yourScore = intent.getIntExtra("your_score", 0)
        val otherScore = intent.getIntExtra("other_score", 0)
        val endText: TextView = findViewById(R.id.endText)
        endText.text = if (youWin) { "You win!" } else {"You lose!"}
        endText.append("\nYour score: $yourScore\nOther player's score: $otherScore")
        val returnBtn = findViewById<Button>(R.id.returnBtn)
        returnBtn.setOnClickListener {
            this.finish()
        }
    }
}