package com.example.moe;

import android.content.Context;
import android.view.View;
import android.widget.MediaController;

public class CustomMediaController extends MediaController {

    public CustomMediaController(Context context) {
        super(context);
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        // 找到进度条并隐藏
        View progress = findViewById(android.R.id.progress);
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
    }
}