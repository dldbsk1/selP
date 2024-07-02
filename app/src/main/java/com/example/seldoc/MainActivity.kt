package com.example.seldoc

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seldoc.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainAdapter: MainAdapter
    private lateinit var headerView: View
    private lateinit var btnSearch: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        headerView = binding.mainDrawerView.getHeaderView(0)
        btnSearch = findViewById(R.id.btnSearch)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        applySettings()


        // DrawerLayout Toggle
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawer,
            R.string.drawer_opened,
            R.string.drawer_closed
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        // Drawer 메뉴
        binding.mainDrawerView.setNavigationItemSelectedListener(this)

        headerView = binding.mainDrawerView.getHeaderView(0)
        val btnAuth = headerView.findViewById<Button>(R.id.btnAuth)
        btnAuth.setOnClickListener {
            Log.d("mobileapp", "인증 버튼")
            val intent = Intent(this, AuthActivity::class.java)
            if (btnAuth.text == "로그인")
                intent.putExtra("status", "logout")
            else if (btnAuth.text == "로그아웃")
                intent.putExtra("status", "login")
            startActivity(intent)
            binding.drawer.closeDrawers()
        }


        binding.btnSearch.setOnClickListener {
            val auth = MyApplication.auth
            if (MyApplication.checkAuth()) {
                val name = binding.edtName.text.toString()
                Log.d("mobileapp", name)

                val call: Call<PharmacyResponse> =
                    RetrofitConnection.xmlNetworkService.getPharmacyList(
                        "xlzxWbz7Y4lPOTo1GowhCP5T5e2QRxCHQAQLgvVi4gtXBPhnT1EertJdLoAIvxIJwxgTa5+ouYeYLmEE914J8A==", // 일반인증키(Decoding)
                        name,  //pharm name
                        1,     // pageNo
                        10     // numOfRows
                    )

                call.enqueue(object : Callback<PharmacyResponse> {
                    override fun onResponse(
                        call: Call<PharmacyResponse>,
                        response: Response<PharmacyResponse>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("mobileApp", "$response")
                            Log.d("mobileApp", "${response.body()}")
                            binding.xmlRecyclerView.layoutManager =
                                LinearLayoutManager(applicationContext)
                            mainAdapter =
                                MainAdapter(response.body()?.body?.items?.item ?: mutableListOf())
                            binding.xmlRecyclerView.adapter = mainAdapter
                            binding.xmlRecyclerView.addItemDecoration(
                                DividerItemDecoration(
                                    applicationContext,
                                    LinearLayoutManager.VERTICAL
                                )
                            )
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "검색 결과를 가져오지 못했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<PharmacyResponse>, t: Throwable) {
                        Log.d("mobileApp", "onFailure ${call.request()}")
                        Toast.makeText(this@MainActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            } else {
                Toast.makeText(this, "인증을 먼저 해주세요.", Toast.LENGTH_LONG).show()
            }
        }


        binding.btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }

    private fun applySettings() {
        val backgroundColor = sharedPreferences.getString("background_color", "#FFFFFF")
        val textColor = sharedPreferences.getString("text_color", "#000000")
        val textSize = sharedPreferences.getString("text_size", "16")?.toFloat() ?: 16f

        binding.root.setBackgroundColor(Color.parseColor(backgroundColor))
        binding.edtName.setTextColor(Color.parseColor(textColor))
        binding.edtName.textSize = textSize
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_board -> {
                Log.d("mobileapp", "게시판 메뉴")
                val intent = Intent(this, BoardActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.item_setting -> {
                Log.d("mobileapp", "설정 메뉴")
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                binding.drawer.closeDrawers()
                return true
            }
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        applySettings()

        val btnAuth = headerView.findViewById<Button>(R.id.btnAuth)
        val tvAuth = headerView.findViewById<TextView>(R.id.tvID)
        if (MyApplication.checkAuth() || MyApplication.email != null) {
            btnAuth.text = "로그아웃"
            tvAuth.text = "${MyApplication.email}님\n 반갑습니다."
        } else {
            btnAuth.text = "로그인"
            tvAuth.text = "안녕하세요.."
        }
    }

    override fun onBackPressed() {
        if (binding.xmlRecyclerView.adapter != null) {
            binding.xmlRecyclerView.adapter = null
            binding.edtName.setText("")
        } else {
            super.onBackPressed()
        }
    }
}
