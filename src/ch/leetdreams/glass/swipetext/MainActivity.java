package ch.leetdreams.glass.swipetext;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.touchpad.Gesture;

public class MainActivity extends Activity {
	private GestureDetector mGestureDetector;
	private ArrayList<TextView> texts = new ArrayList<TextView>();
	private TextView selectedText = null;

	private void addCharacter(String s) {
		LinearLayout ll = (LinearLayout) findViewById(R.id.LinearLayoutInner);
		TextView lolo = new TextView(this);
		lolo.setTextColor(Color.WHITE);
		lolo.setTextSize(100);
		lolo.setText(s);
		lolo.setPadding(5, 0, 5, 0);
		lolo.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		ll.addView(lolo);
		texts.add(lolo);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// padding
		for (int i = 0; i < 4; i++) {
			addCharacter(" ");
		}

		/*
		// A-Z
		for (int i = 65; i <= 90; i++) {
			addCharacter("" + (char) i);
		}
		*/

		// a-z
		for (int i = 97; i <= 122; i++) {
			addCharacter("" + (char) i);
		}
		addCharacter(" ");

		// 0-9
		for (int i = 48; i <= 57; i++) {
			addCharacter("" + (char) i);
		}

		addCharacter(" ");
		
		// special chars
		for (int i = 33; i <= 47; i++) {
			addCharacter("" + (char) i);
		}

		// padding
		for (int i = 0; i < 10; i++) {
			addCharacter(" ");
		}

		mGestureDetector = createGestureDetector(this);

		setSelectedText(texts.get(3));
	}

	private void setSelectedText(TextView txt) {
		TextView old = selectedText;
		if (old != null) {
			old.setTextColor(Color.WHITE);
			old.setBackgroundColor(Color.BLACK);
		}
		txt.setTextColor(Color.RED);
		txt.setBackgroundColor(Color.DKGRAY);
		selectedText = txt;
	}

	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);
		// Create a base listener for generic gestures
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
			@Override
			public boolean onGesture(Gesture gesture) {
				TextView tvCurrent=(TextView)findViewById(R.id.currenttext);
				
				if (gesture == Gesture.TAP) {
					//single tap: add selected character
					tvCurrent.append(selectedText.getText());
					return true;
					
				} else if (gesture == Gesture.TWO_TAP) {
					// double finger tap: activate navigation
					
					String addr=tvCurrent.getText().toString();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("google.navigation:q="+addr));
					startActivity(intent);
					return true;
				} else if (gesture == Gesture.SWIPE_RIGHT) {
					// do something on right (forward) swipe
					return true;
				} else if (gesture == Gesture.SWIPE_UP) {
					// do something on left (backwards) swipe
					CharSequence content=tvCurrent.getText();
					if (content.length()==0){
						return true;
					}
					CharSequence backSpaced=content.subSequence(0, content.length()-1);
					tvCurrent.setText(backSpaced);
					return true;
				}
				return false;
			}
		});
		gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
			@Override
			public void onFingerCountChanged(int previousCount, int currentCount) {
				// do something on finger count changes
			}
		});
		gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
			@Override
			public boolean onScroll(float displacement, float delta,
					float velocity) {
				// do something on scrolling
				HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scrollView1);
				scroller.scrollBy((int) delta, 0);
				int firstVisible = getFirstVisibleTextIndex();
				setSelectedText(texts.get(firstVisible + 3));

				return false;

			}
		});
		return gestureDetector;

	}

	/**
	 * get the index of the left most textview which is currently visible
	 * @return
	 */
	public int getFirstVisibleTextIndex() {
		HorizontalScrollView scroller = (HorizontalScrollView) findViewById(R.id.scrollView1);
		Rect scrollBounds = new Rect();
		scroller.getHitRect(scrollBounds);
		for (int i = 0; i < texts.size(); i++) {
			TextView tv = texts.get(i);
			if (tv.getLocalVisibleRect(scrollBounds)) {
				return i;
			}

		}
		return texts.size();
	}

	/*
	 * Send generic motion events to the gesture detector
	 */
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (mGestureDetector != null) {
			return mGestureDetector.onMotionEvent(event);
		}
		return false;
	}

}
