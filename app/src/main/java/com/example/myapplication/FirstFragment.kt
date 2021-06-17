package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.example.myapplication.databinding.FragmentFirstBinding
import com.example.myapplication.xstore.sync.Credentials
import com.example.myapplication.xstore.sync.Example
import com.example.myapplication.xstore.sync.ServerData
import java.util.*
import java.util.function.Consumer

class FirstFragment : Fragment() {
    private var binding: FragmentFirstBinding? = null
    private val listItems = ArrayList<String>()
    private var arrayAdapter: ArrayAdapter<String>? = null
    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        val list = preferences.getStringSet("listItems", HashSet(listItems))
        list!!.forEach(Consumer { it: String ->
            if (!listItems.contains(it)) {
                listItems.add(it)
            }
            if (!GlobalData.getInstance().runningInstruments.contains(it)) {
                listItems.add(it)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.listview_item, listItems)
        binding!!.listeningList.adapter = arrayAdapter
        binding!!.addInstrumentButton.setOnClickListener { l: View? ->
            val instrument = binding!!.addInstrumentText.text.toString()
            if (listItems.contains(instrument)) {
                return@setOnClickListener
            }
            runInstrumentListening(instrument)
            listItems.add(instrument)
            GlobalData.getInstance().runningInstruments.add(instrument)
            val preferences = PreferenceManager.getDefaultSharedPreferences(this.context)
            val editor = preferences.edit()
            val set = HashSet(listItems)
            editor.putStringSet("listItems", set)
            editor.apply()
            arrayAdapter!!.notifyDataSetChanged()
        }
        binding!!.listeningList.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                val clickedItem = listItems[position]
                val b = Bundle()
                b.putString("Instrument", clickedItem)
                Navigation.findNavController(parent!!)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment, b)
            }
        return binding!!.root
    }

    private fun runInstrumentListening(instrument: String) {
        val c = this.context
        val runnable = Runnable {
            val credentials = Credentials("12263751", "xoh26561")
            val ex: Example
            ex = Example(c)
            ex.runExample(ServerData.ServerEnum.DEMO, credentials, instrument)
        }
        Thread(runnable).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}