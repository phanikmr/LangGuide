package com.bitsblender.langguide;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;

	private Camera mCamera; 
 
	public CameraPreview(Context context, Camera cam) {
        super(context);
        mCamera = cam; 
 
        // Install a SurfaceHolder.Callback so we get notified when the 
        // underlying surface is created and destroyed. 
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0 
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    } 
 

	public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview. 
        try { 
        	 System.out.println("Preview Created");
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview(); 
           
        } catch (IOException e) {
            Log.d("Camera preview Error", "Error setting camera preview: " + e.getMessage());
        } 
    } 
 
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity. 
    	mCamera.stopPreview();
    	mCamera.release();
    	System.out.println("Preview Destroyed");
    } 
 
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here. 
        // Make sure to stop the preview before resizing or reformatting it. 
 
        if (mHolder.getSurface() == null){
          // preview surface does not exist 
          return; 
        } 
 
        // stop preview before making changes 
        try { 
            mCamera.stopPreview(); 
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview 
        } 
 
        // set preview size and make any resize, rotate or 
        // reformatting changes here 
 
        // start preview with new settings 
        try { 
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview(); 
            System.out.println("Preview Changed");
 
        } catch (Exception e){
            Log.d("Camera preview Error", "Error starting camera preview: " + e.getMessage());
        } 
    } 
} 