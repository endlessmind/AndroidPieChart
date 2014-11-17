package com.example.androidpiechart;

import java.util.Random;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity {

	PieChart pieChart;
	Button btnGenerate;
	CheckBox chbSort, chbShadow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pieChart = (PieChart) findViewById(R.id.pieChart1);
		btnGenerate = (Button) findViewById(R.id.btnGenerate);
		chbSort = (CheckBox) findViewById(R.id.chbSort);
		chbShadow = (CheckBox) findViewById(R.id.chbShadow);
		
		btnGenerate.setOnClickListener(btnGenerate_Click);
		chbSort.setOnCheckedChangeListener(Check_Change);
		chbShadow.setOnCheckedChangeListener(Check_Change);

		CreateTestPieces();
		
		
	}
	
	private void CreateTestPieces() {
		PiePiece p;
		for (int i = 0;i < 25; i++) {
			Random r = new Random();
			p = new PiePiece();
			p.value = r.nextInt(1000);
			p.Text = "Text " + i;
			pieChart.addPiece(p);
		}
	}
	
	private OnCheckedChangeListener Check_Change = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton view, boolean checked) {
			if (view.equals(chbShadow)) {
				pieChart.setHasShadow(checked);
			} else {
				pieChart.setIsSortingEnabled(checked);
			}
		}
		
	};
	
	private OnClickListener btnGenerate_Click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			pieChart.clearPieces();
			CreateTestPieces();
		}
		
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
