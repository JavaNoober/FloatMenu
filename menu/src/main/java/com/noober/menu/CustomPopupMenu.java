package com.noober.menu;


import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Gravity;
import android.view.InflateException;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomPopupMenu extends PopupWindow{

	/** Menu tag name in XML. */
	private static final String XML_MENU = "menu";

	/** Group tag name in XML. */
	private static final String XML_GROUP = "group";

	/** Item tag name in XML. */
	private static final String XML_ITEM = "item";
	private Context context;
	private List<String> menuItemList;
	private View view;

	private int clickX;
	private int clickY;

	private int menuWidth;
	private int menuHeight;

	private final int offset = 20;

	private Point screenPoint;

	public CustomPopupMenu(Context context, View view) {
		super(context);
		setOutsideTouchable(true);
		setBackgroundDrawable(null);
		screenPoint = Display.getScreenMetrics(context);
		this.context = context;
		this.view = view;
		view.setOnTouchListener(new MenuTouchListener());
		menuItemList = new ArrayList<>();
	}

	public void inflate(int menuRes) {
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

		LinearLayout menuLayout = new LinearLayout(context);
		menuLayout.setBackgroundColor(Color.WHITE);
		menuLayout.setOrientation(LinearLayout.VERTICAL);
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(1000, ViewGroup.LayoutParams
				.WRAP_CONTENT);
		menuLayout.setLayoutParams(layoutParams);
		TypedValue typedValue = new TypedValue();
		context.getTheme()
				.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
		int[] attribute = new int[]{android.R.attr.selectableItemBackground};
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(typedValue.resourceId, attribute);
		for(int i = 0; i < menuItemList.size(); i ++){
			TextView textView = new TextView(context);
			textView.setClickable(true);
			textView.setPadding(30, 30, 30, 30);
			textView.setWidth(400);
			textView.setBackgroundDrawable(typedArray.getDrawable(0));
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			textView.setTextSize(17);
			textView.setTextColor(Color.BLACK);
			textView.setText(menuItemList.get(i));
			menuLayout.addView(textView);
		}
		int width = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		int height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		menuLayout.measure(width,height);
		menuWidth = menuLayout.getMeasuredWidth();
		menuHeight = menuLayout.getMeasuredHeight();
		Log.e("sss", menuWidth + "");
		setContentView(menuLayout);
	}

	private void parseMenu(XmlPullParser parser, AttributeSet attrs)
			throws XmlPullParserException, IOException {
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
//						menuState.readGroup(attrs);
						parser.next();
					} else if (tagName.equals(XML_ITEM)) {
//						menuState.readItem(attrs);
						readItem(attrs);
					} else if (tagName.equals(XML_MENU)) {
						// A menu start tag denotes a submenu for an item
//						SubMenu subMenu = menuState.addSubMenuItem();
//						registerMenu(subMenu, attrs);
//
//						 Parse the submenu into returned SubMenu
//						parseMenu(parser, attrs, subMenu);
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
		// Inherit attributes from the group as default value
//		int itemId = a.getResourceId(R.styleable.MenuItem_id, defaultItemId);
		CharSequence itemTitle = a.getText(R.styleable.MenuItem_menu_title);
		int itemIconResId = a.getResourceId(R.styleable.MenuItem_icon, 0);
		menuItemList.add(String.valueOf(itemTitle));
		a.recycle();
	}

	public void show(){
//		Log.e(?)

		if(clickX <= screenPoint.x / 2){
			if(clickY + menuHeight < screenPoint.y){
				final int[] location = new int[2];
				view.getLocationOnScreen(location);
				showAsDropDown(view, clickX,
						clickY - location[1] - view.getHeight() + offset);
			}else {
				showAtLocation(view, Gravity.TOP | Gravity.LEFT,
						clickX,
						clickY - menuHeight - offset);
			}
		}else {
			if(clickY + menuHeight < screenPoint.y){
				final int[] location = new int[2];
				view.getLocationOnScreen(location);
				showAsDropDown(view, clickX - menuWidth,
						clickY - location[1] - view.getHeight() + offset);
			}else {
				showAtLocation(view, Gravity.TOP,
						clickX,
						clickY - menuHeight - offset);
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
}
