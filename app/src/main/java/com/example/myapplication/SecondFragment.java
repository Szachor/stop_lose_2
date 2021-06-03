package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.databinding.FragmentSecondBinding;

import org.jetbrains.annotations.NotNull;

public class SecondFragment extends Fragment {
    private FragmentSecondBinding binding = null;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String clickedItem = "";
        if (savedInstanceState != null) {
            clickedItem = savedInstanceState.getString("Instrument");
        }


        GlobalData globalDataInstance = GlobalData.getInstance();
        String newText = "";

        if (globalDataInstance.logInstrumentCode != null) {
            newText += "Old instrument was: " + globalDataInstance.logInstrumentCode + "\n";
        }

        Bundle b = this.getArguments();

        assert b != null;
        clickedItem = b.getString("Instrument");
        if (clickedItem != null) {
            newText += "New instrument is: " + clickedItem;
        }
        binding.logsContent.setText(newText);

        globalDataInstance.logInstrumentCode = clickedItem;

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
};