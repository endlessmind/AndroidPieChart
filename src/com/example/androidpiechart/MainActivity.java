package com.example.androidpiechart;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	PieChart pieChart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pieChart = (PieChart) findViewById(R.id.pieChart1);
		
		pieChart.setMax(100);
		PiePiece p = new PiePiece();
		p.value = 5;
		p.Text = "Text 1";
		pieChart.addPiece(p);
		
		p = new PiePiece();
		p.value = 7;
		p.Text = "Text 2";
		pieChart.addPiece(p);
		
		p = new PiePiece();
		p.value = 13;
		p.Text = "Text 3";
		pieChart.addPiece(p);
		
		p = new PiePiece();
		p.value = 15;
		p.Text = "Text 4";
		pieChart.addPiece(p);
		
		p = new PiePiece();
		p.value = 20;
		p.Text = "Text 5";
		pieChart.addPiece(p);
		
		p = new PiePiece();
		p.value = 40;
		p.Text = "Text 6";
		pieChart.addPiece(p);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
