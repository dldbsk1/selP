package com.example.seldoc

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.seldoc.databinding.ActivityAddBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.saveButton.setOnClickListener {
            val edtStr = binding.input.text.toString()

            if (edtStr.isNotEmpty()) {
                val intent = intent
                intent.putExtra("result", edtStr)
                setResult(Activity.RESULT_OK, intent)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val currentTime = dateFormat.format(System.currentTimeMillis())

                // Firestore에 데이터 저장
                val data = mapOf(
                    "email" to MyApplication.email,
                    "stars" to binding.ratingBar.rating.toFloat(),
                    "comments" to binding.input.text.toString(),
                    "date_time" to currentTime
                )
                FirebaseFirestore.getInstance().collection("comments")
                    .add(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "데이터 저장 성공", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "데이터 저장 실패", Toast.LENGTH_LONG).show()
                    }

                // Firebase Storage에 게시글 이름, 이메일, 저장 시간 저장
                val storageRef = FirebaseStorage.getInstance().reference.child("$edtStr.txt")
                val fileContents =
                    "Comments: $edtStr\nEmail: ${MyApplication.email}\nDate Time: $currentTime"
                val uploadTask = storageRef.putBytes(fileContents.toByteArray())
                uploadTask.addOnSuccessListener {
                    Toast.makeText(this, "파일 저장 성공", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "파일 저장 실패", Toast.LENGTH_LONG).show()
                }

                // SharedPreferences에 마지막 저장 시간 저장
                saveLastSavedTime(currentTime)

                finish()
            } else {
                Toast.makeText(this, "한줄평 먼저 입력해주세요..", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveLastSavedTime(dateTime: String) {
        val sharedPreferences = getSharedPreferences("seldoc_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("last_saved_time", dateTime)
        editor.apply()
    }
}
