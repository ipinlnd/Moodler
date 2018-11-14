package com.nlnd.moodler;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ShareMoodle extends AppCompatActivity
{
	private Uri file;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_moodle);

		if (savedInstanceState == null)
		{
			file = getIntent().getExtras().getParcelable("file");
		}

		if (file == null)
			return;
	}
}
