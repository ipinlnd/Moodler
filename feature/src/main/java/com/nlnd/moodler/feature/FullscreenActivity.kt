package com.nlnd.moodler.feature

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import java.io.File

class FullscreenActivity : AppCompatActivity()
{
	private var moodles : List<Moodle> = ArrayList()
	override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
    }

	private fun setView()
	{
		val lv : ListView = findViewById(R.id.main_page_moodles_list_view)
		val path = this.filesDir.toString() + "/"
		val directory = File(path)
		val files = directory.listFiles()
		moodles = ArrayList()
		for (i in files.indices)
		{
			if (files[i].getName()[files[i].getName().length - 1] == 'v' &&
					files[i].getName()[files[i].getName().length - 2] == '3' &&
					files[i].getName()[files[i].getName().length - 3] == 'v')
			{
				(moodles as ArrayList).add(Moodle(files[i].name))
				moodles[moodles.size - 1].file = files[i]
			}
		}

		val mpmlv = MainPageMoodlesListView(this, R.layout.main_page_list_view, moodles)
		lv.adapter = mpmlv

		lv.setOnItemClickListener({adapterView: AdapterView<*>?,
								   view: View, position: Int, l: Long ->
			val popup = PopupMenu(this, view)
			val inflater = popup.getMenuInflater()
			popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem ->

				onMenuItemClick(item, position)

				true
			})
			inflater.inflate(R.menu.moodle_popup, popup.menu)
			popup.show()
		})
	}

	override fun onResume()
	{
		super.onResume()
		setView()
	}

	fun onMenuItemClick(menu : MenuItem, position : Int): Boolean
	{
		when(menu.itemId)
		{
			R.id.popup_menu_watch -> watchMoodle(position)
			R.id.popup_menu_delete -> deleteMoodle(position)
			R.id.popup_menu_render -> renderMoodle(position)
			R.id.popup_menu_share -> shareMoodle(position)
			R.id.popup_menu_continue -> continueMoodle(position)
		}
		return true;
	}

	private fun continueMoodle(position: Int)
	{
		val intent = Intent(this, CreateMoodle::class.java)
		fileName = getFileName(Uri.fromFile(moodles[position].file))
		intent.putExtra("file", Uri.fromFile(moodles[position].file))
		intent.putExtra("continue", true);
		startActivity(intent)
	}

	private fun shareMoodle(position: Int)
	{
		val contentUri = FileProvider.getUriForFile(applicationContext,
				"com.nlnd.moodler.feature", moodles[position].file)
		var intent = Intent(Intent.ACTION_SEND)
		intent.putExtra(Intent.EXTRA_STREAM, contentUri)
		intent.type = "*/*"
		startActivity(Intent.createChooser(intent, "Share File"));
	}

	private fun renderMoodle(position: Int)
	{
		val intent = Intent(this, WatchMoodle::class.java)
		intent.putExtra("file", Uri.fromFile(moodles[position].file))
		intent.putExtra("render", true)
		startActivity(intent)
	}

	private fun deleteMoodle(position: Int)
	{
		moodles[position].file.delete()
		(moodles as ArrayList).clear()
		setView()
	}

	private fun watchMoodle(position: Int)
	{
		val intent = Intent(this, WatchMoodle::class.java)
		intent.putExtra("file", Uri.fromFile(moodles[position].file))
		intent.putExtra("render", false)
		startActivity(intent)
	}

	fun addNewMoodleClicked(v : View)
    {
		val getMusic = Intent(ACTION_OPEN_DOCUMENT)
		getMusic.addCategory(Intent.CATEGORY_OPENABLE)
		getMusic.setType("audio/*")
		startActivityForResult(getMusic, 42)
    }

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
	{
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == 42 && resultCode == Activity.RESULT_OK)
		{
			val mFile : Uri = data!!.data
			fileName = getFileName(mFile)
			val intent = Intent(this, CreateMoodle::class.java)
			intent.putExtra("file", mFile)
			intent.putExtra("continue", false)
			startActivity(intent)
		}
	}

	companion object
	{
		var fileName: String? = null
	}

	private fun getFileName(uri: Uri): String
	{
		var result: String? = null

		if (uri.scheme == "content")
		{
			val cursor = contentResolver.query(uri, null, null, null, null)
			try
			{
				if (cursor != null && cursor.moveToFirst())
				{
					result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
				}
			}
			finally
			{
				cursor!!.close()
			}
		}
		else
		{
			result = uri.path.split("/")[uri.path.split("/").size - 1]

			result = result.substring(0, result.length - 4)
		}
		return result!!
	}
}
