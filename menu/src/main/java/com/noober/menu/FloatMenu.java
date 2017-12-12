package com.noober.menu;


import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.Gravity;
import android.view.InflateException;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoqi on 2017/12/11.
 */

public class FloatMenu extends PopupWindow{

	/** Menu tag name in XML. */
	private static final String XML_MENU = "menu";

	/** Group tag name in XML. */
	private static final String XML_GROUP = "group";

	/** Item tag name in XML. */
	private static final String XML_ITEM = "item";

	private static final int ANCHORED_GRAVITY = Gravity.TOP | Gravity.START;

	private final int DEFAULT_MENU_WIDTH;
	private final int VERTICAL_OFFSET;
	private int ANIM_START_OFFSET;

	private Context context;
	private List<String> menuItemList;
	private View view;
	private Point screenPoint;
	private int clickX;
	private int clickY;
	private int menuWidth;
	private int menuHeight;
	private boolean isStartDismissAnim = false;
	private boolean isPlayingAnim = false;
	private LinearLayout menuLayout;
	private StartPoint startPoint;

	private OnItemClickListener onItemClickListener;

	public enum StartPoint{
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}

	public interface OnItemClickListener {
		void onClick(View v, int position);
	}

	public FloatMenu(Context context, View view) {
		super(context);
//		setAnimationStyle(R.style.popwin_anim_bottom);
		setOutsideTouchable(true);
		setFocusable(true);
		setBackgroundDrawable(null);
		view.setOnTouchListener(new MenuTouchListener());
		this.context = context;
		this.view = view;
		VERTICAL_OFFSET = Display.dip2px(context, 10);
		DEFAULT_MENU_WIDTH = Display.dip2px(context, 180);
		screenPoint = Display.getScreenMetrics(context);
		menuItemList = new ArrayList<>();
	}

	public void inflate(int menuRes) {
		inflate(menuRes, DEFAULT_MENU_WIDTH);
	}

	public void inflate(int menuRes, int itemWidth) {
		XmlResourceParser parser = null;
		try {
			parser = context.getResources().getLayout(menuRes);
			AttributeSet attrs = Xml.asAttributeSet(parser);
			parseMenu(parser, attrs);
		} catch (XmlPullParserException e) {
			throw new InflateException("Error inflating menu XML", e);
		} catch (IOException e) {
			throw new InflateException("Error inflating menu XML", e);
		} finally {
			if (parser != null) parser.close();
		}

		menuLayout = new LinearLayout(context);
		menuLayout.setBackgroundColor(Color.WHITE);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			menuLayout.setOutlineProvider(new ViewOutlineProvider() {
				@Override
				public void getOutline(View view, Outline outline) {
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
						outline.setRect(0, 0, view.getWidth(), view.getHeight());
					}
				}
			});
			menuLayout.setElevation(Display.dip2px(context, 10));
			menuLayout.setTranslationZ(Display.dip2px(context, 10));
		}
		menuLayout.setOrientation(LinearLayout.VERTICAL);
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		menuLayout.setLayoutParams(layoutParams);
		int padding = Display.dip2px(context, 12);
		for(int i = 0; i < menuItemList.size(); i ++){
			TextView textView = new TextView(context);
			textView.setClickable(true);
			textView.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.selector_item));
			textView.setPadding(padding, padding, padding, padding);
			textView.setWidth(itemWidth);
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
			textView.setTextSize(15);
			textView.setTextColor(Color.BLACK);
			textView.setText(menuItemList.get(i));
			if(onItemClickListener != null){
				textView.setOnClickListener(new ItemOnClickListener(i));
			}
			menuLayout.addView(textView);
		}
		int width = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		int height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		menuLayout.measure(width,height);
		menuWidth = menuLayout.getMeasuredWidth();
		menuHeight = menuLayout.getMeasuredHeight();
		ANIM_START_OFFSET = menuWidth / 5;
		setContentView(menuLayout);
		setWidth(menuWidth);
		setHeight(menuHeight);
	}

	private void parseMenu(XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();
		String tagName;
		boolean lookingForEndOfUnknownTag = false;
		String unknownTagName = null;

		// This loop will skip to the menu start tag
		do {
			if (eventType == XmlPullParser.START_TAG) {
				tagName = parser.getName();
				if (tagName.equals(XML_MENU)) {
					// Go to next tag
					eventType = parser.next();
					break;
				}
				throw new RuntimeException("Expecting menu, got " + tagName);
			}
			eventType = parser.next();
		} while (eventType != XmlPullParser.END_DOCUMENT);

		boolean reachedEndOfMenu = false;
		while (!reachedEndOfMenu) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if (lookingForEndOfUnknownTag) {
						break;
					}
					tagName = parser.getName();
					if (tagName.equals(XML_GROUP)) {
					//	parser group
						parser.next();
					} else if (tagName.equals(XML_ITEM)) {
						readItem(attrs);
					} else if (tagName.equals(XML_MENU)) {
						// A menu start tag denotes a submenu for an item
						//pares subMenu
						parser.next();
					} else {
						lookingForEndOfUnknownTag = true;
						unknownTagName = tagName;
					}
					break;

				case XmlPullParser.END_TAG:
					tagName = parser.getName();
					if (lookingForEndOfUnknownTag && tagName.equals(unknownTagName)) {
						lookingForEndOfUnknownTag = false;
						unknownTagName = null;
					} else if (tagName.equals(XML_GROUP)) {

					} else if (tagName.equals(XML_ITEM)) {

					} else if (tagName.equals(XML_MENU)) {
						reachedEndOfMenu = true;
					}
					break;

				case XmlPullParser.END_DOCUMENT:
					throw new RuntimeException("Unexpected end of document");
			}

			eventType = parser.next();
		}
	}

	private void readItem(AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuItem);
		CharSequence itemTitle = a.getText(R.styleable.MenuItem_menu_title);
