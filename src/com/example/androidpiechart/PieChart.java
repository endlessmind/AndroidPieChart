package com.example.androidpiechart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class PieChart extends View {
	
	public ArrayList<PiePiece> pieces = new ArrayList<PiePiece>();
	private int Max;
	int color;
	private String TAG = "PieChart";
	private OnDrawCompleteListener mListener;
	private boolean CallDrawComp = true;
	private boolean mSortEnabled = false;
	private boolean mShadowEnabled = false;
	float center_x, center_y;

	public PieChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public PieChart(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public PieChart(Context context) {
		super(context);
	}
	
	public void setMax(int value) {
		Log.e(TAG, "" + value);
		this.Max = value;
		if (pieces.size() > 0) {
			CallDrawComp = false;
			invalidate();
		}
	}
	
	public int getMax() {
		return this.Max;
	}
	
	public boolean getIsSortingEnabled() {
		return mSortEnabled;
	}
	
	public void setIsSortingEnabled(boolean value) {
		mSortEnabled = value;
		invalidate();
	}
	
	public void setOnDrawCompleteListener(OnDrawCompleteListener listener) {
		this.mListener = listener;
	}
	
	public OnDrawCompleteListener getOnDrawCompleteListener() {
		return this.mListener;
	}
	
	public void addPiece(PiePiece value) {
		pieces.add(value);
		
		int maxValue = 0;
		for (PiePiece p : pieces) {
			maxValue += p.value;
		}
		setMax(maxValue);
		invalidate();
	}
	
	public ArrayList<PiePiece> getPiePieces() {
		return this.pieces;
	}
	
	public void clearPieces() {
		this.pieces.clear();
		//invalidate();
	}
	
	public boolean getHasShadow() {
		return mShadowEnabled;
	}
	
	public void setHasShadow(boolean value) {
		mShadowEnabled = value;
		invalidate();
	}
	
	public class PieceSorter implements Comparator<PiePiece> {
	    @Override
	    public int compare(PiePiece o1, PiePiece o2) {
	    	Integer first = o1.value;
	    	Integer second = o2.value;
	        return first.compareTo(second);
	    }
	}
	
	public void setSelectedPiece(int piece, boolean value) {
		if (piece > -1) {
			for (int i = 0; i < pieces.size(); i++) {
				if (i == piece) {
					pieces.get(i).isSelected = value;
				} else {
					pieces.get(i).isSelected = false;
				}
			}
		}
		CallDrawComp = false;
		invalidate();
	}
	
	private float colorCounter = 0;
	  private void cycleColor()
	  {
	    int r = (int)Math.floor(128*(Math.sin(colorCounter) + 1));
	    int g = (int)Math.floor(128*(Math.sin(colorCounter + 2) + 1));
	    int b = (int)Math.floor(128*(Math.sin(colorCounter + 4) + 1));
	     color =Color.argb(225, r, g, b);
	    colorCounter += 0.773;
	  }
	  
	  private void setShadowToPaint(Paint p) {
      	if (mShadowEnabled) {
    		if (Build.VERSION.SDK_INT >= 11) { //Disable hardware acceleration to enable drop shadow on Android 3.0+
    			this.setLayerType(View.LAYER_TYPE_SOFTWARE, p);
    		}
			p.setShadowLayer(3.0f, -3.0f, 2.0f, Color.BLACK);
    	}
	  }
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Sort the list if enabled
        if (mSortEnabled)
		Collections.sort(pieces, new PieceSorter());
		//Reset color counter, colors should not change when we invalidate
		colorCounter = 0;
		//Set center points
		center_x = getWidth()/2;
		center_y = getHeight()/2;
		//Pie-radius & Selected-radius
		float radius = 0;
		float radiusSelected = 0;
		//We don't want it to go outside the View.
		//So the the radius is base on what orientation is slimest.
		if (getHeight() < getWidth()) {
			radius = getHeight() / 2.4f;
			radiusSelected = getHeight() / 2.2f;
		} else {
			radius = getWidth() / 2.4f;
			radiusSelected = getWidth() / 2.2f;
		}
		//Set up our RectF (We use RectF instead if Rect because we are working with floats)
        final RectF oval = new RectF();
        final RectF ovalSelected = new RectF();
        oval.set(center_x - radius, center_y - radius, center_x + radius, center_y + radius);
        ovalSelected.set(center_x - radiusSelected, center_y - radiusSelected, center_x + radiusSelected, center_y + radiusSelected);
        //Rotate the canvas, we want the top to be Angle = 0;
        canvas.rotate(-90f, oval.centerX(), oval.centerY());

        //Counters
        float lastAng = 0;
        int count = 0;
        //Draw each piece and leave about 0.5 degree angle between each piece.
        for (PiePiece p : pieces) {
           
            count++;
            //Set up the first set of colors
            Paint paint = new Paint();
            if (p.Text == "Others") {
            	paint.setColor(Color.GRAY);
            	color = Color.GRAY;
            } else {
            	cycleColor();
            	paint.setColor(color);
            }
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Dip(8));
            paint.setAntiAlias(true);
            int index = pieces.indexOf(p);
            p.color = color;
            pieces.set(index, p);
            //Calculate angle
        	float angle = ((float)(360)/(float)Max) *p.value; 
        	//Leave space
        	angle -= 0.5f;

        	//Draw piece and shadow
        	setShadowToPaint(paint);
        	canvas.drawArc(oval, lastAng, angle, false, paint);
        	//More paint
    		paint = new Paint();
            paint.setColor(color);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(Dip(1));
            paint.setAntiAlias(true);
            paint.setTextSize(Dip(9));
            
            //Create the text that shown on the inside of each piece
            String Text = Math.round(((double)p.value / (double)Max) * (double)100) + "%";
            //Rotate the canvas back 90 degrees, we want our text to be horizontal
            canvas.rotate(90f, oval.centerX(), oval.centerY());
            //Calculate the point for the text, we want it in the middle of the piece
        	float newX =  (float) (oval.centerX() + (radius/1.22)* Math.cos(Math.toRadians((lastAng+ (angle /2)) -90)));
        	float newY = (float) (oval.centerY()  + (radius/1.22)* Math.sin(Math.toRadians((lastAng+ (angle /2)) -90)));
        	//Draw the text
        	canvas.drawText(Text, newX, newY, paint);
        	//Again, rotate the canvas so that Angle = 0 is at the top
        	canvas.rotate(-90f, oval.centerX(), oval.centerY());
        	//Is this piece selected?
        	if (p.isSelected) {
        		//Paint
        		paint = new Paint();
                paint.setColor(color);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(Dip(2));
                paint.setAntiAlias(true);
                //Draw selected indicator with shadow?
                setShadowToPaint(paint);
                canvas.drawArc(ovalSelected, lastAng, angle, false, paint);
                //More Paint
        		paint = new Paint();
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(Dip(1));
                paint.setAntiAlias(true);
                paint.setTextSize(Dip(14));
                //We are about to the info about the selected piece.
                //This info is shown in the middle if the PieChart
                //Rotate the canvas back 90 degrees, we want our text to be horizontal
                canvas.rotate(90f, oval.centerX(), oval.centerY());
                //Calculate the points for the top text and draw it
                float Text_x = oval.centerX() - (paint.measureText(p.Text) /2);
                float Text_y = (float) (oval.centerY() - (Dip(14) * 1));
                canvas.drawText(p.Text, Text_x, Text_y, paint);
                
                //Calculate the points for the middle text and draw it
                Text_x = oval.centerX() - (paint.measureText(p.value + "") /2);
                Text_y = (float) (oval.centerY() + (Dip(14) / 2));
                canvas.drawText(p.value + "", Text_x, Text_y, paint);

                //Calculate the points for the bottom text and draw it
                Text_x = oval.centerX() - (paint.measureText(Text) /2);
                Text_y = (float) (oval.centerY() + (Dip(14) * 2));
                canvas.drawText(Text, Text_x, Text_y, paint);
                //Again, rotate the canvas so that Angle = 0 is at the top
                canvas.rotate(-90f, oval.centerX(), oval.centerY());
                
        	}
        	
        	//Add the last angle to the total angle, so the next piece is draw after the current
        	lastAng += angle  + 0.5f;

        	
        	
        }
        //Call the DrawDone if we want to.
        //If we bound this call to update an other View, we might not want to update it when we are just
        //updating the selected piece
        if (mListener != null && CallDrawComp) {
        	mListener.OnDrawDone();
        }
        //Draws the total value at the bottom of the View, if we have any pieces at all
        if (pieces.size() > 0) {
        	Paint paint = new Paint();
        	paint.setColor(Color.BLACK);
        	paint.setStyle(Paint.Style.FILL);
        	paint.setStrokeWidth(Dip(1));
        	paint.setAntiAlias(true);
        	paint.setTextSize(Dip(12));
        	canvas.rotate(90f, oval.centerX(), oval.centerY());
        	canvas.drawText("Total: " + (Max ), 0, this.getHeight()- 5, paint);
        	canvas.rotate(-90f, oval.centerX() , oval.centerY());
        }
        CallDrawComp = true;

	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		//This is probably the most advanced piece.
		//We are going to calculate if the users is touching a piece
		//and if so, then we are goint to select that piece.
		if (event.getAction() == MotionEvent.ACTION_UP) {
			//We get our point, touch-point, center-point and a fixed point that we will
			//calculate the angle from.
			PointF point = new PointF(event.getX(),event.getY());
			PointF point2 = new PointF(center_x, center_y);
			PointF point3 = new PointF(center_x, 0);
			//We call our getAngle function, in our case, we can call in 2 ways:
			//getAngle(point3,point2, point)
			//or
			//getAngle(point,point2, point3)
			//Both will give us the desired values
			float angle = getAngle(point3,point2, point);
			//I was tired when i did this one, so the getAngle(PointF, PointF,PointF) only returns 0-180 degrees
			//Once it reaches 180 (bottom-center) it starts going backwards again.
			//A quick fix was to see if the users is touching on the on the left side of the center,
			//and take 180 degrees and add the missing ( 180 - angle )
			if (event.getX() < center_x) {
				angle = 180 + (180 - angle);
			}
			//We got our angles, now we need to check each piece if they fall within the users touch
			float PieceAngle = 0;
			for (PiePiece p : pieces) {
				//Calculate the swipe angle of the piece
				float newAngle = Math.round(((double)360/(double)Max) *p.value);
				//Check if the touch angle is between the start or the finish of the sweep
				if (angle > PieceAngle && angle < (PieceAngle + newAngle)){
					//If the touch angle is between the start or the finish if the sweep, then we select that piece
					setSelectedPiece(pieces.indexOf(p), true);
					//Piece found, leave the loop
					break;
				} else {
					//This piece was not touched, add the sweep to the counter so we know where the next piece starts
					PieceAngle += newAngle;
				}
				
			}
		}
		
		return true;
		
		
	}
	
	
	public float getAngle(PointF A, PointF B, PointF C) {
	    float AB = (float) Math.sqrt(Math.pow(B.x-A.x,2)+ Math.pow(B.y-A.y,2));    
	    float BC = (float) Math.sqrt(Math.pow(B.x-C.x,2)+ Math.pow(B.y-C.y,2)); 
	    float AC = (float) Math.sqrt(Math.pow(C.x-A.x,2)+ Math.pow(C.y-A.y,2));
	    float angle = (float) Math.toDegrees(Math.acos((BC*BC+AB*AB-AC*AC)/(2*BC*AB)));
	    //Just in case
	    if(angle < 0){
	    	angle += 360;
	    }
	    return angle;
	}
	
    private int Dip(int value) {
    	Resources r = getResources();
    	float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, r.getDisplayMetrics());
    	return (int) px;

    }

}
