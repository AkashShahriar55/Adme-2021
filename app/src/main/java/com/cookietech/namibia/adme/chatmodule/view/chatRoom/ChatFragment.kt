package com.cookietech.namibia.adme.chatmodule.view.chatRoom

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cookietech.namibia.adme.chatmodule.view.FirebaseViewModel
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.chatmodule.data.model.Message
import com.cookietech.namibia.adme.chatmodule.data.source.firebase.FirebaseDaoImpl
import com.cookietech.namibia.adme.chatmodule.utils.FileHelper
import com.cookietech.namibia.adme.chatmodule.utils.Utility
import com.cookietech.namibia.adme.chatmodule.utils.bindingFakeAudioProgress
import com.cookietech.namibia.adme.chatmodule.utils.extension.afterTextChanged
import com.cookietech.namibia.adme.chatmodule.utils.states.NetworkState
import com.cookietech.namibia.adme.databinding.FragmentChatBinding
import com.cookietech.namibia.adme.managers.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_user_info.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
open class ChatFragment : Fragment() {
    var mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    companion object {
        val TAG = ChatFragment::class.java.simpleName
        const val LOADING = "loading"
    }

    lateinit var audioHelper: AudioHelper

    private val fakeMsgAudio = Message(
        audioUrl = LOADING,
        audioFile = LOADING,
        isOwner = true
    )

    private val fakeMsg = Message(
        photoUrl = LOADING,
        isOwner = true
    )

    @Inject
    lateinit var fileHelper: FileHelper

    private val firebaseVm: FirebaseViewModel by viewModels({ requireActivity() })
    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: MsgAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(LayoutInflater.from(context))
        binding.lifecycleOwner = this
        binding.firebaseVm = firebaseVm

        audioHelper = AudioHelper(fileHelper, activity as AppCompatActivity)

        adapter = MsgAdapter(getMsgAdapterListener())
        binding.recyclerView.layoutAnimation = null
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = adapter

        observePushPhotoState()
        observePushAudioState()

        binding.messageEditText.afterTextChanged { text ->
            binding.sendButton.isActivated = text.isNotBlank()
        }

        binding.photoPickerButton.setOnClickListener {
            onPhotoPickerClick()
        }
        binding.sendButton.setOnTouchListener(onSendButtonTouch())

