package tech.caaa.aircraft

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tech.caaa.aircraft.game.Difficulty
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


fun dif2int(difficulty: Difficulty): Int {
    return when(GlobalCtx.difficulty){
        Difficulty.EASY -> 1
        Difficulty.MEDIUM -> 2
        Difficulty.HARD -> 3
    }
}

class EndActivity : AppCompatActivity() {
    private lateinit var dataList: MutableList<ScoreRecord>
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var rankAdapter: RankAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_end)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val returnBtn = findViewById<Button>(R.id.returnBtn)
        returnBtn.setOnClickListener {
            this.finish()
        }
        val scoreTextView = findViewById<TextView>(R.id.titleTextView)
        scoreTextView.text = when(GlobalCtx.difficulty) {
            Difficulty.EASY -> "EASY"
            Difficulty.MEDIUM -> "MEDIUM"
            Difficulty.HARD -> "HARD"
        }
        val rankListView = findViewById<ListView>(R.id.rankListView)
        databaseHelper = DatabaseHelper(this)
        val current_score = intent.getIntExtra("score", -1)
        if (current_score == -1) throw Exception("Current user score not found!")
        databaseHelper.addRecord(GlobalCtx.username, current_score, System.currentTimeMillis(), dif2int(GlobalCtx.difficulty))
        dataList = databaseHelper.getAllDataModels(dif2int(GlobalCtx.difficulty)).toMutableList()
        rankAdapter = RankAdapter(this, dataList)
        rankListView.adapter = rankAdapter
        rankListView.setOnItemLongClickListener { parent, view, position, id ->
            val record = dataList[position]
            showDeleteConfirmationDialog(record, position)
            true
        }
    }

    // Show a confirmation dialog before deletion
    private fun showDeleteConfirmationDialog(record: ScoreRecord, position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setMessage("Are you sure you want to delete this score record?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            databaseHelper.deleteRecord(record.id)
            dataList.removeAt(position)
            rankAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}

class RankAdapter(private val context: Context, private val dataList: List<ScoreRecord>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView: View
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = inflater.inflate(R.layout.list_item, parent, false)
        } else {
            rowView = convertView
        }

        val nameColumn = rowView.findViewById<TextView>(R.id.nameColumn)
        val scoreColumn = rowView.findViewById<TextView>(R.id.scoreColumn)
        val timeColumn = rowView.findViewById<TextView>(R.id.timeColumn)

        val data = dataList[position]

        nameColumn.text = data.name
        scoreColumn.text = data.score.toString()
        timeColumn.text = convertLongToISOFormat(data.time)

        return rowView
    }
}

fun convertLongToISOFormat(datetime: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(datetime))
}