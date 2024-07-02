@file:Suppress("DEPRECATION")

package com.example.seldoc

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.seldoc.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_auth)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeVisibility(intent.getStringExtra("status").toString())

        binding.moveSign.setOnClickListener {    // 회원 가입 Button
            changeVisibility("signin")
        }

        binding.btnSignin.setOnClickListener {        // 가입 Button
            val email = binding.authEmail.text.toString()
            val password = binding.authPw.text.toString()
            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.authEmail.text.clear()
                    binding.authPw.text.clear()
                    if (task.isSuccessful) {
                        MyApplication.auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { sendTask ->
                                if (sendTask.isSuccessful) {
                                    Toast.makeText(
                                        baseContext,
                                        "회원가입 성공!!.. 메일을 확인해주세요",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d("mobileapp", "회원가입 성공!!")
                                    changeVisibility("logout")
                                } else {
                                    Toast.makeText(baseContext, "메일발송 실패", Toast.LENGTH_SHORT)
                                        .show()
                                    Log.d("mobileapp", "메일발송 실패")
                                    changeVisibility("logout")
                                }
                            }
                    } else {
                        Toast.makeText(baseContext, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        Log.d("mobileapp", "== ${task.exception} ==")
                        changeVisibility("logout")
                    }
                }
        }

        binding.loginBtn.setOnClickListener {   // 로그인 Button
            val email = binding.authEmail.text.toString()
            val password = binding.authPw.text.toString()
            MyApplication.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.authEmail.text.clear()
                    binding.authPw.text.clear()
                    if (task.isSuccessful) {
                        if (MyApplication.checkAuth()) {
                            MyApplication.email = email
                            Log.d("mobileapp", "로그인 성공")
                            finish()
                        } else {
                            Toast.makeText(baseContext, "이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("mobileapp", "이메일 인증 안됨")
                        }
                    } else {
                        Toast.makeText(baseContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                        Log.d("mobileapp", "로그인 실패")
                    }
                }
        }

        binding.logoutBtn.setOnClickListener {  // 로그아웃 Button
            MyApplication.auth.signOut()
            NaverIdLoginSDK.logout()
            MyApplication.email = null
            Log.d("mobileapp", "로그아웃")
            finish()
        }

        val requestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                Log.d("mobileapp", "account1 : ${task.toString()}")
                //Log.d("mobileapp","account2 : ${task.result}")
                try {
                    val account = task.getResult(ApiException::class.java)
                    val crendential = GoogleAuthProvider.getCredential(account.idToken, null)
                    MyApplication.auth.signInWithCredential(crendential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                MyApplication.email = account.email
                                Toast.makeText(baseContext, "구글 로그인 성공", Toast.LENGTH_SHORT).show()
                                Log.d("mobileapp", "구글 로그인 성공")
                                finish()
                            } else {
                                changeVisibility("logout")
                                Toast.makeText(baseContext, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
                                Log.d("mobileapp", "구글 로그인 실패")
                            }
                        }
                } catch (e: ApiException) { // APIException은 이미 지정된 exception말고 custom한 exception을 만들어서 쓰고 싶을때 사용
                    changeVisibility("logout")
                    Toast.makeText(
                        baseContext,
                        "구글 로그인 Exception : ${e.printStackTrace()},${e.statusCode}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("mobileapp", "구글 로그인 Exception : ${e.message}, ${e.statusCode}")
                }
            }

        binding.googleLoginBtn.setOnClickListener { // 구글인증 Button
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_client_id))
                .requestEmail()
                .build()
            val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
            requestLauncher.launch(signInIntent)
        }

        binding.btnNaverLogin.setOnClickListener {
            val oAuthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(result: NidProfileResponse) {
                            MyApplication.email = result.profile?.email.toString()
                            finish()
                        }
                        override fun onError(errorCode: Int, message: String) {}
                        override fun onFailure(httpStatus: Int, message: String) {}
                    })
                }

                override fun onError(errorCode: Int, message: String) {

                }

                override fun onFailure(httpStatus: Int, message: String) {
                }
            }
            NaverIdLoginSDK.initialize(
                this@AuthActivity, getString(R.string.naver_client_id),
                getString(R.string.naver_client_secret), "SelDoc")
            NaverIdLoginSDK.authenticate(this@AuthActivity, oAuthLoginCallback)
        }

    }


    fun changeVisibility(mode: String) {
        if (mode.equals("login")) {
            binding.run {
                authMainTextView.text = "정말 로그아웃하시겠습니까?"
                authMainTextView.visibility = View.VISIBLE
                logoutBtn.visibility = View.VISIBLE
                moveSign.visibility = View.GONE
                authEmail.visibility = View.GONE
                authPw.visibility = View.GONE
                btnSignin.visibility = View.GONE
                loginBtn.visibility = View.GONE
                googleLoginBtn.visibility = View.GONE
                btnNaverLogin.visibility = View.GONE
            }
        } else if (mode.equals("logout")) {
            binding.run {
                authMainTextView.text = "로그인 하거나 회원가입 해주세요."
                authMainTextView.visibility = View.VISIBLE
                logoutBtn.visibility = View.GONE
                moveSign.visibility = View.VISIBLE
                authEmail.visibility = View.VISIBLE
                authPw.visibility = View.VISIBLE
                btnSignin.visibility = View.GONE
                loginBtn.visibility = View.VISIBLE
                googleLoginBtn.visibility = View.VISIBLE
                btnNaverLogin.visibility = View.VISIBLE
            }
        } else if (mode.equals("signin")) {
            binding.run {
                authMainTextView.visibility = View.GONE
                logoutBtn.visibility = View.GONE
                moveSign.visibility = View.GONE
                authEmail.visibility = View.VISIBLE
                authPw.visibility = View.VISIBLE
                btnSignin.visibility = View.VISIBLE
                loginBtn.visibility = View.GONE
                googleLoginBtn.visibility = View.GONE
                btnNaverLogin.visibility = View.GONE
            }
        }
    }
}