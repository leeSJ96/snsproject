package com.sjkorea.meetagain.intro

//페이스북,구글,익명로그인 구현
    //회원가입으로 변경

//class LoginActivity : AppCompatActivity() {
//    //구글
//    private lateinit var auth: FirebaseAuth
//    private val RC_SIGN_IN = 99
//    private var googleSignInClient: GoogleSignInClient? = null
//    private var firebaseAuth = FirebaseAuth.getInstance()
//    private var firebasestore = FirebaseFirestore.getInstance()
//
//
//    //페이스북
//    private val FB_SIGN_IN = 64206
//    private lateinit var loginManager: LoginManager
//    var callbackManager = CallbackManager.Factory.create()
//    private var activityLoginBinding: ActivityLoginBinding? = null
//    var nameid: String? = null
//
//    var uid: String? = null
//    var id: String? = null
//    var name : String? = null
//
//    var firestore: FirebaseFirestore? = null
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        var binding: ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
//        activityLoginBinding = binding
//        setContentView(activityLoginBinding!!.root)
//
//        loginIntent()
//
//        //구글 로그인
//        firestore = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//        auth = Firebase.auth
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        uid = auth.currentUser?.uid
//        id = auth.currentUser?.email
//
//        SharedPreferenceFactory.putStrValue("userToken", uid)
//        SharedPreferenceFactory.putStrValue("userEmail", id)
//
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//
//        google_login.setOnClickListener { // google login button
//            val signInIntent = googleSignInClient?.signInIntent
//            startActivityForResult(signInIntent, RC_SIGN_IN)
//
//
//        }
//
//        //페이스북 로그인
//        facebook_login.setOnClickListener {
//            facebookLogin()
//
//        }
//
//        //카카오톡 로그인
////        kakaologin()
//
//        // 1. 기본 다이얼로그
//        sign_in_button.setOnClickListener {
//            var builder = AlertDialog.Builder(this)
//            builder.setTitle("익명로그인")
//            builder.setMessage("익명으로 로그인 하시겠습니까? \n(익명 로그인시 앱삭제시 데이터는 저장되지 않습니다) ")
//
//            // 버튼 클릭시에 무슨 작업을 할 것인가!
//            var listener = object : DialogInterface.OnClickListener {
//                override fun onClick(p0: DialogInterface?, p1: Int) {
//                    when (p1) {
//                        //로그인
//                        DialogInterface.BUTTON_NEGATIVE ->
//                            Ananimus()
//
//                        //로그인 취소
//                        DialogInterface.BUTTON_POSITIVE ->
//                            Snackbar.make(login_layout, "로그인 취소", Snackbar.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//            builder.setPositiveButton("로그인 취소", listener)
//            builder.setNegativeButton("로그인", listener)
//
//            builder.show()
//        }
//
//    }
//
//
//
//
//
//
//    //Change UI according to user data.
//    fun updateUI(account: FirebaseUser?) {
//
//
//        var email = FirebaseAuth.getInstance().currentUser?.email
//
//        firebasestore.collection("user_id").document(email!!).get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
////                    var idDTO = task.result?.toObject(IdDTO::class.java)
//                    var idDTO= task.result?.toObject(IdDTO::class.java)
//                    IDDTO = idDTO.toString()
//
//                    Log.d(Constants.TAG, "idDTO : ${idDTO.toString()} ")
//
//
//                    if (account != null) {
//                        Snackbar.make(login_layout, "로그인이 되었습니다", Snackbar.LENGTH_SHORT).show()
////                        firestore?.collection("user_id")?.document(id.toString())?.get()
////                            ?.addOnCompleteListener {
////                                if (it.isSuccessful) {
////                                    var idDTO = it.result?.toObject(IdDTO::class.java)
////                                    IDDTO = idDTO.toString()
////
////
////                                    Log.d(Constants.TAG, "idDTO : $idDTO ")
////                                    SharedPreferenceFactory.putStrValue("userName", Constants.IDDTO)
////                                    val intent = Intent(this, MainActivity::class.java)
////                                    startActivity(intent)
////                                    overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)
////                                    finish()
////
////                                }
////                            }
//
//                        if (idDTO == null) {
//                            startActivity(Intent(this, FirstVisitActivity::class.java))
//                        } else {
//
//                            startActivity(Intent(this, MainActivity::class.java))
//                        }
//                    } else {
//                        Snackbar.make(login_layout, "로그인이 실패되었습니다", Snackbar.LENGTH_SHORT).show()
//                    }
//
//
//
//                }
//            }
//
//
//
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        callbackManager?.onActivityResult(requestCode, resultCode, data);
//
//        // 구글 로그인
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)!!
//                firebaseAuthWithGoogle(account)
//            } catch (e: ApiException) {
//
//                // Google Sign In failed, update UI appropriately
//                // ...
//            }
//        }
//
//
//    }
//
//
//    //구글로그인
//    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
//        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
//        auth.signInWithCredential(credential)
//
//            .addOnCompleteListener(this) { task ->
//
//                if (task.isSuccessful) {
//
//                    val user = auth.currentUser
//                    updateUI(user)
//
//
//
//                    Snackbar.make(login_layout, "로그인이 되었습니다", Snackbar.LENGTH_SHORT).show()
//
//                } else {
//                    Snackbar.make(login_layout, "로그인 실패", Snackbar.LENGTH_SHORT).show()
//                    updateUI(null)
//                }
//
//            }
//
//    }
//
//    fun kakaologin() {
//        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//            if (error != null) {
//                when {
//                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
//                        Snackbar.make(login_layout, "접근이 거부 됨(동의 취소)", Snackbar.LENGTH_SHORT)
//                            .show()
//                    }
//                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
//                        Snackbar.make(login_layout, "유효하지 않은 앱", Snackbar.LENGTH_SHORT).show()
//                    }
//                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
//                        Snackbar.make(
//                            login_layout,
//                            "인증 수단이 유효하지 않아 인증할 수 없는 상태",
//                            Snackbar.LENGTH_SHORT
//                        ).show()
//                    }
//                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
//                        Snackbar.make(login_layout, "요청 파라미터 오류", Snackbar.LENGTH_SHORT).show()
//                    }
//                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
//                        Snackbar.make(login_layout, "유효하지 않은 scope ID", Snackbar.LENGTH_SHORT)
//                            .show()
//                    }
//                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
//                        Snackbar.make(
//                            login_layout,
//                            "설정이 올바르지 않음(android key hash)",
//                            Snackbar.LENGTH_SHORT
//                        ).show()
//                    }
//                    error.toString() == AuthErrorCause.ServerError.toString() -> {
//                        Snackbar.make(login_layout, "서버 내부 에러", Snackbar.LENGTH_SHORT).show()
//                    }
//                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
//                        Snackbar.make(login_layout, "앱이 요청 권한이 없음", Snackbar.LENGTH_SHORT)
//                            .show()
//                    }
//                    else -> { // Unknown
//                        Snackbar.make(login_layout, "기타 에러", Snackbar.LENGTH_SHORT).show()
//                    }
//                }
//            } else if (token != null) {
//                val user = auth.currentUser
//                updateUI(user)
//
//
//                Snackbar.make(login_layout, "로그인에 성공하였습니다.", Snackbar.LENGTH_SHORT).show()
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//            }
//        }
//
//        kakao_login_button.setOnClickListener {
//            if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
//                LoginClient.instance.loginWithKakaoTalk(this, callback = callback)
//
//            } else {
//                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)
//            }
//        }
//
//        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
//            if (error != null) {
//                Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
//            } else if (tokenInfo != null) {
//                Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//
//            }
//        }
//    }
//
//
//    fun moveMainPage(user: FirebaseUser?) {
//        if (user != null) {
//            startActivity(Intent(this, FirstVisitActivity::class.java))
//            finish()
//        }
//    }
//
//    //페이스북로그인
//
//    fun facebookLogin() {
//        LoginManager
//            .getInstance()
//            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
//        LoginManager
//            .getInstance()
//            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
//                override fun onSuccess(result: LoginResult?) {
//                    handleFacebookAccessToken(result?.accessToken)
//                    Snackbar.make(login_layout, "로그인이 되었습니다", Snackbar.LENGTH_SHORT).show()
//                }
//
//                override fun onCancel() {
//
//                }
//
//                override fun onError(error: FacebookException?) {
//
//                }
//
//            })
//    }
//
//    fun handleFacebookAccessToken(token: AccessToken?) {
//        var credential = FacebookAuthProvider.getCredential(token?.token!!)
//        auth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                moveMainPage(auth?.currentUser)
//
//
//            }
//        }
//
//    }
//
//
//    //익명로그인
//    fun Ananimus() {
//        auth.signInAnonymously()
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    val user = auth.currentUser
//
//
//
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//
//                    Toast.makeText(
//                        baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    updateUI(null)
//                }
//
//                // ...
//            }
//    }
//
//    //로그인 창 이동
//    fun loginIntent() {
//        login_button.setOnClickListener {
//            val intent = Intent(this, LoginActivitysub::class.java)
//            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//
//        }
//
//    }
//
//    fun firestoreData() {
//
//        var idDTO = IdDTO()
//        idDTO.name = auth?.currentUser?.email
//
//        firebasestore?.collection("user_mainid")?.document(auth?.uid.toString())?.set(idDTO)
//    }
//
//
//}