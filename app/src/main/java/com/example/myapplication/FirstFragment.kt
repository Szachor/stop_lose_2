package com.example.myapplication

import android.content.Intent
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
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var listItems: ArrayList<String> = ArrayList<String>()
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onResume() {
        super.onResume()
        var preferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        var list = preferences.getStringSet("listItems", listItems.toSet())
        list?.forEach {
            listItems.add(it)
            runInstrumentListening(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            var preferences = PreferenceManager.getDefaultSharedPreferences(this.context)
            var editor = preferences.edit()
            var set = listItems.toSet<String>()
            editor.putStringSet("listItems", set)
            editor.apply()
            arrayAdapter.notifyDataSetChanged()
        }

        binding.listeningList.setOnItemClickListener { parent, view, position, id ->
            print(parent.toString())
            var clickedItem = listItems[position]
            var b = Bundle()
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

