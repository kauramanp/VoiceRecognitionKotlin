package com.aman.voicerecognition.ui.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aman.voicerecognition.BuildConfig
import com.aman.voicerecognition.WavClass
import com.aman.voicerecognition.databinding.FragmentRecordVoiceBinding
import com.aman.voicerecognition.ui.activities.MainActivity
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordVoiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordVoiceFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var myPlayer: MediaPlayer? = null
    private var outputFile: String = ""
    val binding: FragmentRecordVoiceBinding by lazy {
        FragmentRecordVoiceBinding.inflate(layoutInflater)
    }
    val mainActivity: MainActivity by lazy {
        requireActivity() as MainActivity
    }

    private val TAG = "RecordVoiceFragment"

    val recordAudioPermission = android.Manifest.permission.RECORD_AUDIO

    val readStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_AUDIO
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE

    }

    val writeStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_AUDIO
    } else {
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    // Register the permission request callback using Activity Result API
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle the permissions result here
        if (permissions[android.Manifest.permission.RECORD_AUDIO] == true &&
            permissions[readStoragePermission] == true &&
            permissions[writeStoragePermission] == true
        ) {
            // Permissions granted
            Toast.makeText(requireActivity(), "All permissions granted", Toast.LENGTH_SHORT).show()
            start()
        } else {
            // Permissions not granted
            Toast.makeText(requireActivity(), "Permissions denied", Toast.LENGTH_SHORT).show()
            openAppSettings()
        }
    }
    private val wavObj: WavClass by lazy {
        WavClass(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startBtn.setOnClickListener { v ->
            if (hasPermissions()) {
                // Permissions are granted
                start()
            } else {
                // Request permissions
                requestPermissionsWithRationale()
            }
        }

        binding.stopBtn.setOnClickListener { v ->
            stop(v)
        }

        binding.playBtn.setOnClickListener { v ->
            play(v)
        }

        binding.stopPlayBtn.setOnClickListener { v ->
            stopPlay(v)
        }

        binding.btnUpload.setOnClickListener { _ ->
            mainActivity.binding.llProgress.visibility = View.VISIBLE
            uploadAudio()
        }
    }

    private fun hasPermissions(): Boolean {
        val recordAudioPermission = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.RECORD_AUDIO
        )
        val readStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(), readStoragePermission
        )
        val writeStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(), writeStoragePermission
        )
        return recordAudioPermission == PackageManager.PERMISSION_GRANTED &&
                readStoragePermission == PackageManager.PERMISSION_GRANTED &&
                writeStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    // Request permissions with rationale handling
    private fun requestPermissionsWithRationale() {
        Log.e(
            TAG,
            "recordAudioPermission ${recordAudioPermission} readStoragePermission $readStoragePermission writeStoragePermission $writeStoragePermission"
        )
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), android.Manifest.permission.RECORD_AUDIO
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), readStoragePermission
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), writeStoragePermission
        )

        if (shouldShowRationale) {
            // Show rationale to the user
            Toast.makeText(
                requireActivity(),
                "Permissions are required for the app to function properly",
                Toast.LENGTH_LONG
            ).show()
            // Request permissions after rationale
            openAppSettings()
        } else {
            // Request permissions directly
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                writeStoragePermission,
                recordAudioPermission
            )
        )
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = android.net.Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun start() {
        try {
            wavObj.startRecording()
        } catch (e: IllegalStateException) {
            // start:aman is called before prepare()
            // prepare: aman is called after start() or before setOutputFormat()
            e.printStackTrace()
        } catch (e: IOException) {
            // prepare() fails
            e.printStackTrace()
        }

        binding.text1.text = "Recording Point: Recording"
        binding.startBtn.isEnabled = false
        binding.btnUpload.isEnabled = false
        binding.stopBtn.isEnabled = true

        Toast.makeText(
            requireContext(), "Start recording...",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun stop(view: View?) {
        try {
            wavObj.stopRecording()

            binding.stopBtn.isEnabled = false
            binding.playBtn.isEnabled = true
            binding.btnUpload.isEnabled = true
            binding.text1.text = "Recording Point: Stop recording"

            Toast.makeText(
                requireContext(), "Stop recording...",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IllegalStateException) {
            //  aman is called before start()
            e.printStackTrace()
        } catch (e: RuntimeException) {
            // no valid audio/video data has been received
            e.printStackTrace()
        }
    }

    private fun play(view: View?) {
        try {
            outputFile = wavObj.finalFile
            myPlayer = MediaPlayer()
            myPlayer!!.setDataSource(outputFile)
            myPlayer!!.prepare()
            myPlayer!!.start()

            binding.playBtn.isEnabled = false
            binding.stopPlayBtn.isEnabled = true
            binding.text1.text = "Recording Point: Playing"

            Toast.makeText(
                requireContext(), "Start play the recording...",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    private fun stopPlay(view: View?) {
        try {
            if (myPlayer != null) {
                myPlayer?.stop()
                myPlayer?.release()
                myPlayer = null
                binding.playBtn.isEnabled = true
                binding.stopPlayBtn.isEnabled = false
                binding.text1.text = "Recording Point: Stop playing"

                Toast.makeText(
                    requireContext(), "Stop playing the recording...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun uploadAudio() {
        outputFile = wavObj.getFinalFile()
        Log.e(TAG, "uploadAudio: $outputFile",)
        mainActivity.binding.llProgress.visibility = View.GONE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecordVoiceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordVoiceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

//    fun prepareFilePart(filePath: String): MultipartBody.Part {
//        val file = File(filePath)
//        val requestBody: RequestBody = RequestBody.create(
//            "audio/*".toMediaTypeOrNull(), // Adjust MIME type if needed
//            file
//        )
//        return MultipartBody.Part.createFormData("audio", file.name, requestBody)
//    }


