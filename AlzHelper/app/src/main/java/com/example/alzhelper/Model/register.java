package com.example.alzhelper.Model;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.alzhelper.DBHelper;
import com.example.alzhelper.LoginActivity;
import com.example.alzhelper.R;

public class register extends AppCompatActivity {
    EditText pno, fno, dno;
    Button signUp;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        pno = (EditText)findViewById(R.id.editTextpno);
        fno = (EditText)findViewById(R.id.editTextfno);
        dno = (EditText)findViewById(R.id.editTextdno);
        signUp = (Button)findViewById(R.id.RegisterButton);
        DB = new DBHelper(this);
        Intent intent = getIntent();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = intent.getStringExtra("username");
                String pnum = pno.getText().toString();
                String fnum = fno.getText().toString();
                String dnum = dno.getText().toString();
                boolean i = DB.insertData2(uname, pnum, fnum, dnum);
                if(i)
                {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    public void onsigninClick(View view){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }
}