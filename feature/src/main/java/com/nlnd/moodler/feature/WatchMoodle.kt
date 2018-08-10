package com.nlnd.moodler.feature

import android.app.Activity
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import processing.android.CompatUtils
import processing.android.PFragment
import processing.core.PApplet
import java.io.*

class WatchMoodle : AppCompatActivity()
{
	private var sketch : PApplet? = null
	private var loaded = false

	override fun onCreate(savedInstanceState: Bundle?)
	{
		var ownFile : Uri?
		super.onCreate(savedInstanceState)
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN)

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
		val newSong = (filesDir.toString() + "/tempSong.mp3")
		var line : String

		while (true)
		{
			line = dis.readLine()
			ar.add(line)
			if (line == "end")
				break
		}
		config = ar.toTypedArray()

		val b = ByteArray(dis.available())
		dos = DataOutputStream(FileOutputStream(newSong))
		dis.read(b)
		dos.write(b)

		musicFile = Uri.fromFile(File(newSong))
		loaded = true
	}

	override fun onDestroy()
	{
		super.onDestroy()
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
		var context : Activity? = null

		fun stopMusic()
		{
			mplayer!!.stop()
		}

		fun getCurrentPosition(): Int
		{
			return mplayer!!.currentPosition
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
