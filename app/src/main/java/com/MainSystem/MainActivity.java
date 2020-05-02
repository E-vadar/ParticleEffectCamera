package com.MainSystem;

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
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.List;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{
    private String TAG="MainAcitivity";
    Button btn;
    ViewPager viewPager;
    TextView title;
    LinearLayout dotGroup;
    private boolean isSwitchPager = false;
    private int previousPosition = 0;
    private int[] imageUrl =new int[] {R.drawable.pg1, R.drawable.pg2, R.drawable.pg3, R.drawable.pg4};
    private List<ImageView> imageList;
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
        ActionBar actionBar = getSupportActionBar();
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
        int item = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % imageList.size());
        Log.d(TAG, "item=" + item);
        viewPager.setCurrentItem(item);
        dotGroup.getChildAt(previousPosition).setEnabled(true);
        title.setText(imageDescArrs[previousPosition]);
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
        imageDescArrs = new String[]{"Particle effect", "Face recognition", "Precise positioning", "Real-time rendering"};
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
            dotView = new View(getApplicationContext());
            dotView.setBackgroundResource(R.drawable.dot);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15, 15);
            if (i != 0) {
                params.leftMargin = 15;
            }
            dotView.setLayoutParams(params);
            dotView.setEnabled(false);
            dotGroup.addView(dotView);
        }
    }

    private class MyViewPager extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int newPostion = position % imageList.size();
            //获取到条目要显示的内容imageView
            ImageView img = imageList.get(newPostion);
            container.addView(img);
            return img;
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
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
        dotGroup.getChildAt(newPostion).setEnabled(true);
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

