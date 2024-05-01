package com.example.chatmessenger.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatmessenger.R
import com.example.chatmessenger.Utils
import com.example.chatmessenger.adapter.MessageAdapter
import com.example.chatmessenger.databinding.FragmentChatfromHomeBinding
import com.example.chatmessenger.modal.Messages
import com.example.chatmessenger.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView
import android.content.Intent
import android.provider.MediaStore
import android.app.Activity
import android.net.Uri



class ChatfromHome : Fragment() {

    lateinit var args : ChatfromHomeArgs
    lateinit var binding: FragmentChatfromHomeBinding
    lateinit var viewModel : ChatAppViewModel
    lateinit var toolbar: Toolbar
    lateinit var adapter : MessageAdapter

    private val PICK_IMAGE_REQUEST = 71

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatfrom_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolBarChat)
        val circleImageView = toolbar.findViewById<CircleImageView>(R.id.chatImageViewUser)
        val textViewName = toolbar.findViewById<TextView>(R.id.chatUserName)

        args = ChatfromHomeArgs.fromBundle(requireArguments())
        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        Glide.with(view.context)
            .load(args.recentchats.friendsimage!!)
            .placeholder(R.drawable.person)
            .dontAnimate()
            .into(circleImageView)

        textViewName.text = args.recentchats.name

        binding.chatBackBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatfromHome_to_homeFragment)
        }

        binding.sendBtn.setOnClickListener {
            viewModel.sendMessage(
                Utils.getUidLoggedIn(),
                args.recentchats.friendid!!,
                args.recentchats.name!!,
                args.recentchats.friendsimage!!
            )
        }

        binding.attachPhotoBtn.setOnClickListener {
            launchGallery()
        }

        viewModel.getMessages(args.recentchats.friendid!!)
            .observe(viewLifecycleOwner, Observer { initRecyclerView(it) })
    }

    private fun initRecyclerView(list: List<Messages>) {
        adapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        binding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        adapter.setList(list)
        adapter.notifyDataSetChanged()
        binding.messagesRecyclerView.adapter = adapter
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            if (selectedImage != null) {
                // Здесь можно загрузить выбранное изображение и отправить его
            }
        }
    }
}
