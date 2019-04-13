package com.example.readwritefile;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    EditText et_text;

    Button b_write, b_read;

    TextView tv_text;

    String fileName = "file.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_text = (EditText) findViewById(R.id.et_text);
        b_write = (Button) findViewById(R.id.b_write);
        b_read = (Button) findViewById(R.id.b_read);
        tv_text = (TextView) findViewById(R.id.tv_text);

        b_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_text.setText(readFile(fileName));
            }
        });

        b_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFile(fileName, et_text.getText().toString());
            }
        });
    }

    public void saveFile(String file, String text){
        try{
            FileOutputStream fos = openFileOutput(file, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
            Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error saving file", Toast.LENGTH_SHORT).show();
        }
    }

    public String readFile(String file){
        String text = "";
        try{
            FileInputStream fis = openFileInput(file);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            text = new String(buffer);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error reading file", Toast.LENGTH_SHORT).show();
        }

        return text;
    }

}
