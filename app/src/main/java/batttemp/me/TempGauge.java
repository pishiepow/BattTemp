package batttemp.me;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public final class TempGauge extends View {

	private static final String TAG = TempGauge.class.getSimpleName();
	
	// drawing tools
	private RectF rimRect;
	private Paint rimPaint;

	private RectF faceRect;
	private Bitmap faceTexture;
	private Paint facePaint;

	private Paint scalePaint;
	private RectF scaleRect;
	
	private Paint valuePaint;
	
	private Paint titlePaint;	
	private Path titlePath;
	
	private Paint handPaint;
	private Path handPath;
	private Paint handScrewPaint;
	
	private Paint backgroundPaint; 
    private final float magnifier = 100f;		// Lollipop Hack

	// end drawing tools
	
	private Bitmap background; // holds the cached static part
	
	// title
	
	private int iTitleColor = 0xff2288e7; // Blue
	private String strTitle = "";
	
	// value
	
	private float gValue = 0;
	
	// scale configuration												- Default Settings. Should be Changed at ReInit.
	private int nickSpacing = 75;
	private float degreesPerNick = (360.0f / nickSpacing);	
	private int topGaugeNumber = 50; 						// the one in the top centre (12 o'clock)
	private int minNumber = 0;
	private int maxNumber = 100;
	private int majNickBase = 10;
	private int minNickBase = 10;
	
	// hand dynamics -- all are angular expressed in numbers
	private boolean handInitialized = true;
	private float handPosition = minNumber;
	
	
	public TempGauge(Context context) {
		super(context);
		init();
	}

	public TempGauge(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TempGauge(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		initDrawingTools();
	}

	private String getTitle() {
		return strTitle;
	}

	private void initDrawingTools() {
		rimRect = new RectF(0.05f, 0.05f, 0.95f, 0.95f);

		// the linear gradient is a bit skewed for realism
		rimPaint = new Paint();
		rimPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		rimPaint.setShader(new LinearGradient(0.40f, 0.0f, 0.60f, 1.0f, 
										   Color.rgb(0xf0, 0xf5, 0xf0),
										   Color.rgb(0x30, 0x31, 0x30),
										   Shader.TileMode.CLAMP));		

		float rimSize = 0.02f;
		faceRect = new RectF();
		faceRect.set(rimRect.left + rimSize, rimRect.top + rimSize, 
			     rimRect.right - rimSize, rimRect.bottom - rimSize);		

		faceTexture = BitmapFactory.decodeResource(getContext().getResources(), 
				   R.drawable.plastic);
		BitmapShader paperShader = new BitmapShader(faceTexture, 
												    Shader.TileMode.MIRROR, 
												    Shader.TileMode.MIRROR);
		Matrix paperMatrix = new Matrix();
		facePaint = new Paint();
		facePaint.setFilterBitmap(true);
		paperMatrix.setScale(1.0f / faceTexture.getWidth(), 
							 1.0f / faceTexture.getHeight());
		paperShader.setLocalMatrix(paperMatrix);
		facePaint.setStyle(Paint.Style.FILL);
		facePaint.setShader(paperShader);

		scalePaint = new Paint();
		scalePaint.setStyle(Paint.Style.STROKE);
		scalePaint.setColor(0xcfffffff);
		scalePaint.setStrokeWidth(0.005f);
		scalePaint.setAntiAlias(true);
		
		scalePaint.setTextSize(0.06f * magnifier);
		scalePaint.setTypeface(Typeface.DEFAULT);
		scalePaint.setLinearText(true);
		scalePaint.setTextScaleX(1f);
		scalePaint.setTextAlign(Paint.Align.CENTER);		
		
		float scalePosition = 0.10f;
		scaleRect = new RectF();
		scaleRect.set(faceRect.left + scalePosition, faceRect.top + scalePosition,
					  faceRect.right - scalePosition, faceRect.bottom - scalePosition);

		titlePaint = new Paint();
		titlePaint.setColor(0xffffffff);				// White
		titlePaint.setAntiAlias(true);
		titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
		titlePaint.setLinearText(true);
		titlePaint.setTextAlign(Paint.Align.CENTER);
		titlePaint.setTextSize(0.1f * magnifier);
		titlePaint.setTextScaleX(0.75f);

		titlePath = new Path();
		titlePath.addArc(new RectF(0.20f * magnifier, 0.20f * magnifier, 0.80f * magnifier, 0.80f * magnifier), -180.0f, -180.0f);

		valuePaint = new Paint();
		valuePaint.setColor(0xffffffff);				// White
		valuePaint.setAntiAlias(true);
		valuePaint.setLinearText(true);
		valuePaint.setTextAlign(Paint.Align.CENTER);
		valuePaint.setTextSize(0.1f * magnifier);
		valuePaint.setTextScaleX(1f);
		
    	handPaint = new Paint();
		handPaint.setAntiAlias(true);
		handPaint.setColor(0xffb71100);		
		handPaint.setShadowLayer(0.01f * magnifier, -0.005f * magnifier, -0.005f * magnifier, 0x7f000000);
		handPaint.setStyle(Paint.Style.FILL);	
		
		handPath = new Path();
		handPath.moveTo(0.5f * magnifier, (0.5f + 0.2f) * magnifier);
		handPath.lineTo((0.5f - 0.010f) * magnifier, (0.5f + 0.2f - 0.007f) * magnifier);
		handPath.lineTo((0.5f - 0.002f) * magnifier, (0.5f - 0.32f) * magnifier);
		handPath.lineTo((0.5f + 0.002f) * magnifier, (0.5f - 0.32f) * magnifier);
		handPath.lineTo((0.5f + 0.010f) * magnifier, (0.5f + 0.2f - 0.007f) * magnifier);
		handPath.lineTo(0.5f * magnifier, (0.5f + 0.2f) * magnifier);
		handPath.addCircle(0.5f * magnifier, 0.5f * magnifier, 0.025f * magnifier, Path.Direction.CW);
		
		handScrewPaint = new Paint();
		handScrewPaint.setAntiAlias(true);
		handScrewPaint.setColor(0xff493f3c);
		handScrewPaint.setStyle(Paint.Style.FILL);
		
		backgroundPaint = new Paint();
		backgroundPaint.setFilterBitmap(true);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);
		
		int chosenDimension = Math.min(chosenWidth, chosenHeight);
		
		setMeasuredDimension(chosenDimension, chosenDimension);
	}
	
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		} 
	}
	
	// in case there is no size specified
	private int getPreferredSize() {
		return 300;
	}

	private void drawRim(Canvas canvas) {
		// first, draw the metallic body
		canvas.drawOval(rimRect, rimPaint);
	}
	
	private void drawFace(Canvas canvas) {		
		canvas.drawOval(faceRect, facePaint);
		// draw the inner rim circle
	}

	private void drawScale(Canvas canvas) {
	//	canvas.save(Canvas.MATRIX_SAVE_FLAG);
		for (int i = 0; i < nickSpacing; ++i) {
			float y1 = scaleRect.top;
			float y2 = y1 - 0.020f;
			
			if (i % minNickBase == 0) {								// Every TEN draw Nick and Value.
				int value = nickToValue(i);
				
				if (value >= minNumber && value <= maxNumber) {
					canvas.drawLine(0.5f, y1, 0.5f, y2, scalePaint);
					if (i % majNickBase == 0) {
						String valueString = Integer.toString(value);
						
						// Get Old Stroke Width
						float fOldStrokeWidth = scalePaint.getStrokeWidth();
						
					    // Scale the canvas
					    canvas.save();
					    canvas.scale(1f / magnifier, 1f / magnifier);		
						
					    // Scale Stroke Width
					    scalePaint.setStrokeWidth (fOldStrokeWidth * magnifier);
					    
					    // draw the value						
						canvas.drawText(valueString, 0.5f * magnifier, (y2 - 0.015f) * magnifier, scalePaint);
						
					    // bring everything back to normal
						scalePaint.setStrokeWidth(fOldStrokeWidth);
					    canvas.restore();						
					}
				}
			}
			
			canvas.rotate(degreesPerNick, 0.5f, 0.5f);
		}
		canvas.restore();		
	}
	
	private int nickToValue(int nick) {
		int rawDegree = ((nick < nickSpacing / 2) ? nick : (nick - nickSpacing));
		int shiftedDegree = rawDegree + topGaugeNumber;
		return shiftedDegree;
	}
	
	private float valueToAngle(float degree) {
		return (degree - topGaugeNumber) * degreesPerNick;
	}
	
	private void drawTitle(Canvas canvas) {
		String title = getTitle();
		
	    // do the drawing of the text
	    canvas.drawTextOnPath(title, titlePath, 0.0f, 0.0f, titlePaint);
	}
	
	private void drawHand(Canvas canvas) {
		if (handInitialized) {
			float handAngle = valueToAngle(handPosition);
			//canvas.save(Canvas.MATRIX_SAVE_FLAG);
			canvas.rotate(handAngle, 0.5f * magnifier, 0.5f * magnifier);
			canvas.drawPath(handPath, handPaint);
			canvas.restore();
			
			canvas.drawCircle(0.5f * magnifier, 0.5f * magnifier, 0.01f * magnifier, handScrewPaint);
		}
	}

	private void drawValue(Canvas canvas) {
		String sValue;

		if (gValue == 999) {
			sValue = "E";
		} else {
				sValue = String.valueOf(gValue);
		}

	    // draw the value
		canvas.drawText(sValue, 0.5f * magnifier, 0.65f * magnifier, valuePaint);
	}
	
	private void drawBackground(Canvas canvas) {
		if (background == null) {
			Log.w(TAG, "Background not created");
		} else {
			canvas.drawBitmap(background, 0, 0, backgroundPaint);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		drawBackground(canvas);
		
		//canvas.save(Canvas.MATRIX_SAVE_FLAG);
		
		// Setup Scaling...
		float scale = (float) getWidth();
		canvas.scale(scale / magnifier, scale / magnifier);
		
	    // Scale the canvas
		drawHand(canvas);
		drawValue(canvas);
		drawTitle(canvas);

		canvas.restore();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		regenerateBackground();
	}
	
	private void regenerateBackground() {
		// free the old bitmap
		if (background != null) {
			background.recycle();
		}
		
		background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas backgroundCanvas = new Canvas(background);
		float scale = (float) getWidth();		
		backgroundCanvas.scale(scale, scale);
		
		drawRim(backgroundCanvas);
		drawFace(backgroundCanvas);
		drawScale(backgroundCanvas);
		setHandTarget(gValue);
	}

	public void setValue(float value) {
		gValue = value;
		setHandTarget(gValue);			
	}
	
	public void setValue(float value, int color) {
		valuePaint.setColor(color);
		setValue(value);
	}
	
	public void setTitle(String title) {
		strTitle = title;
	}
	
	public void setTitle(String title, int color) {
		iTitleColor = color;
		titlePaint.setColor(iTitleColor);		
		setTitle(title);
	}	
	
	public void ReInit(int _nickSpacing, int _topGaugeNumber, int _minNumber, int _maxNumber, int _majNickBase, int _minNickBase) {
		
		/*	nickSpacing is the space between each "1" imaginary 'nick'. This just needs to be guessed.
		 *  topGaugeNumber is the number at the TOP of the gauge. If you have a gauge from 0-150, best putting 70 or 80 at the top.
		 *  minNumber is the minimum value.
		 *  maxNumber is the maximum value.
		 *  nickBase is how often a nick should be drawn. If set to 10, there will be a nick for every 10 (0, 10, 20, 30, etc).
		 * 
		 *  NOTE: nickBase needs to be a multiple of nickSpacing!
		 *  
		 */
		
		if (maxNumber <= minNumber) {
			return;
		}
		
		nickSpacing = _nickSpacing;
		topGaugeNumber = _topGaugeNumber;
		minNumber = _minNumber;
		maxNumber = _maxNumber;
		majNickBase = _majNickBase;
		minNickBase = _minNickBase;
		
		if (minNickBase == 0) {
			minNickBase = majNickBase;
		}
		
		degreesPerNick = (360.0f / nickSpacing);
		
		init();
		setValue(minNumber);
	}
	
	private void setHandTarget(float value) {
		if (value < minNumber) {
			value = minNumber;
		} else if (value > maxNumber) {
			value = maxNumber;
		}
		handPosition = Math.round(value);
		handInitialized = true;
		invalidate();
	}
}
