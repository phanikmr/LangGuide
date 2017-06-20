package com.bitsblender.langguide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ZoomControls;

@SuppressWarnings("deprecation")
public class CameraActivity extends Activity implements OnClickListener, PictureCallback {

	FrameLayout cameraPreviewLayout;
	ImageButton takeSnapBtn, pickImageBtn;
	Camera camera = null;
	CameraPreview cameraPreview;
	ZoomControls zoomControls;
	int currentZoomLevel,maxZoomLevel;
	Camera.Parameters params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		this.initView();
		this.initCamera();
		this.initCameraFeatures();
	}

	private void initCameraFeatures() {
		if (this.params.isZoomSupported()) {
			this.maxZoomLevel = params.getMaxZoom();
			this.currentZoomLevel = 0;
			this.zoomControls.setIsZoomInEnabled(true);
			this.zoomControls.setIsZoomOutEnabled(true);
			this.zoomControls.setOnZoomInClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(CameraActivity.this.currentZoomLevel<CameraActivity.this.maxZoomLevel){
						CameraActivity.this.currentZoomLevel++;
						CameraActivity.this.params.setZoom(CameraActivity.this.currentZoomLevel);
						CameraActivity.this.camera.setParameters(CameraActivity.this.params);
					}
				}
			});
			this.zoomControls.setOnZoomOutClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(CameraActivity.this.currentZoomLevel>0){
						CameraActivity.this.currentZoomLevel--;
						CameraActivity.this.params.setZoom(CameraActivity.this.currentZoomLevel);
						CameraActivity.this.camera.setParameters(CameraActivity.this.params);
					}
				}
			});	
		}
		else{
			this.zoomControls.setVisibility(View.GONE);
		}
		if(CameraActivity.this.params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
			CameraActivity.this.params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			CameraActivity.this.camera.setParameters(CameraActivity.this.params);
		}
	
	}

	private void initCamera() {
		try {
			this.cameraPreviewLayout = (FrameLayout) findViewById(R.id.camera_preview);
			this.camera = Camera.open(0);
			cameraPreview = new CameraPreview(CameraActivity.this, this.camera);
			cameraPreviewLayout.addView(cameraPreview);
			this.params = this.camera.getParameters();

		} catch (Exception e) {
			Intent intent = new Intent();
			intent.putExtra("MESSAGE", "No Camera Found");
			setResult(Codes.RESULT_CAMERA_NOT_FOUND, intent);
			System.err.print("No Camera Found");
			this.finish();
		}
	}

	@Override
	protected void onResume() {

		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void initView() {
		this.takeSnapBtn = (ImageButton) findViewById(R.id.take_snap_btn);
		this.pickImageBtn = (ImageButton) findViewById(R.id.image_browser_btn);
		this.takeSnapBtn.setOnClickListener(CameraActivity.this);
		this.pickImageBtn.setOnClickListener(CameraActivity.this);
		this.zoomControls = (ZoomControls) findViewById(R.id.zooncontrols);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		int id = v.getId();
		if (id == R.id.image_browser_btn) {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, Codes.REQUEST_LOAD_IMAGE);
		} else if (id == R.id.take_snap_btn) {
			this.camera.takePicture(null, null, CameraActivity.this);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		Intent intent;
		if (requestCode == Codes.REQUEST_LOAD_IMAGE && resultCode == RESULT_OK) {
			if (data != null) {
				Uri selectedImagePath = data.getData();
				intent = new Intent();
				intent.putExtra("IMAGE_FILE", this.getPath(selectedImagePath));
				CameraActivity.this.setResult(Codes.RESULT_IMAGE_FILE, intent);
				CameraActivity.this.finish();
			}
		}
		if (requestCode == Codes.REQUEST_LOAD_IMAGE && resultCode == RESULT_CANCELED) {
			this.setResult(Codes.RESULT_LOAD_IMAGE_CANCELLED);
			this.finish();
		}
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Toast.makeText(this, "Shot Taken", Toast.LENGTH_LONG).show();
		File pictureFile = new File(Environment.getExternalStorageDirectory() + "/LangGuide/temp.jpg");
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(data);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(pictureFile.toString());
		Intent intent = new Intent();
		intent.putExtra("IMAGE_FILE", pictureFile.getAbsolutePath());
		CameraActivity.this.setResult(Codes.RESULT_IMAGE_FILE, intent);
		CameraActivity.this.finish();
	}

	public String getPath(Uri uri) {
		String selectedImagePath;
		// 1:MEDIA GALLERY --- query from MediaStore.Images.Media.DATA
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = this.managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			selectedImagePath = cursor.getString(column_index);
		} else {
			selectedImagePath = null;
		}

		if (selectedImagePath == null) {
			// 2:OI FILE Manager --- call method: uri.getPath()
			selectedImagePath = uri.getPath();
		}
		return selectedImagePath;
	}

	@Override
	public void onBackPressed() {
		this.setResult(Codes.RESULT_LOAD_IMAGE_CANCELLED);
		this.finish();
	}

}
