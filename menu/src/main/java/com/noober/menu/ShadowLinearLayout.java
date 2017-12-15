package com.noober.menu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by xiaoqi on 2017/12/15.
 */

public class ShadowLinearLayout extends LinearLayout{
	Paint paint;
	int width;
	int height;
	int shadowWidth;
	LinearGradient lg;
	LinearGradient lg2;
	LinearGradient lg3;
	LinearGradient lg4;
	public ShadowLinearLayout(Context context) {
		super(context);
		paint = new Paint();
		shadowWidth = Display.dip2px(getContext(), 10);
		lg = new LinearGradient(0,shadowWidth / 2,
				0,0,Color.parseColor("#b7b7b7"),Color.parseColor("#00fbfbfb"), Shader.TileMode.MIRROR);
		lg2 = new LinearGradient(width - shadowWidth/2,shadowWidth / 2,
				width,shadowWidth / 2,Color.parseColor("#b7b7b7"),Color.parseColor("#00fbfbfb"), Shader.TileMode.MIRROR);
		lg3 = new LinearGradient(0,height - shadowWidth / 2,
				0,height,Color.parseColor("#b7b7b7"),Color.parseColor("#00fbfbfb"), Shader.TileMode.MIRROR);
		lg4 = new LinearGradient(shadowWidth / 2,shadowWidth / 2,
				0,shadowWidth / 2,Color.parseColor("#b7b7b7"),Color.parseColor("#00fbfbfb"), Shader.TileMode.MIRROR);
//		paint.setStyle(Paint.Style.STROKE);
//		paint.setShadowLayer(360f, Display.dip2px(context, 100),
//				Display.dip2px(context, 100), Color.parseColor("#bfbfbf"));
		setWillNotDraw(false);
	}

	public ShadowLinearLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public ShadowLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = getMeasuredWidth();
		height = getMeasuredHeight();
	}

	@Override
	protected void onDraw(Canvas canvas) {


		paint.setStrokeWidth(width * 2);
		paint.setShader(lg);
		canvas.drawLine(shadowWidth, shadowWidth / 2, shadowWidth, 0, paint);
		lg2 = new LinearGradient(width - shadowWidth/2,shadowWidth / 2,
				width,shadowWidth / 2,Color.parseColor("#b7b7b7"),Color.parseColor("#00fbfbfb"), Shader.TileMode.MIRROR);
		paint.setShader(lg2);
		paint.setStrokeWidth(height);
		canvas.drawLine(width - shadowWidth/2, shadowWidth / 2, width - shadowWidth/2, height - shadowWidth / 2, paint);

		paint.setShader(lg3);
		paint.setStrokeWidth(width * 2);
		canvas.drawLine(0, height - shadowWidth / 2, 0, height, paint);


		paint.setShader(lg4);
		paint.setStrokeWidth(height * 2);
		canvas.drawLine(shadowWidth / 2, shadowWidth / 2, 0, shadowWidth / 2, paint);

//		canvas.drawLine(0, 0, width, 0, paint);
//		canvas.drawLine(width, 0, width, height, paint);
//		canvas.drawLine(width, height, 0, height, paint);
//		canvas.drawLine(0, height, 0, 0, paint);
		super.onDraw(canvas);

	}
}
