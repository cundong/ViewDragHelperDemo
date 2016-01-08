package com.cundong.viewdraghelper;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cundong.viewdraghelper.qq.DragLayout;
import com.cundong.viewdraghelper.qq.MainContainer;
import com.cundong.viewdraghelper.util.Cheeses;

/**
 * Created by liucundong on 2016/1/7.
 *
 * 类似QQ那种侧滑
 */
public class LikeQQActivity extends AppCompatActivity {

    private static final String TAG = LikeQQActivity.class.getSimpleName();

    private DragLayout mDragLayout;
    private MainContainer mMainContainer;

    private ListView mLeftList, mMainList;
    private ImageView mTitleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_like_qq);

        mLeftList = (ListView) findViewById(R.id.left_list);
        mMainList = (ListView) findViewById(R.id.main_list);
        mTitleImageView = (ImageView) findViewById(R.id.title_img);
        mTitleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDragLayout.open();
            }
        });

        mMainContainer = (MainContainer) findViewById(R.id.main_layout);

        mDragLayout = (DragLayout) findViewById(R.id.drag_layout);
        mDragLayout.setOnDragStatusListener(new DragLayout.OnDragStatusChangedListener() {
            @Override
            public void onClose() {
                Log.d(TAG, "close");
            }

            @Override
            public void onOpen() {
                Log.d(TAG, "open");
            }

            @Override
            public void onDraging(float percent) {
                Log.d(TAG, "percent:" + percent);
                mTitleImageView.setAlpha(1-percent);
            }
        });

        mLeftList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.CHEESES) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView mText = ((TextView) view);
                mText.setTextColor(Color.WHITE);
                return view;
            }
        });

        mMainList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.CHEESES));

        mMainContainer.setDragLayout(mDragLayout);
    }

    @Override
    public void onBackPressed() {


        if(mDragLayout.getStatus() == DragLayout.Status.Open) {
            mDragLayout.close();
        } else {
            super.onBackPressed();
        }
    }
}