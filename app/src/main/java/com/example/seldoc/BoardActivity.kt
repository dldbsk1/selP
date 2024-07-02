package com.example.seldoc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seldoc.databinding.ActivityBoardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

class BoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoardBinding
    private var datas: MutableList<String> = mutableListOf()
    private lateinit var adapter: BoardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadLastSavedTime()

        adapter = BoardAdapter(datas)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra("result")?.let {
                    if (it.isNotEmpty()) {
                        datas.add(it)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        binding.mainFab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            intent.putExtra("today", dateFormat.format(System.currentTimeMillis()))
            requestLauncher.launch(intent)
        }

        loadCommentsFromFirebase()
    }

    private fun loadLastSavedTime() {
        val sharedPreferences = getSharedPreferences("seldoc_prefs", Context.MODE_PRIVATE)
        val lastSavedTime = sharedPreferences.getString("last_saved_time", "정보 없음")
        binding.lastsaved.text = "마지막 저장 시간: $lastSavedTime"
    }

    private fun loadCommentsFromFirebase() {
        val database = FirebaseDatabase.getInstance().reference.child("comments")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datas.clear()
                for (commentSnapshot in snapshot.children) {
                    val comment = commentSnapshot.child("comments").getValue(String::class.java)
                    if (comment != null) {
                        datas.add(comment)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터베이스 읽기 실패 처리
            }
        })
    }
}
