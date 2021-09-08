package com.example.alzhelper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.alzhelper.Adapter.ChatMessageAdapter;
import com.example.alzhelper.Model.ChatMessage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton btnSend;
    EditText editTextMsg;
    ImageView imageView;
    DBHelper DB;
    private Bot bot;
    public static Chat chat;
    private ChatMessageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Intent intent = getIntent();
        String usern = intent.getStringExtra("username");


        listView = findViewById(R.id.listView);
        btnSend = findViewById(R.id.btnSend);
        editTextMsg = findViewById(R.id.editTextMsg);
        imageView = findViewById(R.id.imageView);
        DB = new DBHelper(this);

        adapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        listView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMsg.getText().toString();
                if(message.equalsIgnoreCase("where do i live") || message.equalsIgnoreCase("where do i live?") || message.equalsIgnoreCase("where is my house") || message.equalsIgnoreCase("where is my house?") || message.contains("go home") || message.contains("get home")){
                    String addr = DB.getAddress(usern);
                    String response = "You live here: "+ addr + ". I will guide you there now.";
                    sendMessage(message);
                    botsReply(response);
                    editTextMsg.setText("");
                    listView.setSelection(adapter.getCount() - 1);
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+addr.replaceAll("\\s", "+"));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    }, 3000);
                }
                else if(message.equalsIgnoreCase("who am i") || message.equalsIgnoreCase("who am i?") || message.equalsIgnoreCase("what is my name") || message.equalsIgnoreCase("what is my name?") || message.equalsIgnoreCase("i forgot my name") || message.equalsIgnoreCase("i don't remember my name")){
                    String n = DB.getName(usern);
                    String response = "Your name is "+n;
                    sendMessage(message);
                    botsReply(response);
                    editTextMsg.setText("");
                    listView.setSelection(adapter.getCount() - 1);
                }
                else if(message.equalsIgnoreCase("who are you") || message.equalsIgnoreCase("who are you?")){
                    String response = "I am your memory assistant";
                    sendMessage(message);
                    botsReply(response);
                    editTextMsg.setText("");
                    listView.setSelection(adapter.getCount() - 1);
                }
                else if(message.contains("emergency")||(message.contains("family")&&message.contains("call"))||message.equalsIgnoreCase("I need help")){
                    String f = DB.getfno(usern);
                    String response = "Your family member's contact number is "+f+". I am connecting the call now.";
                    sendMessage(message);
                    botsReply(response);
                    editTextMsg.setText("");
                    listView.setSelection(adapter.getCount() - 1);

                    Handler handle = new Handler();
                    handle.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent callfIntent = new Intent(Intent.ACTION_CALL);
                            callfIntent.setData(Uri.parse("tel:"+f));
                            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            startActivity(callfIntent);
                        }
                    }, 3000);
                }
                else if(message.contains("doctor")||message.contains("medical")||message.contains("health")){
                    String d = DB.getdno(usern);
                    String response = "Your doctor's contact number is "+d+". I am connecting the call now.";
                    sendMessage(message);
                    botsReply(response);
                    editTextMsg.setText("");
                    listView.setSelection(adapter.getCount() - 1);

                    Handler hand = new Handler();
                    hand.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent calldIntent = new Intent(Intent.ACTION_CALL);
                            calldIntent.setData(Uri.parse("tel:"+d));
                            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            startActivity(calldIntent);
                        }
                    }, 3000);
                }
                else{
                    String response = chat.multisentenceRespond(message);
                    sendMessage(message);
                    botsReply(response);
                    editTextMsg.setText("");
                    listView.setSelection(adapter.getCount() - 1);
                }

                if(TextUtils.isEmpty(message)){
                    Toast.makeText(HomeActivity.this, "Please enter a query", Toast.LENGTH_SHORT).show();
                }
                

            }
        });

        boolean available = isSDCardAvailable();
        AssetManager assets = getResources().getAssets();
        File filename = new File(Environment.getExternalStorageDirectory().toString() + "/TBC/bots/TBC");

        boolean makeFile = filename.mkdirs();
        if(filename.exists()){

            try{
                for(String dir : assets.list("TBC")){
                    File subDir = new File(filename.getPath() + "/" + dir);
                    boolean subDir_check = subDir.mkdirs();

                    for(String file : assets.list("TBC/"+dir)){
                        File newFile = new File(filename.getPath() + "/" + dir + "/" + file);
                        if(newFile.exists()){
                            continue;
                        }

                        InputStream in;
                        OutputStream out;
                        String str;
                        in = assets.open("TBC/" + dir + "/" + file);
                        out = new FileOutputStream(filename.getPath() + "/" + dir + "/" + file);
                        copyFile(in, out);
                        in.close();
                        out.flush();
                        out.close();

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/TBC";
        AIMLProcessor.extension = new PCAIMLProcessorExtension();
        bot = new Bot("TBC", MagicStrings.root_path, "chat");
        chat = new Chat(bot);


    }

    private void copyFile(InputStream in, OutputStream out) throws IOException{
        byte[] buffer = new byte[1024];
        int read;

        while((read=in.read(buffer))!=-1){
            out.write(buffer, 0, read);
        }


    }

    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private void botsReply(String response) {
        ChatMessage chatMessage = new ChatMessage(false,false, response);
        adapter.add(chatMessage);

    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(false,true, message);
        adapter.add(chatMessage);
    }
}