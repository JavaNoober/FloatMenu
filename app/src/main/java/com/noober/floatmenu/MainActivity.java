package com.noober.floatmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import com.noober.menu.CustomPopupMenu;

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

		final CustomPopupMenu popupMenu1 = new CustomPopupMenu(MainActivity.this, button1);
		popupMenu1.inflate(R.menu.popup_menu);
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				popupMenu1.show();
			}
		});
		final CustomPopupMenu popupMenu2 = new CustomPopupMenu(MainActivity.this, button2);
		popupMenu2.inflate(R.menu.popup_menu);
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				popupMenu2.show();
			}
		});


		final CustomPopupMenu popupMenu3 = new CustomPopupMenu(MainActivity.this, button3);
		popupMenu3.inflate(R.menu.popup_menu);
		button3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				popupMenu3.show();
			}
		});
		final CustomPopupMenu popupMenu4 = new CustomPopupMenu(MainActivity.this, button4);
		popupMenu4.inflate(R.menu.popup_menu);
		button4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				popupMenu4.show();
			}
		});
		final CustomPopupMenu popupMenu5 = new CustomPopupMenu(MainActivity.this, button5);
		popupMenu5.inflate(R.menu.popup_menu);
		button5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				popupMenu5.show();
			}
		});
	}
}
