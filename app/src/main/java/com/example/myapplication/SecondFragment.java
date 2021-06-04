package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.databinding.FragmentSecondBinding;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class SecondFragment extends Fragment {
    private FragmentSecondBinding binding = null;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String clickedItem;

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

        binding.buttonSecond.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_SecondFragment_to_FirstFragment));

        listenLogs();
    }

    private String logs = "";
    private Runnable runnable;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void listenLogs() {
        GlobalData globalDataInstance = GlobalData.getInstance();
        runnable = () -> {
            while (binding != null) {
                String toDisplayMessage;
                try {
                    int substring_start = Math.max(logs.length() - 1000, 0);
                    if (substring_start > 0) {
                        substring_start = logs.indexOf("\n", substring_start) + 1;
                    }
                    logs = logs.substring(substring_start);
                    logs += globalDataInstance.logs.take().toString();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                toDisplayMessage = logs + "\n Time:";
                toDisplayMessage += LocalDateTime.now().toString();

                Message msg = mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("Logs", toDisplayMessage);
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
                binding.logsContent.setText(msg.getData().getString("Logs"));
            } catch (Exception e) {
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
}