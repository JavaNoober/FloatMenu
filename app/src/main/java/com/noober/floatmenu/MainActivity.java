package com.noober.floatmenu;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.noober.menu.FloatMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private Point point = new Point();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView listView = findViewById(R.id.listview);
		List<String> itemList = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			itemList.add("菜单" + i);
		}

		ArrayAdapter<String> myAdapter = new ArrayAdapter<>(
				MainActivity.this, android.R.layout.simple_list_item_1, itemList);
		listView.setAdapter(myAdapter);

		final FloatMenu floatMenu = new FloatMenu(MainActivity.this);
		floatMenu.items("菜单1", "菜单2", "菜单3");

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.e("onItemClick", view.getVisibility() + "  " + position);
				floatMenu.show(point);
			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			point.x = (int) ev.getRawX();
			point.y = (int) ev.getRawY();
		}
		return super.dispatchTouchEvent(ev);
	}
}
