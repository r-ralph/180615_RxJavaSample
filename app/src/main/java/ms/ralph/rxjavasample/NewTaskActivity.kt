package ms.ralph.rxjavasample

import android.os.Bundle
import android.support.annotation.WorkerThread
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main2.btn_send

class NewTaskActivity : AppCompatActivity() {
    private var dialog: AlertDialog? = null

    private lateinit var imageUploadTask: Single<Long>

    private var time: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        time = intent.getIntExtra("TIME", 0)

        btn_send.setOnClickListener { onSendButtonClicked() }

        val hotImageUploadTask = Single.fromCallable { uploadImage("") } // 画像投稿のSingleを作る
            .subscribeOn(Schedulers.io()) // ↑の uploadImage はI/Oスレッドで実行
            .toObservable() // Observableに変換（下のreplayのため）
            .replay()  // Observableで流れてきた値をキャッシュする
        hotImageUploadTask.connect()// Observable の処理を開始する

        imageUploadTask = hotImageUploadTask.singleOrError() // Singleに変換する
    }

    fun onSendButtonClicked() {
        imageUploadTask
            .observeOn(Schedulers.io()) // これより下の処理を I/O スレッドで行う
            .flatMap { id ->
                Single.fromCallable { sendComment("", id) } // 画像アップロードの結果の imageId を使用してコメント送信
            }
            .observeOn(AndroidSchedulers.mainThread()) // これより下の処理を UIスレッドで行う
            .doOnSubscribe { showDialog() } //  このSingleをsubscribeしたタイミングでダイアログ表示
            .doOnEvent { _, _ -> hideDialog() } // このSingleが完了したときにダイアログを消す
            .subscribe({
                // すべてが終了したときの処理
            }, {
                it.printStackTrace() // エラーハンドリング
            })
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
}
