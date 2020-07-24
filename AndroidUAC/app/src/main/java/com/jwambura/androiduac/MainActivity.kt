package com.jwambura.androiduac

import android.content.Intent
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import com.jwambura.androiduac.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var viewModel: MyViewModel

    private var fileUri: Uri? = null
    private val filename: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        with(binding) {
            setContentView(root)
            setSupportActionBar(toolbar)

            viewModel = ViewModelProvider(this@MainActivity).get(MyViewModel::class.java)

            /*val navController = findNavController(R.id.nav_host_fragment)
            appBarConfiguration = AppBarConfiguration(navController.graph)
            setupActionBarWithNavController(navController, appBarConfiguration)*/

            contentMainData.buttonWeb.setOnClickListener {

                val newIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://jogwa-elections.web.app/"))
                //intent.data = Uri.parse("http://jogwa-elections.web.app/")
                startActivity(Intent.createChooser(newIntent, "Open Web Version"))
            }

            fab.setOnClickListener { view ->
                selectImage()
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            }
        }

        // Restore instance state
        savedInstanceState?.let {
            fileUri = it.getParcelable(KEY_FILE_URI)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }*/

    private fun selectImage() {
        Log.d(TAG, "launchCamera")

        // Pick an image from storage
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "audio/*"

        startActivityForResult(intent, RC_TAKE_AUDIO)

        //val newIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        //startActivityForResult(Intent.createChooser(newIntent, "Select Audio File"), RC_TAKE_AUDIO)
    }

    public override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)
        out.putParcelable(KEY_FILE_URI, fileUri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")

        if (requestCode == RC_TAKE_AUDIO) {
            if (resultCode == RESULT_OK) {
                fileUri = data?.data

                assert(fileUri != null)
                Log.d(TAG, "Uri: ${fileUri!!}")

                browseFile()
            } else {
                Toast.makeText(this, "Taking audio file failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun browseFile() {
        val timestamp = System.currentTimeMillis()

        val filename = fileUri!!.lastPathSegment!!.substring(fileUri!!.lastPathSegment!!.lastIndexOf("/").plus(1))

        //val myFile = File(fileUri.toString())

        //val fileNameMap = URLConnection.getFileNameMap()
        //val myFileType = fileNameMap.getContentTypeFor(fileUri.toString())

        //val fileSizeInBytes: Long = myFile.length()
        //val fileSizeInKB = fileSizeInBytes / 1024
        //val fileSizeInMB = fileSizeInKB / 1024

        val extractor = MediaExtractor()
        extractor.setDataSource(this, fileUri!!, null)

        val format = extractor.getTrackFormat(0)

        val count = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)

        binding.contentMainData.materialTextViewFileName.text = filename
        //binding.contentMainData.materialTextViewFileChannels.text = myFileType
        binding.contentMainData.materialTextViewFileNumOfChannels.text = count.toString()
        //binding.contentMainData.materialTextViewFileContentType.text = myFileType
        //binding.contentMainData.materialTextViewFileSize.text = fileSizeInKB.toString()

        /*
         * Get the file's content URI from the incoming Intent, then
         * get the file's MIME type
         */
        binding.contentMainData.materialTextViewFileContentType.text = contentResolver.getType(fileUri!!)

        contentResolver.query(fileUri!!, null, null, null, null)
        ?.use { cursor ->
            /*
             * Get the column indexes of the data in the Cursor,
             * move to the first row in the Cursor, get the data,
             * and display it.
             */
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            binding.contentMainData.materialTextViewFileName.text = cursor.getString(nameIndex)
            binding.contentMainData.materialTextViewFileSize.text = (cursor.getLong(sizeIndex) / 1024).toString()
        }
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
        const val RC_TAKE_AUDIO = 2

        private const val KEY_FILE_URI = "key_file_uri"
    }
}