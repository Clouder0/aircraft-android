package tech.caaa.aircraft

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var difLauncher: ActivityResultLauncher<Intent>
    private lateinit var difLauncherServer: ActivityResultLauncher<Intent>
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

        val nameEdit: EditText = findViewById(R.id.nameEdit)
        difLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
                val intent2 = Intent(this, GameActivity::class.java)
                startActivity(intent2)
        }
        difLauncherServer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            val intent2 = Intent(this, ServerWaitRoom::class.java)
            startActivity(intent2)
        }
        val localModeBtn: Button = findViewById(R.id.localModeBtn)
        localModeBtn.setOnClickListener {
            Toast.makeText(this, "button clicked!", Toast.LENGTH_SHORT).show()
            GlobalCtx.misc_music_enabled = musicCheckbox.isChecked
            GlobalCtx.username = nameEdit.text.toString()
            if (GlobalCtx.misc_music_enabled)
                Toast.makeText(this, "music enabled!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DifficultySelect::class.java)
            difLauncher.launch(intent)
        }
        val remoteServerButton: Button = findViewById(R.id.remoteServerBtn)
        remoteServerButton.setOnClickListener {
            Toast.makeText(this, "button clicked!", Toast.LENGTH_SHORT).show()
            GlobalCtx.misc_music_enabled = musicCheckbox.isChecked
            GlobalCtx.username = nameEdit.text.toString()
            if (GlobalCtx.misc_music_enabled)
                Toast.makeText(this, "music enabled!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DifficultySelect::class.java)
            difLauncherServer.launch(intent)
        }
        val remoteClientButton: Button = findViewById(R.id.remoteClientBtn)
        remoteClientButton.setOnClickListener {
            Toast.makeText(this, "button clicked!", Toast.LENGTH_SHORT).show()
            GlobalCtx.misc_music_enabled = musicCheckbox.isChecked
            GlobalCtx.username = nameEdit.text.toString()
            if (GlobalCtx.misc_music_enabled)
                Toast.makeText(this, "music enabled!", Toast.LENGTH_SHORT).show()
            val intent2 = Intent(this, ClientWaitRoom::class.java)
            startActivity(intent2)
        }
    }

}
