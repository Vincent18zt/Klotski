package com.example.zhongteng.Klotski;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mrv.huarongdao.R;


public class begin extends AppCompatActivity {
    //  添加游戏音效
    private SoundPool soundPool;
    private int music[];

    public static final Integer TEXT_REQUEST = 1;
    public static final String EXTRA_MESSAGE =
            "com.example.mrv.huarongdao.extra.MESSAGE";

    private final int maxLevel=10;

    int level= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);

        initVoice();
    }

    public void initVoice(){
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        music = new int[3];
        music[0]=soundPool.load(this,R.raw.chess,1);
        music[1]=soundPool.load(this,R.raw.click,1);
        music[2]=soundPool.load(this,R.raw.error,1);
    }
    public void beginGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, level);
        startActivityForResult(intent, TEXT_REQUEST);

        soundPool.play(music[1], 1, 1, 0, 0, 1);
    }

    public void exit(View view) {
        AlertDialog.Builder alertdialogbuilder=new AlertDialog.Builder(this);
        alertdialogbuilder.setMessage("确定要退出程序吗？");
        //定义对话框2个按钮标题及接受事件的函数
        alertdialogbuilder.setPositiveButton("确定",click1);
        alertdialogbuilder.setNegativeButton("取消",click2);
        //创建并显示对话框
        AlertDialog alertdialog1=alertdialogbuilder.create();
        alertdialog1.show();

        soundPool.play(music[1], 1, 1, 0, 0, 1);
    }
    private DialogInterface.OnClickListener click1=new DialogInterface.OnClickListener()
    {
        //使用该标记是为了增强程序在编译时候的检查，如果该方法并不是一个覆盖父类的方法，在编译时编译器就会报告错误。
        @Override
        public void onClick(DialogInterface arg0,int arg1)
        {
            //当按钮click1被按下时执行结束进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };
    private DialogInterface.OnClickListener click2=new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface arg0,int arg1)
        {
            //当按钮click2被按下时则取消操作
            arg0.cancel();
        }
    };

    public void helper(View view) {
        AlertDialog.Builder alertdialogbuilder=new AlertDialog.Builder(this);
        alertdialogbuilder.setTitle("帮助");
        alertdialogbuilder.setMessage("通过移动各个棋子，帮助曹操从初始位置移到棋盘最下方中部，从出口逃走。不允许跨越棋子，还要设法用最少的步数把曹操移到出口。");
        //定义对话框2个按钮标题及接受事件的函数
        alertdialogbuilder.setPositiveButton("确定",click3);
        //创建并显示对话框
        AlertDialog alertdialog1=alertdialogbuilder.create();
        alertdialog1.show();

        soundPool.play(music[1], 1, 1, 0, 0, 1);
    }
    private DialogInterface.OnClickListener click3=new DialogInterface.OnClickListener()
    {
        //使用该标记是为了增强程序在编译时候的检查，如果该方法并不是一个覆盖父类的方法，在编译时编译器就会报告错误。
        @Override
        public void onClick(DialogInterface arg0,int arg1)
        {
            //当按钮click1被按下时执行结束进程
            arg0.cancel();
        }
    };

    public void choose(View view) {
        level = level % maxLevel;
        level += 1;
        Button b = (Button)findViewById(R.id.choose);
        String s[] = {"横刀立马", "兵随将转", "别无选择", "不惑之后", "不惑之前", "兵分三路", "侧面白虎", "倒影同步", "谦之有成", "山穷水尽"};
        b.setText(s[level-1]);

        soundPool.play(music[1], 1, 1, 0, 0, 1);
    }

    public void showHistory(View view) {
        Intent intent = new Intent(this, historyActivity.class);
        startActivityForResult(intent, TEXT_REQUEST);

        soundPool.play(music[1], 1, 1, 0, 0, 1);
    }
}
