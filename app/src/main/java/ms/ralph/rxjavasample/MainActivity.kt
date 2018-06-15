package ms.ralph.rxjavasample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.btn_async
import kotlinx.android.synthetic.main.activity_main.btn_rx
import kotlinx.android.synthetic.main.activity_main.edit_time

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_async.setOnClickListener {
            startActivity(Intent(this, OldTaskActivity::class.java).apply {
                putExtra("TIME", edit_time.text.toString().toInt())
            })
        }
        btn_rx.setOnClickListener {
            startActivity(Intent(this, NewTaskActivity::class.java).apply {
                putExtra("TIME", edit_time.text.toString().toInt())
            })
        }
    }
}
