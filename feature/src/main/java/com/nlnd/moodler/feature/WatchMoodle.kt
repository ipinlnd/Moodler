package com.nlnd.moodler.feature

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.FrameLayout
import processing.android.CompatUtils
import processing.core.PApplet
import processing.android.PFragment
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.view.View
import java.io.*

class WatchMoodle : AppCompatActivity()
{
	private var sketch : PApplet? = null
	private var context : Context? = null
	private var loaded = false

	override fun onCreate(savedInstanceState: Bundle?)
	{
		var ownFile : Uri? = null
		super.onCreate(savedInstanceState)
		val frame = FrameLayout(this)
		context = this

		frame.id = CompatUtils.getUniqueViewId()
		setContentView(frame, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT))
		if (savedInstanceState == null)
		{
			file = intent.extras.getParcelable("file")
			render = intent.extras.getBoolean("render")
		}

		if (file == null)
			return

		ownFile = file
		loadFile(File(ownFile!!.path))

		while(!loaded)
			continue

		mplayer = MediaPlayer()
		mplayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)

		try
		{
			mplayer!!.setDataSource(this, musicFile)
			mplayer!!.prepare()
			duration = mplayer!!.duration
		}
		catch (e: IOException)
		{
			e.printStackTrace()
		}

		sketch = WatchMoodleSketch()
		val fragment = PFragment(sketch)
		fragment.setView(frame, this)
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
	{
		if (sketch != null)
		{
			sketch!!.onRequestPermissionsResult(
					requestCode, permissions, grantResults)
		}
	}

	override fun onNewIntent(intent: Intent)
	{
		if (sketch != null)
		{
			sketch!!.onNewIntent(intent)
		}
	}

	fun loadFile(f : File)
	{
		val ar = ArrayList<String>()
		val dis = DataInputStream(FileInputStream(f))
		val dos: DataOutputStream
		var newSong = (filesDir.toString() + "/tempSong.mp3")
		var line = ""

		var progressDialog = ProgressDialog(this,
				ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(false);
		progressDialog.setMessage("Loading...");
		progressDialog.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		while (true)
		{
			line = dis.readLine()
			ar.add(line)
			if (line == "end")
				break
		}
		config = ar.toTypedArray();

		val b = ByteArray(dis.available())
		dos = DataOutputStream(FileOutputStream(newSong));
		dis.read(b)
		dos.write(b)

		musicFile = Uri.fromFile(File(newSong))
		progressDialog.dismiss();
		loaded = true
	}

	companion object
	{
		var musicFile: Uri? = null
		var mplayer: MediaPlayer? = null
		var duration = 0
		var layout : View? = null
		var config: Array<String>? = null
		var file : Uri? = null
		var render : Boolean = false

		fun stopMusic()
		{
			mplayer!!.stop()
		}

		fun getCurrentPosition(): Int
		{
			return mplayer!!.getCurrentPosition()
		}

		fun pauseMusic()
		{
			mplayer!!.pause()
		}

		fun startMusic()
		{
			mplayer!!.start()
		}

		fun musicSeekTo(a: Int)
		{
			mplayer!!.seekTo(a)
		}

		fun mute()
		{
			mplayer!!.setVolume(0f, 0f)
		}
	}
}
