package ms.ralph.rxjavasample

import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.WorkerThread
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main2.btn_send

class OldTaskActivity : AppCompatActivity() {

    private var time: Int = 0

    private var dialog: AlertDialog? = null

    private val LOCK = Any()

    private var imageId: Long? = null

    private var finishListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        time = intent.getIntExtra("TIME", 0)

        btn_send.setOnClickListener { onSendButtonClicked() }

        UploadImageTask().execute("")
    }

    fun onSendButtonClicked() {
        showDialog()
        val listener = {
            imageId?.let {
                SendCommentTask().execute("message" to it)
            }
            Unit
        }
        synchronized(LOCK) {
            if (imageId != null) {
                listener.invoke()
            } else {
                finishListener = listener
            }
        }
    }

    private fun showDialog() {
        dialog = AlertDialog.Builder(this)
            .setMessage("Loading")
            .setCancelable(false)
            .show()
    }

    private fun hideDialog() {
        dialog?.dismiss()
    }

    @WorkerThread
    fun uploadImage(url: String): Long {
        runOnUiThread {
            Toast.makeText(this, "Image upload start", Toast.LENGTH_LONG).show()
        }

        Thread.sleep(time * 1000L)

        runOnUiThread {
            Toast.makeText(this, "Image upload finish", Toast.LENGTH_LONG).show()
        }
        return 1 // image id
    }

    @WorkerThread
    fun sendComment(text: String, imageId: Long) {
        runOnUiThread {
            Toast.makeText(this, "Send start", Toast.LENGTH_LONG).show()
        }
        Thread.sleep(1000L)
        runOnUiThread {
            Toast.makeText(this, "Send finish", Toast.LENGTH_LONG).show()
        }
    }

    inner class UploadImageTask : AsyncTask<String, Unit, Long>() {
        override fun doInBackground(vararg params: String): Long {
            return uploadImage(params[0])
        }

        override fun onPostExecute(result: Long) {
            synchronized(LOCK) {
                imageId = result
                finishListener?.invoke()
            }
        }
    }

    inner class SendCommentTask : AsyncTask<Pair<String, Long>, Unit, Unit>() {
        override fun doInBackground(vararg params: Pair<String, Long>) {
            sendComment(params[0].first, params[0].second)
        }

        override fun onPostExecute(result: Unit) {
            hideDialog()
        }
    }
}
