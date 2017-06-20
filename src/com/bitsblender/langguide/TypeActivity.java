package com.bitsblender.langguide;

import java.io.File;
import java.util.Comparator;

import com.memetix.mst.language.Language;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TypeActivity extends Activity
		implements OnClickListener, Comparator<String>, OnItemSelectedListener, TextWatcher, TranslationListner {

	Spinner OCRLangSpinner, BingLangSpinner;
	ArrayAdapter<String> OCRLangAdapter, BingLangAdapter;
	ImageButton cameraBtn, speakBtn;
	EditText translatingText;
	TextView translatedText;
	ProgressBar translationProgressBar;
	BingTanslator bingTanslator;
	private static final String BING_CLIEND_ID = "TranslatorTestAndroidApp";
	private static final String BING_CLIENT_PASS_KEY = "FzwETOKtPTW8pNIHlpnp4I/2yZvWSMyK5mmOOwEw4eA=";
	ConnectivityManager connectivityManager;
	Editor editor;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initPrefs();
		createDataDir();
		setContentView(R.layout.activity_type);
		initView();
	}

	private void initPrefs() {
		this.prefs = getApplicationContext().getSharedPreferences("LangPrefs", MODE_PRIVATE);
		this.editor = prefs.edit();
	}

	private void createDataDir() {
		File dataFolder = new File(Environment.getExternalStorageDirectory() + "/LangGuide");
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
	}

	private void initView() {
		this.OCRLangSpinner = (Spinner) findViewById(R.id.from_lang);
		this.BingLangSpinner = (Spinner) findViewById(R.id.to_lang);
		this.translatedText = (TextView) findViewById(R.id.translated_text);
		this.translatingText = (EditText) findViewById(R.id.translating_text);
		this.cameraBtn = (ImageButton) findViewById(R.id.camera_btn);
		this.translationProgressBar = (ProgressBar) findViewById(R.id.translation_progressbar);
		this.translationProgressBar.setVisibility(View.GONE);
		//this.speakBtn = (ImageButton) findViewById(R.id.speak_btn);
		this.cameraBtn.setOnClickListener(TypeActivity.this);
		//this.speakBtn.setOnClickListener(TypeActivity.this);
		this.BingLangAdapter = new ArrayAdapter<>(TypeActivity.this, android.R.layout.simple_list_item_1);
		this.BingLangAdapter.addAll(Languages.getBingLangsStrings());
		this.BingLangAdapter.sort(TypeActivity.this);
		this.OCRLangAdapter = new ArrayAdapter<>(TypeActivity.this, android.R.layout.simple_list_item_1);
		this.OCRLangAdapter.addAll(Languages.getOCRLangsStrings());
		this.OCRLangAdapter.sort(TypeActivity.this);
		this.BingLangSpinner.setAdapter(BingLangAdapter);
		this.OCRLangSpinner.setAdapter(OCRLangAdapter);
		this.translatingText.addTextChangedListener(this);
		this.BingLangSpinner.setOnItemSelectedListener(this);
		this.OCRLangSpinner.setOnItemSelectedListener(this);
		this.BingLangSpinner.setSelection(this.prefs.getInt("ToLang", 0));
		this.OCRLangSpinner.setSelection(this.prefs.getInt("FromLang", 0));
		Language lang = (Language) Languages.BingLangs.get(this.BingLangSpinner.getSelectedItem().toString());
		this.bingTanslator = new BingTanslator(BING_CLIEND_ID, BING_CLIENT_PASS_KEY, lang);
		this.bingTanslator.setOnTranslationListner(this);
		this.connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.type, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		int id = v.getId();
		if (id == R.id.camera_btn) {
			intent = new Intent(TypeActivity.this, CameraActivity.class);
			startActivityForResult(intent, Codes.REQUEST_LOAD_IMAGE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Codes.RESULT_IMAGE_FILE) {
			Intent intent = new Intent(TypeActivity.this, OCRActivity.class);
			intent.putExtra("TESSDATA_PATH",
					Environment.getExternalStorageDirectory().getAbsolutePath() + "/LangGuide");
			intent.putExtra("IMAGE_FILENAME", data.getStringExtra("IMAGE_FILE"));
			intent.putExtra("FROM_LANG", OCRLangAdapter.getItem(OCRLangSpinner.getSelectedItemPosition()));
			this.startActivityForResult(intent, Codes.REQUEST_OCR_STRING);

		}
		if (resultCode == Codes.RESULT_CAMERA_NOT_FOUND) {
			Toast.makeText(TypeActivity.this, "Camera Not Found", Toast.LENGTH_LONG).show();
		}
		if (resultCode == Codes.RESULT_LOAD_IMAGE_CANCELLED) {
			Toast.makeText(TypeActivity.this, "Operation cancelled", Toast.LENGTH_SHORT).show();
		}
		if (resultCode == Codes.RESULT_OCR_STRING) {
			this.translatingText.setText(data.getStringExtra("DETECTED_STRING"));
		}
	}

	@Override
	public int compare(String lhs, String rhs) {
		return lhs.compareTo(rhs);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		Spinner spinner = (Spinner) parent;
		int index = spinner.getId();
		if (index == R.id.from_lang) {
			this.editor.putInt("FromLang", position);
			this.editor.commit();
		} else if (index == R.id.to_lang) {
			this.editor.putInt("ToLang", position);
			this.editor.commit();
			this.bingTanslator.setDefaultLanguage(
					(Language) Languages.BingLangs.get(this.BingLangSpinner.getSelectedItem().toString()));
			String text = this.translatingText.getText().toString();
			if (text.length() > 0) {
				this.bingTanslator.translate(text);
			}
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		this.translationProgressBar.setVisibility(View.VISIBLE);
		NetworkInfo activeNetwork = this.connectivityManager.getActiveNetworkInfo();
		final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		Thread currentThread = new Thread(new Runnable() {

			@Override
			public void run() {
				String text = TypeActivity.this.translatingText.getText().toString();
				if (text.length() > 0 && isConnected) {
					TypeActivity.this.bingTanslator.translate(text);
				}
			}
		});
		currentThread.start();
		if (!isConnected) {
			TypeActivity.this.translationProgressBar.setVisibility(View.GONE);
			this.translatedText.setText("No Network Connection");
		}
	}

	@Override
	public void onTranslationCompleted(final String translatedString) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (translatedString != null) {
					TypeActivity.this.translatingText.invalidate();
					TypeActivity.this.translatedText.setText(translatedString);
					TypeActivity.this.translationProgressBar.setVisibility(View.GONE);
				}

			}
		});

	}

	@Override
	public void onTranslationFailed(final String causeOfFailure) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (causeOfFailure != null) {
					TypeActivity.this.translatingText.invalidate();
					TypeActivity.this.translatedText.setText(causeOfFailure);
					TypeActivity.this.translationProgressBar.setVisibility(View.GONE);
				}

			}
		});
	}

	@Override
	public void onTransaltionStarted() {
		// TODO Auto-generated method stub

	}
}
