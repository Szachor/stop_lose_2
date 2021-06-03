package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.myapplication.databinding.FragmentFirstBinding
import com.example.myapplication.xstore.sync.Credentials
import com.example.myapplication.xstore.sync.Example
import com.example.myapplication.xstore.sync.ServerData


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment2 : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var listItems: ArrayList<String> = ArrayList()
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onResume() {
        super.onResume()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        val list = preferences.getStringSet("listItems", listItems.toSet())
        list?.forEach {
            if (!listItems.contains(it)) {
                listItems.add(it)
            }

            if (!GlobalData.getInstance().runningInstruments.contains(it)) {
                runInstrumentListening(it)
                GlobalData.getInstance().runningInstruments.add(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        arrayAdapter = ArrayAdapter(
            this.requireContext(),
            R.layout.listview_item,
            listItems
        )

        binding.listeningList.adapter = arrayAdapter
        binding.addInstrumentButton.setOnClickListener {
            val instrument = binding.addInstrumentText.text.toString()

            if (listItems.contains(instrument)) {
                return@setOnClickListener
            }

            runInstrumentListening(instrument)

            listItems.add(instrument)
            GlobalData.getInstance().runningInstruments.add(instrument)
            val preferences = PreferenceManager.getDefaultSharedPreferences(this.context)
            val editor = preferences.edit()
            val set = listItems.toSet()
            editor.putStringSet("listItems", set)
            editor.apply()
            arrayAdapter.notifyDataSetChanged()
        }

        binding.listeningList.setOnItemClickListener { parent, _, position, _ ->
            print(parent.toString())
            val clickedItem = listItems[position]
            val b = Bundle()
            b.putString("Instrument", clickedItem)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, b)
        }

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        return binding.root
    }


    private fun runInstrumentListening(instrument: String) {
        val runnable = Runnable {
            val context = this.context
            val credentials = Credentials(
                "12263751", "xoh26561"
            )
            val ex = Example(context)
            ex.runExample(ServerData.ServerEnum.DEMO, credentials, instrument)
        }
        Thread(runnable).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

