package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

        listenLogs();
    }

    private String logs = "";
    private Runnable runnable;
    private void listenLogs(){
        GlobalData globalDataInstance = GlobalData.getInstance();
        Runnable runnable = () -> {
            while(binding != null) {
                try {
                    int substring_start = Math.max(logs.length() - 1000, 0);
                    logs = logs.substring(substring_start);
                    logs += globalDataInstance.logs.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("Logs", logs);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        };
        new Thread(runnable).start();
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                binding.logsContent.setText(logs);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacks(runnable);

        binding = null;
    }
};