//		int itemIconResId = a.getResourceId(R.styleable.MenuItem_icon, 0);
		menuItemList.add(String.valueOf(itemTitle));
		a.recycle();
	}

	public void show(){
		if(isShowing() || isPlayingAnim){
			return;
		}
		isStartDismissAnim = false;
		//it is must ,other wise 'setOutsideTouchable' will not work under Android5.0
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
			setBackgroundDrawable(new BitmapDrawable());
		}
		if(clickX <= screenPoint.x / 2){
			if(clickY + menuHeight < screenPoint.y){
				openAnimFromTOP_LEFT();
				showAtLocation(view, ANCHORED_GRAVITY, clickX ,
						clickY + VERTICAL_OFFSET);
			}else {
				openAnimFromBOTTOM_LEFT();
				final int[] location = new int[2];
				view.getLocationOnScreen(location);
				showAtLocation(view, ANCHORED_GRAVITY, clickX ,
						clickY - menuHeight - VERTICAL_OFFSET);
			}
		}else {
			if(clickY + menuHeight < screenPoint.y){
				openAnimFromTOP_RIGHT();
				showAtLocation(view, ANCHORED_GRAVITY,
						clickX - menuWidth ,
						clickY + VERTICAL_OFFSET);
			}else {
				openAnimFromBOTTOM_RIGHT();
				showAtLocation(view, ANCHORED_GRAVITY,
						clickX - menuWidth,
						clickY - menuHeight - VERTICAL_OFFSET);
			}
		}

	}

	@Override
	public void dismiss() {
		if(!isShowing() || isPlayingAnim){
			return;
		}
		if(isStartDismissAnim){
			super.dismiss();
		}else {
			if(startPoint == StartPoint.TOP_LEFT){
				closeAnimFromTOP_LEFT();
			}else if(startPoint == StartPoint.TOP_RIGHT){
				closeAnimFromTOP_RIGHT();
			}else if(startPoint == StartPoint.BOTTOM_RIGHT){
				closeAnimFromBOTTOM_RIGHT();
			}else if(startPoint == StartPoint.BOTTOM_LEFT){
				closeAnimFromBOTTOM_LEFT();
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isStartDismissAnim = true;
					dismiss();
				}
			}, 500);
		}
	}

	@Override
	public void setOnDismissListener(OnDismissListener onDismissListener) {
		super.setOnDismissListener(onDismissListener);
	}

	public void setOnItemClickListener(final OnItemClickListener onItemClickListener){
		this.onItemClickListener = onItemClickListener;
		if(onItemClickListener != null){
			for (int i = 0; i < menuLayout.getChildCount(); i ++){
				View view = menuLayout.getChildAt(i);
				view.setOnClickListener(new ItemOnClickListener(i));
			}
		}
	}

	class MenuTouchListener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_UP){
				clickX = (int) event.getRawX();
				clickY = (int) event.getRawY();
			}
			return false;
		}
	}

	class ItemOnClickListener implements View.OnClickListener {
		int position;

		public ItemOnClickListener(int position){
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if(onItemClickListener != null){
				onItemClickListener.onClick(v, position);
			}
		}
	}


	private void openAnimFromTOP_LEFT(){
		startPoint = StartPoint.TOP_LEFT;
		ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
				Animation.ABSOLUTE, ANIM_START_OFFSET, Animation.ABSOLUTE, 0f);
		scaleAnimation.setDuration(200); //动画持续时间
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setAnimationListener(new AnimationListener());
		menuLayout.setAnimation(scaleAnimation);
		scaleAnimation.start();
	}

	private void openAnimFromBOTTOM_LEFT(){
		startPoint = StartPoint.BOTTOM_LEFT;
		ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
				Animation.ABSOLUTE, ANIM_START_OFFSET, Animation.ABSOLUTE, menuWidth);
		scaleAnimation.setDuration(200); //动画持续时间
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setAnimationListener(new AnimationListener());
		menuLayout.startAnimation(scaleAnimation);
	}

	private void openAnimFromTOP_RIGHT(){
		startPoint = StartPoint.TOP_RIGHT;
		ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
				Animation.ABSOLUTE, menuWidth - ANIM_START_OFFSET, Animation.ABSOLUTE, 0f);
		scaleAnimation.setDuration(200); //动画持续时间
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setAnimationListener(new AnimationListener());
		menuLayout.startAnimation(scaleAnimation);
	}

	private void openAnimFromBOTTOM_RIGHT(){
		startPoint = StartPoint.BOTTOM_RIGHT;
		ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
				Animation.ABSOLUTE, menuWidth - ANIM_START_OFFSET, Animation.ABSOLUTE, menuWidth);
		scaleAnimation.setDuration(200); //动画持续时间
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setAnimationListener(new AnimationListener());
		menuLayout.startAnimation(scaleAnimation);
	}

	private void closeAnimFromTOP_LEFT(){
		ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f,
				Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, 0f);
		scaleAnimation.setDuration(200); //动画持续时间
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setAnimationListener(new AnimationListener());
		menuLayout.startAnimation(scaleAnimation);
	}

	private void closeAnimFromBOTTOM_LEFT(){
		ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f,
				Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, menuHeight);
		scaleAnimation.setDuration(200); //动画持续时间
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setAnimationListener(new AnimationListener());
		menuLayout.startAnimation(scaleAnimation);
	}

	private void closeAnimFromTOP_RIGHT(){
		ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f,
				Animation.ABSOLUTE, menuWidth, Animation.ABSOLUTE, 0f);
		scaleAnimation.setDuration(200); //动画持续时间
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setAnimationListener(new AnimationListener());
		menuLayout.startAnimation(scaleAnimation);
	}

	private void closeAnimFromBOTTOM_RIGHT(){
		ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f,
				Animation.ABSOLUTE, menuWidth, Animation.ABSOLUTE, menuHeight);
		scaleAnimation.setDuration(200); //动画持续时间
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setAnimationListener(new AnimationListener());
		menuLayout.startAnimation(scaleAnimation);
	}

	class AnimationListener implements Animation.AnimationListener{

		@Override
		public void onAnimationStart(Animation animation) {
			isPlayingAnim = true;
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			isPlayingAnim = false;
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	}
}
