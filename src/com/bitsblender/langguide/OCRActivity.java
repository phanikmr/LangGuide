package com.bitsblender.langguide;

import java.io.File;
import java.util.ArrayList;
import com.googlecode.leptonica.android.Pixa;

import com.googlecode.tesseract.android.TessBaseAPI;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ClickableViewAccessibility")
public class OCRActivity extends Activity implements OnTouchListener, OnClickListener {

	TessBaseAPI tesseractAPI;
	ImageView ocrImageView, buttonImageView;
	EditText ocrEditTextView;
	Button endOCRActivityBtn, selectAllWordsBtn;
	TextView confidenceTextView;
	Bitmap choosedImage, rectBitmap;
	Paint rectPaintSelected, rectPaintUnSelected;
	Canvas buttonViewCanvas;
	ProgressDialog ocrExecutionProgressDialog;
	ArrayList<Rect> wordRects;
	ArrayList<Integer> selectedWords;
	boolean[] wordsStatus;
	RectDraw rectDraw;
	String[] detectedWords;
	int[] wordConfidences;
	OCRRecognizer ocrTextExtractor;
	Rect buttonViewCood;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		setContentView(R.layout.activity_ocr);
		initView();
		this.ocrExecutionProgressDialog.show();
		OCRActivity.this.choosedImage = BitmapFactory.decodeFile(intent.getStringExtra("IMAGE_FILENAME"));
		OCRActivity.this.ocrImageView.setImageBitmap(OCRActivity.this.choosedImage);
		this.ocrTextExtractor = new OCRRecognizer();
		this.rectDraw = new RectDraw();
		this.ocrTextExtractor.execute(new String[] { intent.getStringExtra("TESSDATA_PATH"),
				Languages.OCRLangs.get(intent.getStringExtra("FROM_LANG")), intent.getStringExtra("IMAGE_FILENAME") });
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		this.ocrImageView = (ImageView) findViewById(R.id.ocr_imageview);
		this.ocrEditTextView = (EditText) findViewById(R.id.ocr_edittextview);
		this.buttonImageView = (ImageView) findViewById(R.id.button_imageview);
		this.endOCRActivityBtn = (Button) findViewById(R.id.end_ocr_activity_btn);
		this.selectAllWordsBtn = (Button) findViewById(R.id.select_all_btn);
		this.confidenceTextView = (TextView) findViewById(R.id.confidence_textview);
		this.endOCRActivityBtn.setOnClickListener(OCRActivity.this);
		this.selectAllWordsBtn.setOnClickListener(OCRActivity.this);
		this.buttonImageView.setOnTouchListener(OCRActivity.this);
		this.rectPaintUnSelected = new Paint();
		this.rectPaintUnSelected.setColor(Color.BLACK);
		this.rectPaintUnSelected.setStyle(Paint.Style.STROKE);
		this.rectPaintUnSelected.setStrokeWidth(3f);
		this.rectPaintSelected = new Paint();
		this.rectPaintSelected.setColor(Color.GREEN);
		this.rectPaintSelected.setStyle(Paint.Style.STROKE);
		this.rectPaintSelected.setStrokeWidth(3f);
		this.ocrExecutionProgressDialog = new ProgressDialog(OCRActivity.this);
		this.ocrExecutionProgressDialog.setCancelable(false);
		this.ocrExecutionProgressDialog.setTitle("Please wait");
		this.ocrExecutionProgressDialog.setMessage("Scanning the Image");
		this.ocrExecutionProgressDialog.setButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				OCRActivity.this.setResult(Codes.RESULT_LOAD_IMAGE_CANCELLED);
				dialog.dismiss();
				OCRActivity.this.finish();
			}
		});
	}

	private class OCRRecognizer extends AsyncTask<String, Void, String> {

		String resultString = null;

		@Override
		protected String doInBackground(String... params) {
			OCRActivity.this.tesseractAPI = new TessBaseAPI();
			OCRActivity.this.tesseractAPI.init(params[0], params[1]);
			OCRActivity.this.tesseractAPI.setImage(new File(params[2]));
			this.resultString = OCRActivity.this.tesseractAPI.getUTF8Text();
			// tesseractAPT.end();
			OCRActivity.this.detectedWords = this.resultString.split("\\s+");
			OCRActivity.this.wordConfidences = OCRActivity.this.tesseractAPI.wordConfidences();
			return resultString;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String result) {
			if (!isCancelled()) {
				Pixa pixa = OCRActivity.this.tesseractAPI.getWords();

				OCRActivity.this.rectBitmap = Bitmap.createBitmap(OCRActivity.this.choosedImage.getWidth(),
						OCRActivity.this.choosedImage.getHeight(), Config.ARGB_8888);

				OCRActivity.this.buttonViewCanvas = new Canvas(OCRActivity.this.rectBitmap);
				OCRActivity.this.wordRects = pixa.getBoxRects();
				for (Rect rect : OCRActivity.this.wordRects) {
					OCRActivity.this.buttonViewCanvas.drawRect(rect, OCRActivity.this.rectPaintUnSelected);
				}
				OCRActivity.this.wordsStatus = new boolean[OCRActivity.this.wordRects.size()];
				OCRActivity.this.buttonImageView.setImageDrawable(new BitmapDrawable(OCRActivity.this.rectBitmap));
				OCRActivity.this.buttonViewCood = OCRActivity
						.getBitmapPositionInsideImageView(OCRActivity.this.buttonImageView);
				OCRActivity.this.ocrExecutionProgressDialog.dismiss();
			}
			super.onPostExecute(result);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		int currentX, currentY;

		if (action == MotionEvent.ACTION_DOWN) {
			currentX = (int) event.getX();
			currentY = (int) event.getY();
			if (this.buttonViewCood.contains(currentX, currentY)) {
				this.rectDraw = new RectDraw();

				int mappedX = currentX - this.buttonViewCood.left;
				int mappedY = currentY;
				this.rectDraw.execute(mappedX, mappedY);
			}
		} else if (action == MotionEvent.ACTION_MOVE) {
		}
		return true;
	}

	private class RectDraw extends AsyncTask<Integer, Void, Void> {

		String result = "";

		@Override
		protected Void doInBackground(Integer... coordinates) {
			float hRatio = (float) OCRActivity.this.rectBitmap.getHeight()
					/ (float) OCRActivity.this.buttonViewCood.height();
			float wRatio = (float) OCRActivity.this.rectBitmap.getWidth()
					/ (float) OCRActivity.this.buttonViewCood.width();
			coordinates[0] = (int) (wRatio * (float) coordinates[0]);
			coordinates[1] = (int) (hRatio * (float) coordinates[1]);
			for (int i = 0; i < OCRActivity.this.wordRects.size(); i++) {
				Rect rect = OCRActivity.this.wordRects.get(i);
				if (rect.contains(coordinates[0], coordinates[1])) {
					if (!OCRActivity.this.wordsStatus[i]) {
						OCRActivity.this.wordsStatus[i] = true;
						OCRActivity.this.buttonViewCanvas.drawRect(rect, OCRActivity.this.rectPaintSelected);
					} else {
						OCRActivity.this.wordsStatus[i] = false;
						OCRActivity.this.buttonViewCanvas.drawRect(rect, OCRActivity.this.rectPaintUnSelected);
					}
					break;
				}
			}

			for (int i = 0; i < OCRActivity.this.wordsStatus.length; i++) {
				if (OCRActivity.this.wordsStatus[i])
					this.result += OCRActivity.this.detectedWords[i] + " ";
			}

			return null;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Void result) {
			OCRActivity.this.buttonImageView.setImageDrawable(new BitmapDrawable(OCRActivity.this.rectBitmap));
			OCRActivity.this.ocrEditTextView.setText(this.result);
			OCRActivity.this.setCurrentWordConfidence();
			super.onPostExecute(result);
		}

	}

	public static Rect getBitmapPositionInsideImageView(ImageView imageView) {

		if (imageView == null || imageView.getDrawable() == null)
			return null;

		// Get image dimensions
		// Get image matrix values and place them in an array
		float[] f = new float[9];
		imageView.getImageMatrix().getValues(f);

		// Extract the scale values using the constants (if aspect ratio
		// maintained, scaleX == scaleY)
		final float scaleX = f[Matrix.MSCALE_X];
		final float scaleY = f[Matrix.MSCALE_Y];

		// Get the drawable (could also get the bitmap behind the drawable and
		// getWidth/getHeight)
		final Drawable d = imageView.getDrawable();
		final int origW = d.getIntrinsicWidth();
		final int origH = d.getIntrinsicHeight();

		// Calculate the actual dimensions
		final int actW = Math.round(origW * scaleX);
		final int actH = Math.round(origH * scaleY);

		// Get image position
		// We assume that the image is centered into ImageView
		int imgViewW = imageView.getWidth();
		int imgViewH = imageView.getHeight();

		int top = (int) (imgViewH - actH) / 2;
		int left = (int) (imgViewW - actW) / 2;
		return new Rect(left, top, left + actW, top + actH);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.end_ocr_activity_btn) {
			this.tesseractAPI.end();
			Intent intent = new Intent();
			System.out.println(this.ocrEditTextView.getText().toString());
			intent.putExtra("DETECTED_STRING", this.ocrEditTextView.getText().toString());
			setResult(Codes.RESULT_OCR_STRING, intent);
			this.finish();
		} else if (id == R.id.select_all_btn) {
			this.ocrEditTextView.setText(this.tesseractAPI.getUTF8Text());
			for (int i = 0; i < this.wordRects.size(); i++) {
				Rect rect = this.wordRects.get(i);
				this.buttonViewCanvas.drawRect(rect, this.rectPaintSelected);
				this.wordsStatus[i] = true;
			}
			this.buttonImageView.setImageDrawable(new BitmapDrawable(this.rectBitmap));
			this.setCurrentWordConfidence();
		}
	}
	@Override
	public void onBackPressed() {
		this.tesseractAPI.end();
		Intent intent = new Intent();
		System.out.println(this.ocrEditTextView.getText().toString());
		intent.putExtra("DETECTED_STRING", this.ocrEditTextView.getText().toString());
		setResult(Codes.RESULT_OCR_STRING, intent);
		this.finish();
	}

	private void setCurrentWordConfidence() {
		int wordsCount = 0;
		int sum = 0;
		for (int i = 0; i < this.wordConfidences.length; i++) {
			if (this.wordsStatus[i]) {
				wordsCount++;
				sum += this.wordConfidences[i];
			}

		}
		if (wordsCount > 0) {
			String confidence = Float.toString((float) sum / (float) wordsCount);
			this.confidenceTextView.setText("Detection mean Confidence of Correctness: " + confidence);
		} else {
			this.confidenceTextView.setText("");
		}
	}

}
