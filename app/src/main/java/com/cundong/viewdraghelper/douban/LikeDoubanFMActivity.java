package com.cundong.viewdraghelper.douban;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cundong.viewdraghelper.R;
import com.cundong.viewdraghelper.util.Cheeses;

/**
 * Created by cundong on 2016/1/12.
 *
 * 类似豆瓣FM那种上下滑动播放器
 */
public class LikeDoubanFMActivity extends AppCompatActivity {

    private static final String TAG = LikeDoubanFMActivity.class.getSimpleName();

    private DragLayout mDragLayout = null;

    private ListView mMusicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_like_douban_fm);

        mDragLayout = (DragLayout) findViewById(R.id.drag_layout);

        mDragLayout.setOnViewDragListener(new DragLayout.OnViewDragListener() {
            @Override
            public void onClose() {
                Log.i(TAG, "onClose");
            }

            @Override
            public void onOpen() {
                Log.i(TAG, "onOpen");
            }

            @Override
            public void onDraging(float percent) {
                Log.i(TAG, "DragLayout" + percent);
            }
        });

        mMusicList = (ListView) findViewById(R.id.music_list);
        mMusicList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.CHEESES));
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
