package edu.cmu.sv.mobisens.ui;

/*
Copyright 2011 jawsware international

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import android.app.Service;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public abstract class OverlayView extends RelativeLayout {

	protected WindowManager.LayoutParams layoutParams;
	protected int layoutResId;

	private boolean inside = false;

	public OverlayView(Service service, int layoutResId) {
		super(service);

		this.layoutResId = layoutResId;

		load();
	}


	public int getGravity() {
		// Override this to set a custom Gravity for the view.

		return Gravity.CENTER;
	}

	protected void setupLayoutParams() {
		// Override this to modify the initial LayoutParams. Be sure to call
		// super.setupLayoutParams() first.

		layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);

		layoutParams.gravity = getGravity();
				
	}

	protected void inflateView() {
		// Inflates the layout resource, sets up the LayoutParams and adds the
		// View to the WindowManager service.

		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(layoutResId, this);

		onInflateView();
		
	}

	protected void onInflateView() {
		// Override this to make calls to findViewById() to setup references to
		// the views that were inflated.
		// This is called automatically when the object is created right after
		// the resource is inflated.
	}

	public boolean isVisible() {
		// Override this method to control when the Overlay is visible without
		// destroying it.
		return true;
	}

	protected void reloadLayout() {
		unload();
		load();
	}
	
	public void refreshLayout() {
		// Call this to force the updating of the view's layout.

		removeAllViews();
		inflateView();

		refresh();
	}
	
	protected void addView() {
		setupLayoutParams();

		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).addView(this, layoutParams);
	}

	protected void load() {
		inflateView();
		addView();
		refresh();
	}

	protected void unload() {
		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).removeView(this);

		removeAllViews();
	}

	public void destory() {
		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).removeView(this);
	}

	public void makeActive() {
		// Call this make the overlay active and start receiving all touch events
		
		layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).updateViewLayout(this, layoutParams);
	}
	
	public void makeInactive() {
		layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

		wm.updateViewLayout(this, layoutParams);
	}

	public void refresh() {
		// Call this to update the contents of the Overlay.

		if (!isVisible()) {
			setVisibility(View.GONE);
		} else {
			setVisibility(View.VISIBLE);

			refreshViews();
		}
	}

	protected void refreshViews() {
		// Override this method to refresh the views inside of the Overlay. Only
		// called when Overlay is visible.
	}
	
	protected boolean showNotificationHidden() {
		// Override this to configure the notificaiton to remain even when the overlay is invisible.
		return true;
	}

	protected int getLeftOnScreen() {
		int[] location = new int[2];

		getLocationOnScreen(location);

		return location[0];
	}

	protected int getTopOnScreen() {
		int[] location = new int[2];

		getLocationOnScreen(location);

		return location[1];
	}

	
	protected boolean isInside(View view, int x, int y) {
		// Use this to test if the X, Y coordinates of the MotionEvent are
		// inside of the View specified.

		int[] location = new int[2];

		view.getLocationOnScreen(location);

		if (x >= location[0]) {
			if (x <= location[0] + view.getWidth()) {
				if (y >= location[1]) {
					if (y <= location[1] + view.getHeight()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	protected void onTouchEvent_Up(MotionEvent event, boolean inside) {

	}

	protected void onTouchEvent_Move(MotionEvent event, boolean inside) {

	}

	protected void onTouchEvent_PressInactive(MotionEvent event) {

	}

	protected void onTouchEvent_PressActive(MotionEvent event, boolean inside) {

	}

	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getActionMasked() == MotionEvent.ACTION_OUTSIDE && isInside(this, (int) event.getRawX(), (int) event.getRawY())) {

			onTouchEvent_PressInactive(event);
			
		} else if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {

			if (isInside(this, (int) event.getRawX(), (int) event.getRawY())) {
				inside = true;
			} else {
				inside = false;
			}

			onTouchEvent_PressActive(event, inside);

		} else if (event.getActionMasked() == MotionEvent.ACTION_UP) {

			onTouchEvent_Up(event, inside);

		} else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {

			onTouchEvent_Move(event, inside);

		}

		return super.onTouchEvent(event);

	}
}
