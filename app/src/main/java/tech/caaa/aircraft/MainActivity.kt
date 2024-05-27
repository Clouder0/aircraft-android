package tech.caaa.aircraft

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val musicCheckbox: CheckBox = findViewById(R.id.musicCheckBox)

        val localModeBtn: Button = findViewById(R.id.localModeBtn)
        localModeBtn.setOnClickListener {
            Toast.makeText(this, "button clicked!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DifficultySelect::class.java)
            GlobalCtx.misc_music_enabled = musicCheckbox.isChecked
            if(GlobalCtx.misc_music_enabled)
                Toast.makeText(this, "music enabled!", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }
}