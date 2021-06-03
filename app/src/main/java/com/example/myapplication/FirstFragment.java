package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.example.myapplication.databinding.FragmentFirstBinding;
import com.example.myapplication.xstore.sync.Credentials;
import com.example.myapplication.xstore.sync.Example;
import com.example.myapplication.xstore.sync.ServerData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding = null;
    private final ArrayList<String> listItems = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        Set<String> list = preferences.getStringSet("listItems", new HashSet<>(listItems));
        list.forEach(it -> {
            if (!listItems.contains(it)) {
                listItems.add(it);
            }
            if (!GlobalData.getInstance().runningInstruments.contains(it)) {
                listItems.add(it);
            }
        });
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        arrayAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.listview_item, listItems);
        binding.listeningList.setAdapter(arrayAdapter);

        binding.addInstrumentButton.setOnClickListener(l -> {
            String instrument = binding.addInstrumentText.getText().toString();
            if (listItems.contains(instrument)) {
                return;
            }
            runInstrumentListening(instrument);
            listItems.add(instrument);
            GlobalData.getInstance().runningInstruments.add(instrument);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            SharedPreferences.Editor editor = preferences.edit();
            HashSet<String> set = new HashSet<>(listItems);
            editor.putStringSet("listItems", set);
            editor.apply();
            arrayAdapter.notifyDataSetChanged();
        });
        binding.listeningList.setOnItemClickListener((parent, view, position, id) -> {
            String clickedItem = listItems.get(position);
            Bundle b = new Bundle();
            b.putString("Instrument", clickedItem);
            Navigation.findNavController(parent).navigate(R.id.action_FirstFragment_to_SecondFragment, b);
        });

        return binding.getRoot();
    }

    private void runInstrumentListening(String instrument) {
        Context c = this.getContext();
        Runnable runnable = () -> {
            Credentials credentials = new Credentials("12263751", "xoh26561");
            Example ex;
            ex = new Example(c);
            ex.runExample(ServerData.ServerEnum.DEMO, credentials, instrument);
        };
        new Thread(runnable).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


