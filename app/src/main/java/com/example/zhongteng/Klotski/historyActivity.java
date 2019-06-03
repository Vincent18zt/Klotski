package com.example.zhongteng.Klotski;

import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrv.huarongdao.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class historyActivity extends AppCompatActivity {

    private SoundPool soundPool;
    private int music[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        read();
        initVoice();
    }

    public void initVoice(){
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        music = new int[1];
        music[0]=soundPool.load(this,R.raw.click,1);
    }

    public String read() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        TextView t = findViewById(R.id.history);
        try {
            //设置将要打开的存储文件名称
            in = openFileInput("winHistory");
            //FileInputStream -> InputStreamReader ->BufferedReader
            reader = new BufferedReader(new InputStreamReader(in));
            String line = new String();
            //读取每一行数据，并追加到StringBuilder对象中，直到结束
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        t.setText(content.toString().replace("$", "\n"));
        return content.toString();
    }

    public void clear(View view) {
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try{
            out=openFileOutput("winHistory",MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write("");

            Toast toast = Toast.makeText(this, "清除历史记录成功！",
                    Toast.LENGTH_SHORT);
            toast.show();

            TextView t = findViewById(R.id.history);
            t.setText("");

            soundPool.play(music[0], 1, 1, 0, 0, 1);
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
