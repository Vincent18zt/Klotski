package com.example.zhongteng.Klotski;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrv.huarongdao.R;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private String playerName="匿名";

    private SoundPool soundPool;
    private int music[];

    GestureDetector detector;
    final int N = 10;
    final int widthDp = 80;
    final int yDirection = 5;
    final int xDirection = 4;
    final int size[]={1,2, 2,2, 1,2, 1,2, 2,1, 1,2, 1,1, 1,1, 1,1, 1,1};
    //关卡
    final int position[][]={
            {0,0, 1,0, 3,0, 0,2, 1,2, 3,2, 1,3, 2,3, 0,4, 3,4},
            {3,0, 0,0, 2,1, 1,3, 2,3, 0,2, 2,0, 1,2, 3,2, 2,4},
            {2,0, 0,0, 1,2, 0,3, 2,2, 3,3, 0,2, 1,4, 2,3, 2,4},
            {2,0, 0,2, 3,0, 2,2, 0,1, 3,2, 0,4, 1,4, 2,4, 3,4},
            {2,1, 0,1, 3,1, 2,3, 0,3, 3,3, 0,0, 1,0, 2,0, 3,0}, //5
            {0,1, 1,0, 3,1, 0,3, 1,2, 3,3, 0,0, 3,0, 1,3, 2,3},
            {3,0, 0,1, 2,1, 3,2, 0,4, 2,3, 1,0, 2,0, 1,3, 3,4},
            {3,1, 0,0, 2,2, 1,3, 2,0, 3,3, 2,1, 0,2, 0,3, 1,2},
            {0,3, 1,0, 1,3, 2,3, 2,2, 3,3, 0,0, 0,1, 0,2, 1,2},
            {0,0, 1,0, 3,0, 1,3, 0,2, 2,3, 2,2, 3,2, 0,3, 3,3}
    };
    int level = 0;
    final int maxLevel = 10;
    int borderWidthPx;
    int phoneWidth;
    int phoneHeight;
    Stack gameState;

    TextView views[];

    //计时间
    private int usedtime = 0;
    private TextView showTime;
    //计步长
    private int usedstep = 0;
    private TextView showStep;
    Handler handler = new Handler();

    public static final String EXTRA_REPLY =
            "com.example.mrv.lab22.extra.REPLY";
    private Intent replyIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        level = intent.getIntExtra(begin.EXTRA_MESSAGE, -1)-1;
        if(level == -1)level = 0;

        detector = new GestureDetector(this, new MyGestureListener());
        borderWidthPx = DpToPx(this, widthDp);
        views = new TextView[N];
        Resources res=getResources();
        for(int i=0;i<N;i++) {
            views[i] = findViewById(res.getIdentifier("textView" + (i + 1), "id", getPackageName()));
        }

        showTime = (TextView)findViewById(R.id.showTime);
        handler.postDelayed(runnable, 1000);
        showStep = (TextView)findViewById(R.id.showStep);

        gameState=new Stack();

        initLayout();
        initVoice();

    }
    public void initVoice(){
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        music = new int[4];
        music[0]=soundPool.load(this,R.raw.chess,1);
        music[1]=soundPool.load(this,R.raw.click,1);
        music[2]=soundPool.load(this,R.raw.error,1);
        music[3]=soundPool.load(this,R.raw.victory,1);
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            usedtime++;
            showTime.setText(usedtime+"s");
            handler.postDelayed(this, 1000);
        }
    };

    public void initLayout(){
        //获取手机屏幕大小
        DisplayMetrics dm = getResources().getDisplayMetrics();
        phoneHeight = dm.heightPixels;
        phoneWidth = dm.widthPixels;

        borderWidthPx = phoneWidth/xDirection;

        for(int i = 0; i < N; ++i){
            AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) views[i].getLayoutParams();
            layoutParams.width = size[2*i]*borderWidthPx;
            layoutParams.height = size[2*i+1]*borderWidthPx;

            layoutParams.x = position[level][i*2]*borderWidthPx;
            layoutParams.y = position[level][i*2+1]*borderWidthPx;
            views[i].setLayoutParams(layoutParams);
        }


    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
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

    public void back(View view) {
        if(gameState.empty()){
            Toast toast = Toast.makeText(this, "已经是第一步，无法回退",
                    Toast.LENGTH_SHORT);
            toast.show();
            soundPool.play(music[2], 1, 1, 0, 0, 1);
            return;
        }
        GameState g = (GameState)gameState.pop();
        switch(g.move){
            case "up": moveDown(g.view);break;
            case "down": moveUp(g.view);break;
            case "left": moveRight(g.view);break;
            case "right": moveLeft(g.view);break;
        }
        usedstep -= 1;
        showStep.setText(usedstep+"步");
        soundPool.play(music[1], 1, 1, 0, 0, 1);
    }

    public void newGame(View view) {
        initLayout();
        gameState.clear();
        usedstep=0;
        usedtime=0;
        showStep.setText(usedstep+"步");
        showTime.setText(usedtime+"s");
        soundPool.play(music[1], 1, 1, 0, 0, 1);
    }

    public void restart(View view){
        AlertDialog.Builder alertdialogbuilder=new AlertDialog.Builder(this);
        alertdialogbuilder.setMessage("确定重新开始对局吗？");
        //定义对话框2个按钮标题及接受事件的函数
        alertdialogbuilder.setPositiveButton("确定",confirm);
        alertdialogbuilder.setNegativeButton("取消",cancel);
        //创建并显示对话框
        AlertDialog alertdialogRestart=alertdialogbuilder.create();
        alertdialogRestart.show();
        soundPool.play(music[1], 1, 1, 0, 0, 1);
    }
    private DialogInterface.OnClickListener confirm=new DialogInterface.OnClickListener()
    {
        //使用该标记是为了增强程序在编译时候的检查，如果该方法并不是一个覆盖父类的方法，在编译时编译器就会报告错误。
        @Override
        public void onClick(DialogInterface arg0,int arg1)
        {
            initLayout();
            gameState.clear();
            usedstep=0;
            usedtime=0;
            showStep.setText(usedstep+"步");
            showTime.setText(usedtime+"s");
            soundPool.play(music[1], 1, 1, 0, 0, 1);
        }
    };
    private DialogInterface.OnClickListener cancel=new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface arg0,int arg1)
        {
            arg0.cancel();
        }
    };

    public void toHome(View view) {
        AlertDialog.Builder alertdialogbuilder=new AlertDialog.Builder(this);
        alertdialogbuilder.setMessage("确定回到主页吗？");
        //定义对话框2个按钮标题及接受事件的函数
        alertdialogbuilder.setPositiveButton("确定",backToHome);
        alertdialogbuilder.setNegativeButton("取消",cancel);
        //创建并显示对话框
        AlertDialog alertdialogToHome=alertdialogbuilder.create();
        alertdialogToHome.show();
        soundPool.play(music[1], 1, 1, 0, 0, 1);
    }
    private DialogInterface.OnClickListener backToHome=new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface arg0,int arg1)
        {
            replyIntent.putExtra(EXTRA_REPLY, "toHome");
            setResult(RESULT_OK, replyIntent);
            soundPool.play(music[1], 1, 1, 0, 0, 1);
            finish();
        }
    };

    public void next(View view) {
        level += 1;
        level %= maxLevel;
        soundPool.play(music[1], 1, 1, 0, 0, 1);
        newGame(view);
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String TAG = "MyGestureListener";

        public MyGestureListener() {

        }
        /**
         * 双击的第二下Touch down时触发
         *
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "onDoubleTap : " + e.getAction());
            return super.onDoubleTap(e);
        }

        /**
         * 双击的第二下 down和up都会触发，可用e.getAction()区分。
         *
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.i(TAG, "onDoubleTapEvent : " + e.getAction());
            return super.onDoubleTapEvent(e);
        }

        /**
         * down时触发
         *
         * @param e
         * @return
         */
        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        /**
         * Touch了滑动一点距离后，up时触发。
         *
         * @param e1
         * @param e2
         * @param velocityX
         * @param velocityY
         * @return
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            TextView t = findViewByPosition((int)e1.getX(), (int)e1.getY()-borderWidthPx);

            double down = e2.getY() - e1.getY();
            double right = e2.getX() - e1.getX();
            if(t != null){
                boolean ok;
                if(Math.abs(down)>Math.abs(right)){
                    if(down > 0) {
                        ok = moveDown(t);
                        if(ok)
                            gameState.push(new GameState(t, "down"));
                    }
                    else {
                        ok=moveUp(t);
                        if(ok)
                            gameState.push(new GameState(t, "up"));
                    }
                }else{
                    if(right > 0) {
                        ok=moveRight(t);
                        if(ok)
                            gameState.push(new GameState(t, "right"));
                    }
                    else {
                        ok=moveLeft(t);
                        if(ok)gameState.push(new GameState(t, "left"));
                    }
                }
                if(ok){
                    usedstep += 1;
                    showStep.setText(usedstep+"步");
                    soundPool.play(music[0], 1, 1, 0, 0, 1);
                }
                else
                    soundPool.play(music[2], 1, 1, 0, 0, 1);
            }
            if(win()) {
                showDialog();
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        /**
         * Touch了不移动一直 down时触发
         *
         * @param e
         */
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        /**
         * Touch了滑动时触发。
         *
         * @param e1
         * @param e2
         * @param distanceX
         * @param distanceY
         * @return
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {
            Log.i(TAG, "onScroll e1 : " + e1.getAction() + ", e2 : " + e2.getAction() + ", distanceX : " + distanceX + ", distanceY : " + distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /**
         * Touch了还没有滑动时触发
         *
         * @param e
         */
        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }
    }

    TextView findViewByPosition(int xPx, int yPx){
        for(int i = 0; i < N; ++i){
            AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) views[i].getLayoutParams();

            int viewxPx = layoutParams.x;
            int viewyPx = layoutParams.y;
            int widthPx =  layoutParams.width;
            int heightPx = layoutParams.height;

            if(xPx >= viewxPx && xPx <= viewxPx + widthPx && yPx >= viewyPx && yPx <= viewyPx + heightPx) {
                return views[i];
            }
        }
        return null;
    }
    public boolean moveUp(TextView t){
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) t.getLayoutParams();
        int viewxPx = layoutParams.x;
        int viewyPx = layoutParams.y;
        int widthPx =  layoutParams.width;
        int heightPx = layoutParams.height;

        for(int x = viewxPx + borderWidthPx/2; x < viewxPx + widthPx; x += borderWidthPx){
            if(findViewByPosition(x, viewyPx - borderWidthPx/2) != null)
                return false;
        }
        if(viewyPx - borderWidthPx/2 < 0)
            return false;
        layoutParams.y = t.getTop() - borderWidthPx;
        t.setLayoutParams(layoutParams);
        return true;
    }
    public boolean moveDown(TextView t){
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) t.getLayoutParams();

        int viewxPx = layoutParams.x;
        int viewyPx = layoutParams.y;
        int widthPx =  layoutParams.width;
        int heightPx = layoutParams.height;

        for(int x = viewxPx + borderWidthPx/2; x < viewxPx + widthPx; x += borderWidthPx){
            if( findViewByPosition(x, viewyPx + borderWidthPx/2 + heightPx)!= null){
                return false;
            }
        }
        if(viewyPx + heightPx +borderWidthPx/2 > yDirection * borderWidthPx)
            return false;
        layoutParams.y = t.getTop() + borderWidthPx;
        t.setLayoutParams(layoutParams);

        return true;

    }
    public boolean moveLeft(TextView t){
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) t.getLayoutParams();

        int viewxPx = layoutParams.x;
        int viewyPx = layoutParams.y;
        int widthPx =  layoutParams.width;
        int heightPx = layoutParams.height;

        for(int y = viewyPx + borderWidthPx/2; y < viewyPx + heightPx; y += borderWidthPx){
            if( findViewByPosition(viewxPx - borderWidthPx/2, y)!= null){
                return false;
            }
        }
        if(viewxPx - borderWidthPx/2 < 0)
            return false;
        layoutParams.x = t.getLeft() - borderWidthPx;
        t.setLayoutParams(layoutParams);

        return true;

    }
    public boolean moveRight(TextView t){
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) t.getLayoutParams();

        int viewxPx = layoutParams.x;
        int viewyPx = layoutParams.y;
        int widthPx =  layoutParams.width;
        int heightPx = layoutParams.height;

        for(int y = viewyPx + borderWidthPx/2; y < viewyPx + heightPx; y += borderWidthPx){
            if( findViewByPosition(viewxPx + widthPx + borderWidthPx/2, y)!= null){
                return false;
            }
        }
        if(viewxPx + borderWidthPx/2 + widthPx > borderWidthPx * xDirection)
            return false;
        layoutParams.x = t.getLeft() + borderWidthPx;
        t.setLayoutParams(layoutParams);

        return true;
    }
    public int DpToPx(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    public int PxToDp(Context context, int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public boolean win(){
        TextView cc = views[1];
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) cc.getLayoutParams();

        int viewxPx = layoutParams.x;
        int viewyPx = layoutParams.y;
        int widthPx =  layoutParams.width;
        int heightPx = layoutParams.height;

        if(viewxPx == borderWidthPx && viewyPx+heightPx == borderWidthPx*yDirection) {
            soundPool.play(music[3], 1, 1, 0, 0, 1);
            return true;
        }
        return false;
    }
    public String record(){
        String s[] = {"横刀立马", "兵随将转", "别无选择", "不惑之后", "不惑之前", "兵分三路", "侧面白虎", "倒影同步", "谦之有成", "山穷水尽"};
        return "关卡:"+s[level]+" 玩家:"+playerName+" 用时:"+usedtime+"s"+" 步数:"+usedstep+"步$";
    }
    public void add() {
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try{
            out=openFileOutput("winHistory",MODE_APPEND);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write(record());
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
    public void showDialog(){
        final EditText inputServer = new EditText(this);
        inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("过关请留名：").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                playerName = inputServer.getText().toString();
                if(playerName == "")playerName = "某大神";
                add();
                newGame(inputServer);
            }
        });
        builder.show();
    }
}
class GameState{
    TextView view;
    String move;
    GameState(TextView v, String m){view=v; move=m;}
}