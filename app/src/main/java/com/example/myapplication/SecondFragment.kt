package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.myapplication.databinding.FragmentSecondBinding
import java.time.LocalDateTime

class SecondFragment : Fragment() {
    private var binding: FragmentSecondBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val clickedItem: String
        val globalDataInstance = GlobalData.getInstance()
        var newText = ""
        if (globalDataInstance.logInstrumentCode != null) {
            newText += """
                Old instrument was: ${globalDataInstance.logInstrumentCode}
                
                """.trimIndent()
        }
        val b = this.requireArguments()
        clickedItem = b.getString("Instrument").toString()
        newText += "New instrument is: $clickedItem"
        binding!!.logsContent.text = newText
        globalDataInstance.logInstrumentCode = clickedItem
        binding!!.buttonSecond.setOnClickListener { v ->
            Navigation.findNavController(
                v
            ).navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        listenLogs()
    }

    private var logs = ""
    private var runnable: Runnable? = null
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun listenLogs() {
        val globalDataInstance = GlobalData.getInstance()
        runnable = Runnable {
            while (binding != null) {
                try {
                    var substringStart = (logs.length - 1000).coerceAtLeast(0)
                    if (substringStart > 0) {
                        substringStart = logs.indexOf("\n", substringStart) + 1
                    }
                    logs = logs.substring(substringStart)
                    logs += globalDataInstance.logs.take().toString()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                var toDisplayMessage = "$logs\n Time:"
                toDisplayMessage += LocalDateTime.now().toString()
                val msg = mHandler.obtainMessage()
                val bundle = Bundle()
                bundle.putString("Logs", toDisplayMessage)
                msg.data = bundle
                mHandler.sendMessage(msg)
            }
        }
        Thread(runnable).start()
    }

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            try {
                binding!!.logsContent.text = msg.data.getString("Logs")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mHandler.removeCallbacks(runnable!!)
        binding = null
    }
}