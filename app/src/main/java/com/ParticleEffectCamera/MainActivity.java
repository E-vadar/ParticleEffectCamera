package com.ParticleEffectCamera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.List;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{
    private String TAG="CVSAMPLE";
    Button btn;
    ViewPager viewPager;
    TextView title;
    LinearLayout dotGroup;
    private boolean isSwitchPager = false; //默认不切换
    private int previousPosition = 0; //默认为0
    private int[] imageUrl =new int[] {R.drawable.pg1, R.drawable.pg2, R.drawable.pg3, R.drawable.pg4};
    private List<ImageView> imageList;
    private LinearLayout dot_group;//小圆点
    private String[] imageDescArrs;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();     //取消标题头actionbar
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        btn=findViewById(R.id.camera_btn);
        viewPager=findViewById(R.id.viewPager);
        title=findViewById(R.id.title);
        dotGroup=findViewById(R.id.dot_group);
        btn.setOnClickListener(this);
        initView();
    }
    private void initView() {
        initViewPagerData();
        viewPager.setAdapter(new MyViewPager());

        //设置当前viewPager要显示的第几个条目
        int item = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % imageList.size());
        Log.d(TAG, "item=" + item);
        viewPager.setCurrentItem(item);

        //把第一个小圆点设置成白色，显示第一个TExtView内容
        dotGroup.getChildAt(previousPosition).setEnabled(true);
        title.setText(imageDescArrs[previousPosition]);
        //设置viewPager滑动监听事件
        viewPager.addOnPageChangeListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isSwitchPager) {
                    SystemClock.sleep(3000);
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();

    }

    private void initViewPagerData() {
        imageDescArrs = new String[]{"趣味贴纸", "人脸识别", "精确定位", "实时渲染"};
        imageList = new ArrayList<ImageView>();

        ImageView im;
        View dotView;
        for (int i = 0; i < imageUrl.length; i++) {
            im = new ImageView(this);
            Glide.with(this).load(imageUrl[i])
                     .transition(DrawableTransitionOptions.withCrossFade())
                     .centerCrop()
                     .into(im);
            imageList.add(im);
            //准备小圆点数据
            dotView = new View(getApplicationContext());
            dotView.setBackgroundResource(R.drawable.dot);
            //设置小圆点宽和高
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15, 15);
            //设置每个小圆点之间的距离
            if (i != 0) {
                params.leftMargin = 15;
            }
            dotView.setLayoutParams(params);
            //设置小圆点状态
            dotView.setEnabled(false);
            //把dotView加入到线性布局中
            dotGroup.addView(dotView);
        }
    }
    private class MyViewPager extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }
        //初始化每个条目要显示的内容
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int newPostion = position % imageList.size();
            //获取到条目要显示的内容imageView
            ImageView img = imageList.get(newPostion);
            container.addView(img);
            return img;
        }
        //是否复用当前view
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        //销毁条目
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }
    @Override
    public void onPageSelected(int position) {
        int newPostion = position % imageList.size();
        //取出postion的位置小圆点设置为true
        dotGroup.getChildAt(newPostion).setEnabled(true);
        //把一个小圆点设置为false
        dotGroup.getChildAt(previousPosition).setEnabled(false);
        title.setText(imageDescArrs[newPostion]);
        previousPosition = newPostion;
    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSwitchPager = false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.camera_btn) {
            Intent intent = new Intent(this.getApplicationContext(), CameraViewActivity.class);
            startActivity(intent);
        }
    }
}