        binding.sendButton.isActivated = false
        binding.progressBar.visibility = ProgressBar.INVISIBLE
        return binding.root
    }

    private fun onPhotoPickerClick() {
        PermissionManager.checkStoragePermission(
            requireContext(),
            object : PermissionManager.SimplePermissionCallback {
                override fun onPermissionGranted() {
                    Log.d("permission_debug", "finally onPermissionGranted: ")
                    mainScope.launch {
                        val intentGallery = Intent(Intent.ACTION_PICK)
                        intentGallery.type = "image/jpeg, image/png"
                        intentGallery.putExtra(Intent.EXTRA_LOCAL_ONLY, true)

                        val chooserIntent = Intent.createChooser(intentGallery, "Select picture")
                        fileHelper.createPhotoMediaFile()?.let { photoFile ->
                            val cameraIntent = Utility.getCameraIntent(requireContext(), photoFile)
                            FileHelper.currentPhotoPath = photoFile.absolutePath
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
                        }

                        startActivityForResult(chooserIntent, FirebaseDaoImpl.RC_PHOTO_PICKER)
                    }

                }

                override fun onPermissionDenied() {
                    Log.d("permission_debug", "finally onPermissionGranted: ")
                }

            },
            binding.mainLayout
        )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("akash_chat_debug", "onActivityResult: ")
        if (requestCode == FirebaseDaoImpl.RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK) {
            val picUri = if (data != null) data.data!! else galleryAddPic()
            picUri?.let { firebaseVm.pushPicture(picUri) }
        }
    }

    private fun galleryAddPic(): Uri? {
        val photoFile = File(FileHelper.currentPhotoPath)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(photoFile.extension)
        MediaScannerConnection.scanFile(
            requireContext(),
            arrayOf(photoFile.absolutePath),
            arrayOf(mimeType),
            null
        )
        return Uri.fromFile(photoFile)
    }

    private fun onSendButtonTouch() = object : View.OnTouchListener {
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            when (view.isActivated) {
                true -> onSendButtonClick(motionEvent, view)
                false -> if (onMicButtonClick(motionEvent)) return true
            }

            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> view.isPressed = false
                MotionEvent.ACTION_DOWN -> view.isPressed = true
            }
            return true
        }
    }

    private fun onSendButtonClick(motionEvent: MotionEvent, view: View) {
        when (motionEvent.action) {
            MotionEvent.ACTION_UP -> {
                if (view.isActivated) pushMsg()
                binding.messageEditText.setText("")
            }
        }
    }

    private fun pushMsg() {
        firebaseVm.pushMsg(binding.messageEditText.text.toString())
    }

    private fun onMicButtonClick(motionEvent: MotionEvent): Boolean {
        PermissionManager.checkRecordPermission(requireContext(),object :PermissionManager.SimplePermissionCallback{
            override fun onPermissionGranted() {
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> audioHelper.startRecording()
                    MotionEvent.ACTION_UP -> {
                        audioHelper.stopRecording()
                        val recordFileName = audioHelper.recordFileName ?: return
                        val recorderDuration = audioHelper.recorderDuration ?: 0
                        if (recorderDuration > 1000) pushAudio(recordFileName, recorderDuration)
                    }
                }
            }

            override fun onPermissionDenied() {

            }

        })

        return false
    }

    private fun pushAudio(recordFileName: String, recorderDuration: Long) {
        fakeMsgAudio.apply {
            audioFile = recordFileName
            audioDuration = recorderDuration
        }
        firebaseVm.pushAudio(recordFileName, recorderDuration)
    }

    private fun observePushPhotoState() {
        firebaseVm.pushImgStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                NetworkState.LOADING -> {
                    val newList = mutableListOf<Message>()
                    newList.addAll(adapter.currentList)
                    newList.add(fakeMsg)
                    adapter.submitList(newList) { scrollToPosition() }
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun observePushAudioState() {
        firebaseVm.pushAudioStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                NetworkState.LOADING -> {
                    val newList = mutableListOf<Message>()
                    newList.addAll(adapter.currentList)
                    newList.add(fakeMsgAudio)
                    adapter.submitList(newList) { scrollToPosition() }
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun scrollToPosition() {
        binding.recyclerView.layoutManager?.scrollToPosition(adapter.itemCount - 1)
    }

    private fun getMsgAdapterListener(): MsgAdapter.MsgAdapterListener {
        return object : MsgAdapter.MsgAdapterListener {
            override fun showPic(view: View, message: Message) {
                val reviewDialog = PhotoDialog(message.photoUrl!!)
                reviewDialog.show(parentFragmentManager, PhotoDialog::class.simpleName)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar, item: Message) {
                val parentView = seekBar.parent as ConstraintLayout
                val playButton = parentView.findViewById<View>(R.id.playButton)
                if (playButton.isActivated) audioHelper.stopTimer()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar, item: Message) {
                val parentView = seekBar.parent as ConstraintLayout
                val playButton = parentView.findViewById<View>(R.id.playButton) ?: return
                if (playButton.isActivated) {
                    audioHelper.stopTimer()
                    audioHelper.stopPlaying()
                    audioHelper.startPlaying()
                } else {
                    val audioTimeView = parentView.findViewById<TextView>(R.id.audioTimeTextView)
                    val audioDuration = item.audioDuration ?: return
                    audioHelper.setAudioTime(seekBar, audioTimeView, audioDuration)
                }
            }

            override fun onAudioClick(view: View, message: Message) {
                if (message.audioDownloaded.not()) bindingFakeAudioProgress(view, message)
                else audioHelper.setupAudioHelper(view, message)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        audioHelper.onStop()
    }
}