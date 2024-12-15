
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.takingtom.R

class RecordingsAdapter(
    private val context: Context,
    private val recordings: List<Recording>
) : RecyclerView.Adapter<RecordingsAdapter.ViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recording, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recording = recordings[position]
        holder.pathTextView.text = recording.filePath
        holder.timestampTextView.text = recording.timestamp

        holder.playButton.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                holder.playButton.text = "Play"
            } else {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(recording.filePath)
                    prepare()
                    start()
                }
                holder.playButton.text = "Stop"
                mediaPlayer?.setOnCompletionListener {
                    holder.playButton.text = "Play"
                }
            }
        }
    }

    override fun getItemCount(): Int = recordings.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pathTextView: TextView = view.findViewById(R.id.pathTextView)
        val timestampTextView: TextView = view.findViewById(R.id.timestampTextView)
        val playButton: Button = view.findViewById(R.id.playButton)
    }
}
data class Recording(
    val id: Int,
    val filePath: String,
    val timestamp: String
)

