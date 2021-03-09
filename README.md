
SNS Meetagin 포트폴리오
=============

개발언어 . Kotlin
개발 프로그램 . Android Studio

유튜브 영상이 준비 되어있습니다 

* 유튜브 포트폴리오 <https://youtu.be/0pU1JdHvN4c>

영상을 다 시청해주시고 코드를 봐주시면 이해하시기 더 편합니다



안녕하세요 SNS Meetagin 코드 설명드립니다




로그인 및 회원가입
-------------
1. [회원가입] JoinActivity


![iage](https://im.ezgif.com/tmp/ezgif-1-bb5365f7829c.gif)

<코드>

패스워드 패턴

    //패스워드 필터
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        Spanned: Spanned?,
        dstart: Int,
        dend: Int): CharSequence {

        val ps: Pattern = Pattern.compile("^[-.@0-9a-zA-Z]*\$")

        if (source!! == "" || ps.matcher(source).matches()) {

            return source
        }

        return ""


    }
    
    
닉네임 및 이메일 중복체크    

    //닉네임,이메일 중복체크
    private fun authCheck() {

        val emailValue = email_input.text.toString()
        val pwValue = password_input.text.toString()
        val nameValue = name_input.text.toString()




        Log.d("로그", "nameValue $nameValue")

        authStore.whereEqualTo("name", nameValue).get().addOnSuccessListener { nameData ->


            when {
                nameData == null || nameData.size() == 0 -> {

                    firebaseAuth.createUserWithEmailAndPassword(emailValue, pwValue).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userUid = firebaseAuth.uid.toString()
                            Log.d("로그", "uid $userUid")
                            authDatabaseAdd(emailValue, pwValue, nameValue, userUid)
                        }
                    }.addOnFailureListener {
                        input_btn.isEnabled = true
                        input_btn.text = "가입하기"

                        if (AuthOverLap in it.toString()) {
                            Snackbar.make(join_layout, "이미 가입된 이메일 계정입니다.", Snackbar.LENGTH_SHORT).show()
                        } else if (EmailFormError in it.toString()) {
                            Snackbar.make(join_layout, "아이디는 이메일 형식으로 작성해주세요.", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
                else -> {
                    Log.d("로그","nameData 크기 ${nameData.size()}")
                    for (i in nameData) {
                        Log.d("로그", "중복 ${i.data["name"]}")
                    }
                    input_btn.isEnabled = true
                    input_btn.text = "가입하기"
                    Snackbar.make(join_layout, "이미 존재하는 닉네임입니다.", Snackbar.LENGTH_SHORT).show()

                }
            }
        }.addOnFailureListener {
            Log.d("로그", "조회 에러 $it")
            Snackbar.make(join_layout, "계정 생성 실패, 다시 시도해 주세요.", Snackbar.LENGTH_SHORT).show()
            input_btn.isEnabled = true
            input_btn.text = "가입하기"
        }
    }
서버에 데이터 저장


    //파이어베이스 데이터 저장
    private fun authDatabaseAdd(id: String, pw: String, userName: String, userUid: String) {


        val pathData = "${id}_${System.currentTimeMillis()}"


        val authModel = AuthModel()

        authModel.apply {
            email = id
            uid = userUid
            name = userName
            path = pathData
            timeStamp = Date().toString()
        }

        var map = HashMap<String, Any>()
        map["name"] = userName.toString()


        var uid = FirebaseAuth.getInstance().currentUser!!.uid //파일 업로드
        FirebaseFirestore.getInstance().collection("profileName").document(uid).set(map)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {

                    SharedPreferenceFactory.putStrValue("userMainName", userName )   // 유저 닉네임
                    finish()


                }
            }


        authStore.document(pathData).set(authModel).addOnSuccessListener {
            Log.d(Constants.TAG, "로그인로그: ")
            Snackbar.make(join_layout, "계정 생성 완료", Snackbar.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id", id)
            intent.putExtra("pw", pw)
            startActivity(intent)

            overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)


        }.addOnFailureListener {
            Snackbar.make(join_layout, "계정 생성 실패, 다시 시도해 주세요.", Snackbar.LENGTH_SHORT).show()
            Log.d("로그", "error $it")
        }


    }
    
[로그인 후 인트로화면에서 메인화면으로->]  IntroActivity

![iage](https://im.ezgif.com/tmp/ezgif-1-2c7aef175b07.gif)


<코드>

데이터는 쉐어드로 조회합니다.

        // 사용자가 로그인을 전에 했을 경우 자동 로그인을 하기 위해 디바이스에 저장된 데이터값을 조회한다
        val uid = SharedPreferenceFactory.getStrValue("userToken", null)
        val name = SharedPreferenceFactory.getStrValue("userName", null)
        val email = SharedPreferenceFactory.getStrValue("userEmail", null)
        
        
인트로 화면에서 데이터를 조회하고
데이터가 없다면 -> 로그인 화면
데이터가 있다면 -> 메인 화면


   닉네임 이메일 유아이디가 없다면 로그인 화면 ->
   
        if (uid != null && name != null && email != null) {

            moveNext(uid, email, name)

        } else {

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)
                finish()
            }, 1500)


        }
        
  닉네임 이메일 유아이디가 있다면 메인 화면 ->
  
            Handler(Looper.getMainLooper()).postDelayed({

            authStore.whereEqualTo("uid",uid).get().addOnSuccessListener { querySnapshot ->

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)
                finish()

            }.addOnFailureListener { error ->
                Log.d("로그","error $error")
            }

        }, 1500)

  

