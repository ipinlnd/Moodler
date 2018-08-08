package com.nlnd.moodler.feature

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
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import java.io.IOException
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager

class CreateMoodle : AppCompatActivity()
{
	private var sketch : PApplet? = null
	private var context : Context? = null
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
		val frame = FrameLayout(this)
		context = this
		makeList()
		frame.id = CompatUtils.getUniqueViewId()
		setContentView(frame, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT))
		if (savedInstanceState == null)
			file = intent.extras.getParcelable("file")

		if (file == null)
			return

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
		val density = context!!.getResources().getDisplayMetrics()
		pw = PopupWindow(layout, (density.widthPixels * .8).toInt(), (density.heightPixels * 0.5).toInt(), true)
		(layout!!.findViewById(R.id.toolbox_close_button) as Button).setOnClickListener { pw!!.dismiss() }
		pw!!.isOutsideTouchable = false;

		val rv = layout!!.findViewById<RecyclerView>(R.id.toolbox_recycler_view)
		val layoutManager = LinearLayoutManager(context,
				LinearLayoutManager.HORIZONTAL, false)
		rv.layoutManager = layoutManager
		val dividerItemDecoration = DividerItemDecoration(rv.context,
				layoutManager.orientation)
		rv.addItemDecoration(dividerItemDecoration)
		val adapter = ToolsRecyclerAdaptor(buttons, context)
		rv.adapter = adapter

		val saveButton : Button = layout!!.findViewById(R.id.toolbox_save_button);
		saveButton.setOnClickListener( {
			CreateMoodleSketch.saving = true;
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
