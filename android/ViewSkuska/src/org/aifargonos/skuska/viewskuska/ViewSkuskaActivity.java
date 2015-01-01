package org.aifargonos.skuska.viewskuska;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class ViewSkuskaActivity extends Activity {
	
	
	
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
		
		SkuskaScallingPanningLayout scallingPanningView = new SkuskaScallingPanningLayout(this);
		scallingPanningView.setPadding(15, 10, 20, 25);
		
//		inception.addView(button, mlp);
//		layout.addView(inception);
		scallingPanningView.addView(button, mlp);
		scallingPanningView.addView(textView);
		layout.addView(scallingPanningView);
		setContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		setContentView(R.layout.activity_view_skuska);
	}
	
	
	
}
