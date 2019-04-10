package com.mirza.texttospeechmsg;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MessageTranslateListener, TextToSpeech.OnInitListener {

    private String TAG = MainActivity.class.getSimpleName();
    private int permsRequestCode = 200;
    private int speechRequestCode = 201;
    private RecyclerView messagesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestForPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (textToSpeech != null && textToSpeech.isSpeaking())
            textToSpeech.stop();
    }

    private void requestForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            gotPermission();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, TextToSpeech.Engine.ACTION_CHECK_TTS_DATA}, permsRequestCode);
        }
    }

    private void checkLanguageResourceFile() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, speechRequestCode);
    }

    private void gotPermission() {
        setMessagesAdapter();
    }

    private void onPermissionDenied() {
        Toast.makeText(this, "Need all permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permsRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gotPermission();
        } else {
            onPermissionDenied();
        }
    }


    private TextToSpeech textToSpeech;

    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "speechRequestCode : " + requestCode);
        if (requestCode == speechRequestCode) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                textToSpeech = new TextToSpeech(MainActivity.this, this);
            } else {
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    private void setMessagesAdapter() {
        messagesRecyclerView = findViewById(R.id.messages_list);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(new MessageAdapter(getAllSms(), this));
    }


    public List<SMSData> getAllSms() {
        List<SMSData> lstSms = new ArrayList();
        SMSData objSms;

        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        if (cursor != null) {
            int totalSMS = cursor.getCount();

            if (cursor.moveToFirst()) {
                for (int i = 0; i < totalSMS; i++) {

                    objSms = new SMSData();
                    objSms.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                    objSms.setAddress(cursor.getString(cursor
                            .getColumnIndexOrThrow("address")));
                    objSms.setMsg(cursor.getString(cursor.getColumnIndexOrThrow("body")));
                    objSms.setReadState(cursor.getString(cursor.getColumnIndex("read")));
                    objSms.setTime(Long.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("date"))));

                    if (objSms.getMsg().contains("credited") || objSms.getMsg().contains("debited") || objSms.getMsg().contains("withdrawn")) {
                        lstSms.add(objSms);
                    }

                    cursor.moveToNext();
                }
            }
            cursor.close();
        }


        return lstSms;
    }

    private String messageBody;

    @Override
    public void onMessageTranslateRequest(int position) {
        messageBody = ((MessageAdapter) messagesRecyclerView.getAdapter()).data.get(position).getMsg();
        Log.d(TAG, "Message Body " + messageBody);
        checkLanguageResourceFile();
    }


    @Override
    public void onInit(int status) {
        Log.d(TAG, "onInit");
        textToSpeech.setLanguage(new Locale("hin","IND"));
        textToSpeech.speak("My name is Mirza and i live in nanded", TextToSpeech.QUEUE_FLUSH, null);
    }

    private void translateToLang(){
        try {
            Translate.setHttpReferrer("http://android-er.blogspot.com/");
            OutputString = Translate.execute(InputString,
                    Language.ENGLISH, Language.FRENCH);
        } catch (Exception ex) {
            ex.printStackTrace();
            OutputString = "Error";
        }
    }
}
