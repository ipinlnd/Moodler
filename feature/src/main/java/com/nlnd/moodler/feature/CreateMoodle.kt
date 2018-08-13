package com.nlnd.moodler.feature

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.PopupWindow
import processing.android.CompatUtils
import processing.android.PFragment
import processing.core.PApplet
import java.io.*

class CreateMoodle : AppCompatActivity()
{
	private var sketch : PApplet? = null
	private var context : Context? = null
	private var loaded = false

	private fun makeList()
	{
		buttons = ArrayList()
		(buttons as ArrayList<MoodleMethod>).add(MoodleMethod("Snow", R.drawable.snow_method))
		(buttons as ArrayList<MoodleMethod>).add(MoodleMethod("Stick", R.drawable.sticks_method))
		(buttons as ArrayList<MoodleMethod>).add(MoodleMethod("Ring", R.drawable.rings_method))
		(buttons as ArrayList<MoodleMethod>).add(MoodleMethod("Firework", R.drawable.fireworks_method))
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		requestWindowFeature(Window.FEATURE_NO_TITLE)
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN)

		val ownFile : Uri?
		val frame = FrameLayout(this)
		context = this
		makeList()
		frame.id = CompatUtils.getUniqueViewId()
		setContentView(frame, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT))
		if (savedInstanceState == null)
		{
			file = intent.extras.getParcelable("file")
			continueMoodle = intent.extras.getBoolean("continue")
		}

		if (file == null)
			return

		if (continueMoodle)
		{
			ownFile = file
			loadFile(File(ownFile!!.path))
			while(!loaded)
				continue
		}
		else
			musicFile = file
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

		inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		layout = inflater!!.inflate(R.layout.toolbox_menu, null)
		val density = context!!.resources.displayMetrics
		pw = PopupWindow(layout, (density.widthPixels * .8).toInt(), (density.heightPixels * 0.7).toInt(), true)
		(layout!!.findViewById(R.id.toolbox_close_button) as Button).setOnClickListener { pw!!.dismiss() }
		pw!!.isOutsideTouchable = false

		val rv = layout!!.findViewById<RecyclerView>(R.id.toolbox_recycler_view)
		val layoutManager = LinearLayoutManager(context,
				LinearLayoutManager.HORIZONTAL, false)
		rv.layoutManager = layoutManager
		val dividerItemDecoration = DividerItemDecoration(rv.context,
				layoutManager.orientation)
		rv.addItemDecoration(dividerItemDecoration)
		val adapter = ToolsRecyclerAdaptor(buttons)
		rv.adapter = adapter

		val saveButton : Button = layout!!.findViewById(R.id.toolbox_save_button)
		saveButton.setOnClickListener( {
			CreateMoodleSketch.saving = true
		})

		sketch = CreateMoodleSketch()
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

	private fun loadFile(f : File)
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

	companion object
	{
		var musicFile: Uri? = null
		var mplayer: MediaPlayer? = null
		var duration = 0
		var file : Uri? = null
		var buttons : List<MoodleMethod>? = null
		var pw : PopupWindow? = null
		var inflater: LayoutInflater? = null
		var layout : View? = null
		var continueMoodle : Boolean = false
		var config: Array<String>? = null

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

		fun setView()
		{
			Handler().postDelayed(Runnable { pw!!.showAtLocation(layout, Gravity.CENTER, 0, 0) }, 100)
		}

		fun unSetView()
		{
			pw!!.dismiss()
		}
	}
}
