package com.example.atvspeechtotext;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    String TAG = "STT Fragment";

    private static final int REQUEST_PERMISSION_RECORD_AUDIO = 1;
    private static final int REQ_CODE = 100;

    TextView statusText;
    TextView resultText;
    Button listenButton;
    Intent intent;
    SpeechRecognizer speechRecognizer;

    public MainFragment() {
    }


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }


    @Override
    public void onDestroy() {
        speechRecognizer.destroy();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        statusText = (TextView)view.findViewById(R.id.state_text);
        statusText.setText("!");
        resultText = (TextView)view.findViewById(R.id.result_text);

        if (ContextCompat.checkSelfPermission(this.getContext() , Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_RECORD_AUDIO);
            }
        }

        listenButton = (Button)view.findViewById(R.id.listen_button);
        listenButton.setOnClickListener(v -> {
            statusText.setText(statusText.getText() + "???????????? ?????? ??????");

            intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "???????????????");

            try {
                startActivityForResult(intent, REQ_CODE);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getContext(),
                        "Sorry your device not supported",
                        Toast.LENGTH_SHORT).show();
            }

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.getContext());
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {
                    statusText.setText(statusText.getText() +  "??????????????? ???????????????.");
                }

                @Override
                public void onBeginningOfSpeech() {
                    statusText.setText(statusText.getText() + "onBeginningOfSpeech");
                    resultText.setText("");
                }

                @Override
                public void onRmsChanged(float v) {
                    statusText.setText(statusText.getText() + "onRmsChanged");
                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                    statusText.setText(statusText.getText() + "onBufferReceived");
                }

                @Override
                public void onEndOfSpeech() {
                    statusText.setText(statusText.getText() + "onEndOfSpeech");
                }

                @Override
                public void onError(int error) {
                    String message = "";
                    switch (error) {
                        case SpeechRecognizer.ERROR_AUDIO:
                            message = "????????? ??????";
                            break;
                        case SpeechRecognizer.ERROR_CLIENT:
                            message = "??????????????? ??????";
                            break;
                        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                            message = "????????? ??????";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK:
                            message = "???????????? ??????";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                            message = "????????? ????????????";
                            break;
                        case SpeechRecognizer.ERROR_NO_MATCH:
                            message = "?????? ??? ??????";
                            break;
                        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                            message = "RECOGNIZER??? ??????";
                            break;
                        case SpeechRecognizer.ERROR_SERVER:
                            message = "????????? ?????????";
                            break;
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            message = "????????? ????????????";
                            break;
                        default:
                            message = "??? ??? ?????? ?????????";
                            break;
                    }
                    statusText.setText(statusText.getText() + "????????? ?????????????????????. : " + message);
                }

                @Override
                public void onResults(Bundle bundle) {
                    statusText.setText(statusText.getText() + "onResults");
                    //micButton.setImageResource(R.drawable.ic_mic_black_off);
                    ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    resultText.setText(data.get(0));
                }

                @Override
                public void onPartialResults(Bundle var1) {
                    statusText.setText(statusText.getText() + "onPartialResults");
                }

                @Override
                public void onEvent(int var1, Bundle var2) {
                    statusText.setText(statusText.getText() + "onEvent");
                }

            });

            speechRecognizer.startListening(intent);
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        statusText.setText(statusText.getText() + "onRequestPermissionsResult");
        if (requestCode == REQUEST_PERMISSION_RECORD_AUDIO && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                statusText.setText(statusText.getText() + "Permission Granted");
        }
    }
}
