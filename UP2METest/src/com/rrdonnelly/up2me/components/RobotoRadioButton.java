package com.rrdonnelly.up2me.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class RobotoRadioButton extends RadioButton {
	public RobotoRadioButton(Context context) {
		super(context);
		init();
	}

	public RobotoRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RobotoRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		if (getContentDescription().toString().trim().equalsIgnoreCase("FontStyle1")) {
			Typeface tf1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Black.ttf");
			setTypeface(tf1);
			setTextSize(20);
		} else if (getContentDescription().toString().trim().equalsIgnoreCase("FontStyle2")) {
			Typeface tf2 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Bold.ttf");
			setTypeface(tf2);
			setTextSize(20);
		} else if (getContentDescription().toString().trim().equalsIgnoreCase("FontStyle3")) {
			Typeface tf1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
			setTypeface(tf1);
			setTextSize(16);
		} else if (getContentDescription().toString().trim().equalsIgnoreCase("FontStyle4")) {
			Typeface tf2 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
			setTypeface(tf2);
			setTextSize(16);
		} else if (getContentDescription().toString().trim().equalsIgnoreCase("FontStyle5")) {
			Typeface tf1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
			setTypeface(tf1);
			setTextSize(16);
		} else if (getContentDescription().toString().trim().equalsIgnoreCase("FontStyle6")) {
			Typeface tf2 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
			setTypeface(tf2);
			setTextSize(12);
		} else if (getContentDescription().toString().trim().equalsIgnoreCase("FontStyle7")) {
			Typeface tf1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Bold.ttf");
			setTypeface(tf1);
			setTextSize(28);
		} else if (getContentDescription().toString().trim().equalsIgnoreCase("FontStyle8")) {
			Typeface tf2 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
			setTypeface(tf2);
			setTextSize(20);
		} else if (getContentDescription().toString().trim().equalsIgnoreCase("FontStyle9")) {
			Typeface tf1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
			setTypeface(tf1);
			setTextSize(26);
		} 
	}
}
