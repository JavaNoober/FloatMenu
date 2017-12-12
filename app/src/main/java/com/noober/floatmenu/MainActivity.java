package com.noober.floatmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.noober.menu.FloatMenu;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Button button1 = findViewById(R.id.button1);
		Button button2 = findViewById(R.id.button2);
		Button button3 = findViewById(R.id.button3);
		final Button button4 = findViewById(R.id.button4);
		Button button5 = findViewById(R.id.button5);

//		button2.setVisibility(View.GONE);
//		button3.setVisibility(View.GONE);
//		button4.setVisibility(View.GONE);
//		button5.setVisibility(View.GONE);

		final FloatMenu popupMenu1 = new FloatMenu(MainActivity.this, button1);
		popupMenu1.inflate(R.menu.popup_menu);
		button1.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				popupMenu1.show();
				return true;
			}
		});

		final FloatMenu popupMenu2 = new FloatMenu(MainActivity.this, button2);
		popupMenu2.inflate(R.menu.popup_menu);
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupMenu2.show();
			}
		});


		final FloatMenu popupMenu3 = new FloatMenu(MainActivity.this, button3);
		popupMenu3.inflate(R.menu.popup_menu);
		button3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				popupMenu3.show();
			}
		});
		final FloatMenu popupMenu4 = new FloatMenu(MainActivity.this, button4);
		popupMenu4.inflate(R.menu.popup_menu);
		button4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				popupMenu4.show();
			}
		});
		final FloatMenu popupMenu5 = new FloatMenu(MainActivity.this, button5);
		popupMenu5.inflate(R.menu.popup_menu);
		button5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				popupMenu5.show();
			}
		});
	}
}
