package org.aifargonos.games.genericmahjong;

import org.aifargonos.games.genericmahjong.gui.StoneView;
import org.aifargonos.games.genericmahjong.skuska.SkuskaScallingPanningLayout;
import org.aifargonos.games.genericmahjong.skuska.ViewSkuskaLayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ViewSkuskaLayout layout = new ViewSkuskaLayout(this);
//		
//		ViewSkuskaLayout inception = new ViewSkuskaLayout(this);
//		inception.setPadding(15, 10, 20, 25);
		
		Button button = new Button(this);
		button.setText("BuTtOn");
		MarginLayoutParams mlp = new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mlp.leftMargin = mlp.bottomMargin = 25;
		mlp.rightMargin = mlp.topMargin = 17;
		
		TextView textView = new TextView(this);
		textView.setText("TextView");
		
		StoneView stoneView = new StoneView(this);
		stoneView.setPadding(15, 10, 20, 25);
		
		SkuskaScallingPanningLayout scallingPanningView = new SkuskaScallingPanningLayout(this);
		scallingPanningView.setPadding(15, 10, 20, 25);
		
//		inception.addView(button, mlp);
//		layout.addView(inception);
//		scallingPanningView.addView(button, mlp);
//		scallingPanningView.addView(textView);
//		scallingPanningView.addView(stoneView);
//		layout.addView(scallingPanningView);
		layout.addView(stoneView);
		setContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		setContentView(R.layout.activity_main);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
}
