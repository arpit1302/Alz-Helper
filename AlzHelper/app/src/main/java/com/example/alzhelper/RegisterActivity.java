package com.example.alzhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    EditText pno, fno, dno;
    Button reg;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        changeStatusBarColor();
        Intent intent = getIntent();
        String uname = intent.getStringExtra("username");

        pno = (EditText)findViewById(R.id.editTextpno);
        fno = (EditText)findViewById(R.id.editTextfno);
        dno = (EditText)findViewById(R.id.editTextdno);
        DB = new DBHelper(this);
        reg = (Button)findViewById(R.id.RegisterButton);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pnum = pno.getText().toString();
                String fnum = fno.getText().toString();
                String dnum = dno.getText().toString();
                if(pnum.equals("")||fnum.equals("")||dnum.equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                }
                else{
                    Boolean b = DB.insertData2(uname, pnum, fnum, dnum);
                    if(b)
                    {
                        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                        //intent1.putExtra("username", uname);
                        startActivity(intent1);
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
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