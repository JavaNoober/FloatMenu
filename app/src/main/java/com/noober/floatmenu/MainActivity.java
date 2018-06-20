package com.noober.floatmenu;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.noober.floatmenu.dummy.DummyContent;
import com.noober.menu.FloatMenu;
import com.noober.menu.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

	private Point point = new Point();
	private ListView listView;
	private Button btn1;
	private Button btn2;
	private Button btnFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = findViewById(R.id.listview);
		btn1 = findViewById(R.id.btn_fun1);
		btn2 = findViewById(R.id.btn_fun2);
		btnFragment = findViewById(R.id.btn_fragment);
		init1();
		init2();
		init3();
		btnFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.ll_content).setVisibility(View.GONE);
                findViewById(R.id.fl_content).setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, ItemFragment.newInstance(0)).commitAllowingStateLoss();
            }
        });
	}

	private void init1(){
		final FloatMenu floatMenu = new FloatMenu(this, btn1);

        List<TestMenuItem> itemList = new ArrayList<>();
        TestMenuItem menuItem = new TestMenuItem();
        menuItem.setItem("菜单1");
        itemList.add(menuItem);
        TestMenuItem menuItem2 = new TestMenuItem();
        menuItem2.setItem("菜单2");
        itemList.add(menuItem2);
        TestMenuItem menuItem3 = new TestMenuItem();
        menuItem3.setItem("菜单2");
        itemList.add(menuItem3);
//		floatMenu.items("菜单1", "菜单2", "菜单3");
        floatMenu.items(itemList);
		floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {
			@Override
			public void onClick(View v, int position) {
				Toast.makeText(MainActivity.this, "菜单"+position, Toast.LENGTH_SHORT).show();
			}
		});

		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				floatMenu.show();
			}
		});

	}

	private void init2(){
		final FloatMenu floatMenu = new FloatMenu(this, btn2);
		floatMenu.inflate(R.menu.popup_menu);
		floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {
			@Override
			public void onClick(View v, int position) {
				Toast.makeText(MainActivity.this, "菜单"+position, Toast.LENGTH_SHORT).show();
			}
		});

		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				floatMenu.show();
			}
		});
	}

	private void init3(){
		List<String> itemList = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			itemList.add("菜单" + i);
		}

		ArrayAdapter<String> myAdapter = new ArrayAdapter<>(
				MainActivity.this, android.R.layout.simple_list_item_1, itemList);
		listView.setAdapter(myAdapter);



		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FloatMenu floatMenu = new FloatMenu(MainActivity.this);
				floatMenu.items("菜单1", "菜单2", "菜单3");
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

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        FloatMenu floatMenu = new FloatMenu(MainActivity.this);
        floatMenu.items("菜单1", "菜单2", "菜单3");
        floatMenu.show(point);
    }
}
