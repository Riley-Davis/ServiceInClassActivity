package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import java.util.Timer
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    lateinit var timerBinder: TimerService.TimerBinder
    var isConnected = false

    lateinit var timerTextView: TextView
    val timerHandler = Handler(Looper.getMainLooper()){
        timerTextView.text = it.what.toString()
        true
    }
    val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            timerBinder = p1 as TimerService.TimerBinder
            timerBinder.setHandler(timerHandler)
            isConnected = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        //TimerService does a countdown
        //1. Want to interface with time. Start and stop timer.
        // Timer has three states. Can be started, stopped and paused.
        //Don't change service class.
        //2. Commit
        //3. Display current countdown value in text display.
        // Give service handler to receive data from timer.
        //4. Text on start button should switch from start to paused when clicked.
        //Should switch from paused to unpaused when clicked again.
        //Get status values from service.


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )

        timerTextView = findViewById<TextView>(R.id.textView)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            if (isConnected){
                if(timerBinder.paused || !timerBinder.isRunning){
                    timerBinder.start(10)
                }
                else{
                    timerBinder.pause()
                }

            }

        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if (isConnected) timerBinder.stop()

        }
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }
}