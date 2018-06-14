/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tbs.tbsmis.file;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tbs.tbsmis.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CategoryBar extends View {
    private static final String LOG_TAG = "CategoryBar";

    private static final int MARGIN = 12;

    private static final int ANI_TOTAL_FRAMES = 10;

    private static final int ANI_PERIOD = 100;
    private Timer timer;

    private class Category {
        public long value;

        public long tmpValue; // used for animation

        public long aniStep; // animation step

        public int resImg;
    }

    private final ArrayList<CategoryBar.Category> categories = new ArrayList<CategoryBar.Category>();

    private long mFullValue;

    public void setFullValue(long value) {
        this.mFullValue = value;
    }

    public CategoryBar(Context context) {
        this(context, null);
    }

    public CategoryBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CategoryBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addCategory(int categoryImg) {
        CategoryBar.Category ca = new CategoryBar.Category();
        ca.resImg = categoryImg;
        this.categories.add(ca);
    }

    public boolean setCategoryValue(int index, long value) {
        if (index < 0 || index >= this.categories.size())
            return false;
        this.categories.get(index).value = value;
        this.invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable d = this.getDrawable(R.drawable.category_bar_empty);

        int width = this.getWidth() - CategoryBar.MARGIN * 2;
        int height = this.getHeight() - CategoryBar.MARGIN * 2;
        boolean isHorizontal = width > height;

        Rect bounds = null;
        if ( isHorizontal )
            bounds = new Rect(CategoryBar.MARGIN, 0, CategoryBar.MARGIN + width, d.getIntrinsicHeight());
        else
            bounds = new Rect(0, CategoryBar.MARGIN, d.getIntrinsicWidth(), CategoryBar.MARGIN + height);

        int beginning = CategoryBar.MARGIN;
        if ( !isHorizontal ) beginning += height;
        d.setBounds(bounds);
        d.draw(canvas);
        if (this.mFullValue != 0) {
            for (CategoryBar.Category c : this.categories) {
                long value = this.timer == null ? c.value : c.tmpValue;
                if ( isHorizontal ) {
                    int w = (int) (value * width / this.mFullValue);
                    if (w == 0)
                        continue;
                    bounds.left = beginning;
                    bounds.right = beginning + w;
                    d = this.getDrawable(c.resImg);
                    bounds.bottom = bounds.top + d.getIntrinsicHeight();
                    d.setBounds(bounds);
                    d.draw(canvas);
                    beginning += w;
                }
                else {
                    int h = (int) (value * height / this.mFullValue);
                    if (h == 0)
                        continue;
                    bounds.bottom = beginning;
                    bounds.top = beginning - h;
                    d = this.getDrawable(c.resImg);
                    bounds.right = bounds.left + d.getIntrinsicWidth();
                    d.setBounds(bounds);
                    d.draw(canvas);
                    beginning -= h;
                }
            }
        }
        if ( isHorizontal ) {
            bounds.left = 0;
            bounds.right = bounds.left + this.getWidth();
        }
        else {
            bounds.top = 0;
            bounds.bottom = bounds.top + this.getHeight();
        }
        d = this.getDrawable(R.drawable.category_bar_mask);
        d.setBounds(bounds);
        d.draw(canvas);
    }

    private Drawable getDrawable(int id) {
        return this.getContext().getResources().getDrawable(id);
    }

    private void stepAnimation() {
        if (this.timer == null)
            return;

        int finished = 0;
        for (CategoryBar.Category c : this.categories) {
            c.tmpValue += c.aniStep;
            if (c.tmpValue >= c.value) {
                c.tmpValue = c.value;
                finished++;
                if (finished >= this.categories.size()) {
                    // stop animation
                    this.timer.cancel();
                    this.timer = null;
                    Log.v(CategoryBar.LOG_TAG, "Animation stopped");
                    break;
                }
            }
        }

        this.postInvalidate();
    }

    public synchronized void startAnimation() {
        if (this.timer != null) {
            return;
        }

        Log.v(CategoryBar.LOG_TAG, "startAnimation");

        for (CategoryBar.Category c : this.categories) {
            c.tmpValue = 0;
            c.aniStep = c.value / CategoryBar.ANI_TOTAL_FRAMES;
        }

        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {

            @Override
			public void run() {
                CategoryBar.this.stepAnimation();
            }

        }, 0, CategoryBar.ANI_PERIOD);
    }
}
