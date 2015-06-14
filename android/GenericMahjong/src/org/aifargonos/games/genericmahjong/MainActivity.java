package org.aifargonos.games.genericmahjong;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aifargonos.games.genericmahjong.data.Coordinates;
import org.aifargonos.games.genericmahjong.engine.Board;
import org.aifargonos.games.genericmahjong.engine.DictStoneContent;
import org.aifargonos.games.genericmahjong.engine.Engine;
import org.aifargonos.games.genericmahjong.engine.EngineListener;
import org.aifargonos.games.genericmahjong.engine.Stone;
import org.aifargonos.games.genericmahjong.engine.StoneContent;
import org.aifargonos.games.genericmahjong.gui.BoardView;
import org.aifargonos.games.genericmahjong.gui.DictStoneContentDrawer;
import org.aifargonos.games.genericmahjong.gui.StoneContentDrawer;
import org.aifargonos.games.genericmahjong.gui.StoneView;
import org.aifargonos.games.genericmahjong.skuska.SkuskaScallingPanningLayout;
import org.aifargonos.games.genericmahjong.skuska.ViewSkuskaLayout;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	
	
	public static final Map<Class<?>, StoneContentDrawer> STONE_CONTENT_DRAWERS;
	static {
		final Map<Class<?>, StoneContentDrawer> stoneContentDrawers = new HashMap<Class<?>, StoneContentDrawer>();
		stoneContentDrawers.put(DictStoneContent.class, new DictStoneContentDrawer());
		STONE_CONTENT_DRAWERS = Collections.unmodifiableMap(stoneContentDrawers);
	}
	
	
	
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
		stoneView.setStone(new Stone(new Coordinates(-1, 0, 0)));
		stoneView.getStone().setContent(new DictStoneContent("Tj", null));
		stoneView.setPadding(15, 10, 20, 25);
		
		SkuskaScallingPanningLayout scallingPanningView = new SkuskaScallingPanningLayout(this);
		scallingPanningView.setPadding(15, 10, 20, 25);
		
		
		Board board = loadBoard();
		Engine engine = new Engine();
		board.setEngine(engine);
		
		board.generate(loadStoneContents());
		
		
		StoneView stoneView1 = new StoneView(this);
		stoneView1.setStone(new Stone(new Coordinates(-1, 0, 0)));
		StoneView stoneView2 = new StoneView(this);
		stoneView2.setStone(new Stone(new Coordinates( 1,-1, 0)));
		StoneView stoneView3 = new StoneView(this);
		stoneView3.setStone(new Stone(new Coordinates( 0, 0, 1)));
		
		final BoardView boardView = new BoardView(this);
//		boardView.addView(stoneView2);
//		boardView.addView(stoneView1);
//		boardView.addView(stoneView3);
		
//		for(Stone stone : board.getStones()) {
//			StoneView sv = new StoneView(this);
//			sv.setStone(stone);
//			boardView.addView(sv);
//		}
		boardView.setBoard(board);
		
		engine.addEngineListener(new EngineListener() {
			@Override
			public void stoneRemoved(final Stone stone) {
				final StoneView stoneView = boardView.getStoneView(stone);
				if(stoneView != null) {
					boardView.removeView(stoneView);
				}
			}
			@Override
			public void stoneAdded(final Stone stone) {
				StoneView stoneView = new StoneView(MainActivity.this);
				stoneView.setStone(stone);
				boardView.addView(boardView);
			}
			@Override
			public void selectionChanged(final Stone stone) {
				final StoneView stoneView = boardView.getStoneView(stone);
				if(stoneView != null) {
					stoneView.postInvalidate();
				}
			}
		});
		
