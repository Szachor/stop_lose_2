package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListAdapter
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentFirstBinding
import com.example.myapplication.xstore.sync.Credentials
import com.example.myapplication.xstore.sync.Example
import com.example.myapplication.xstore.sync.ServerData
import kotlinx.parcelize.Parceler
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.addInstrumentButton
        //var instrumentText = findViewById<EditText>(R.id.add_instrument_text)
        //var instrumentText = findViewById(R.id.add_instrument_text) as EditText
        //add_instrument_text.
        //binding.InstrumentName.value

        var listItems = ArrayList<String>()
        listItems.add("Dupa")

        var arrayAdapter = ArrayAdapter(
            this.requireContext(),
            R.layout.listview_item,
            listItems
        )
        binding.listeningList.adapter = arrayAdapter

        binding.addInstrumentButton.setOnClickListener {
            val instrument = binding.addInstrumentText.text.toString()
            val runnable = Runnable {
                val context = this.context
                val credentials = Credentials(
                    "12263751", "xoh26561"
                )
                val ex = Example(context)
                ex.runExample(ServerData.ServerEnum.DEMO, credentials, instrument)
            }
            Thread(runnable).start()
            listItems.add(instrument)
            arrayAdapter.notifyDataSetChanged()
        }
        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

