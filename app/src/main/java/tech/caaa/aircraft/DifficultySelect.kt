package tech.caaa.aircraft

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tech.caaa.aircraft.game.Difficulty

class DifficultySelect : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_difficulty_select)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val easyMode: Button = findViewById(R.id.easyBtn)
        easyMode.setOnClickListener {
            GlobalCtx.difficulty = Difficulty.EASY
//            val intent = Intent(this, GameActivity::class.java)
//            startActivity(intent)
            this.finish()
        }
        val mediumMode: Button = findViewById(R.id.midBtn)
        mediumMode.setOnClickListener {
            GlobalCtx.difficulty = Difficulty.MEDIUM
//            val intent = Intent(this, GameActivity::class.java)
//            startActivity(intent)
            this.finish()
        }
        val hardMode: Button = findViewById(R.id.hardBtn)
        hardMode.setOnClickListener {
            GlobalCtx.difficulty = Difficulty.HARD
//            val intent = Intent(this, GameActivity::class.java)
//            startActivity(intent)
            this.finish()
        }
    }
}