//		inception.addView(button, mlp);
//		layout.addView(inception);
//		scallingPanningView.addView(button, mlp);
//		scallingPanningView.addView(textView);
//		scallingPanningView.addView(stoneView);
//		layout.addView(scallingPanningView);
//		layout.addView(stoneView);
//		layout.addView(boardView);
//		setContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(boardView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
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
	
	
	
	private Board loadBoard() {
		
		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return null;// TODO [layout_loading] proper error handling!
		}
		
		final File dir = Environment.getExternalStorageDirectory();
		final File file = new File(new File(dir, "GenericMahjong"), "difficult-layout.xml");// TODO [layout_loading] parametrize
		
		Reader reader = null;
		
		try {
			reader = new FileReader(file);
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(reader);
			
			parser.nextTag();
			parser.require(XmlPullParser.START_TAG, "", "field");
			
			final Board board = new Board();
			
			while(parser.nextTag() != XmlPullParser.END_TAG) {
				parser.require(XmlPullParser.START_TAG, "", "coordinates");
				
				parser.nextTag();
				parser.require(XmlPullParser.START_TAG, "", "x");
				String content = parser.nextText();
				final int x = Integer.valueOf(content);
				parser.require(XmlPullParser.END_TAG, "", "x");
				
				parser.nextTag();
				parser.require(XmlPullParser.START_TAG, "", "y");
				content = parser.nextText();
				final int y = Integer.valueOf(content);
				parser.require(XmlPullParser.END_TAG, "", "y");
				
				parser.nextTag();
				parser.require(XmlPullParser.START_TAG, "", "z");
				content = parser.nextText();
				final int z = Integer.valueOf(content);
				parser.require(XmlPullParser.END_TAG, "", "z");
				
				board.put(new Stone(new Coordinates(x, y, z)));
				
				parser.nextTag();
				parser.require(XmlPullParser.END_TAG, "", "coordinates");
			}
			
			parser.require(XmlPullParser.END_TAG, "", "field");
			
			return board;
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return null;// TODO [layout_loading] proper error handling!
		} catch(NumberFormatException e) {
			e.printStackTrace();
			return null;// TODO [layout_loading] proper error handling!
		} catch(XmlPullParserException e) {
			e.printStackTrace();
			return null;// TODO [layout_loading] proper error handling!
		} catch(IOException e) {
			e.printStackTrace();
			return null;// TODO [layout_loading] proper error handling!
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	
	private List<StoneContent> loadStoneContents() {
		
		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return null;// TODO [content_loading] proper error handling!
		}
		
		final File dir = Environment.getExternalStorageDirectory();
		final File file = new File(new File(dir, "GenericMahjong"), "simple_contents.xml");// TODO [content_loading] parametrize
		
		Reader reader = null;
		
		try {
			reader = new FileReader(file);
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(reader);
			
			parser.nextTag();
			parser.require(XmlPullParser.START_TAG, "", "dictTileContents");
			
			final List<StoneContent> stoneContents = new ArrayList<StoneContent>();
			
			while(parser.nextTag() != XmlPullParser.END_TAG) {
				parser.require(XmlPullParser.START_TAG, "", "dictTileContent");
				
				final String text = parser.getAttributeValue("", "text");
				if(text == null) {
					throw new XmlPullParserException("dictTileContent does not have attribute text! " + parser.getPositionDescription());
				}
				
				final List<String> associations = new ArrayList<String>();
				while(parser.nextTag() != XmlPullParser.END_TAG) {
					parser.require(XmlPullParser.START_TAG, "", "association");
					associations.add(parser.nextText());
					parser.require(XmlPullParser.END_TAG, "", "association");
				}
				
				stoneContents.add(new DictStoneContent(text, associations));
				
				parser.require(XmlPullParser.END_TAG, "", "dictTileContent");
			}
			
			parser.require(XmlPullParser.END_TAG, "", "dictTileContents");
			
			return stoneContents;
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return null;// TODO [content_loading] proper error handling!
		} catch(NumberFormatException e) {
			e.printStackTrace();
			return null;// TODO [content_loading] proper error handling!
		} catch(XmlPullParserException e) {
			e.printStackTrace();
			return null;// TODO [content_loading] proper error handling!
		} catch(IOException e) {
			e.printStackTrace();
			return null;// TODO [content_loading] proper error handling!
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	
}
