
SNS Meetagin 포트폴리오
=============

개발언어 . Kotlin
개발 프로그램 . Android Studio

라이브러리 glide , firebase , viewpager, viewBinding , gson , okHttp,
firebase 사용하였습니다


유튜브 영상이 준비 되어있습니다 



영상을 다 시청해주시고 코드를 봐주시면 이해하시기 더 편합니다

* 유튜브 포트폴리오 <https://youtu.be/0pU1JdHvN4c>   
SNS Meetagin 코드 설명드립니다  
   
      

로그인 및 회원가입
-------------
#  [회원가입] JoinActivity   
   
![image](https://im.ezgif.com/tmp/ezgif-1-bb5365f7829c.gif)   
   
<코드>   
   
# [패스워드 패턴]
   
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
    
       
# [닉네임 및 이메일 중복체크]    
    
    
    
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
        
파이어베이스에서 데이터를 조회해서 중복체크      
   
      
         
# [서버에 데이터 저장]   

 
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

 파이어베이스 서버에 회원 정보(uid,가입한시간,이메일,닉네임)을 저장합니다     
     
         
             
             
# [자동 로그인] IntroActivity   
   
![iage](https://im.ezgif.com/tmp/ezgif-1-2c7aef175b07.gif)    

   
<코드>   
   


        // 사용자가 로그인을 전에 했을 경우 자동 로그인을 하기 위해 디바이스에 저장된 데이터값을 조회한다
        val uid = SharedPreferenceFactory.getStrValue("userToken", null)
        val name = SharedPreferenceFactory.getStrValue("userName", null)
        val email = SharedPreferenceFactory.getStrValue("userEmail", null)
           
 데이터는 쉐어드로 조회합니다.      
     
         
             
             
 
   
  #  [인트로 화면 데이터 조회]   
   
   
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
              
인트로 화면에서 데이터를 조회하고
조회한 데이터가 없다면 -> 로그인 화면
조회한 데이터가 있다면 -> 메인 화면

   핸들러를 사용 했습니다
   닉네임 이메일 유아이디가 없다면 로그인 화면 ->  

  
업로드 및 수정,삭제
-------------
   
# [게시글 업로드] AddActivity   

    
![iage](https://im.ezgif.com/tmp/ezgif-1-d4e57af9a9df.gif)   
   

사진이 있을경우 와 사진이 없을경우 나눠집니다   
   
<코드>   
   



    //사진이 있을시 업로드 데이터
    fun contentUpload() {
        // Make filename
        val now = Date()
        val timestamp = now.time
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA)
        val createdAt = sdf.format(timestamp)
        var imageFileName = "IMAGE_" + createdAt + "_.png"
        val contentDTO = ContentDTO()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val path = "${uid}_${System.currentTimeMillis()}"



        // 데이터 저장
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG)
                .show()
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                
                val name = SharedPreferenceFactory.getStrValue("userName", null)
                Log.d(Constants.TAG, "네임확인 : $name ")

                contentDTO.name = name
                // Insert downloadUrl of image
                contentDTO.imageUrl = uri.toString()

                contentDTO.pathData = path
                // Insert uid of user
                contentDTO.uid = auth?.currentUser?.uid

                // Insert userId
                contentDTO.userId = auth?.currentUser?.email

                // Insert explain of content
                contentDTO.explain = addphoto_edit_explain.text.toString()

                contentDTO.title = addphoto_edit_mamo.text.toString()
                
                // Insert timestamp
                contentDTO.timestamp = System.currentTimeMillis()
                
                firestore?.collection("images")?.document(path)?.set(contentDTO)


                setResult(Activity.RESULT_OK)

                finish()
            }
        }

    }
   
사진이 있을경우 데이터저장   



    //사진이 없을시  업로드 데이터
    fun contentUploadNoPhoto() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val path = "${uid}_${System.currentTimeMillis()}"
        val contentDTO = ContentDTO()
        val name = SharedPreferenceFactory.getStrValue("userName", null)
        Log.d(Constants.TAG, "네임확인 : $name ")
        //contentDTO 모델 리스트
                contentDTO.name = name
                // Insert downloadUrl of image
                contentDTO.imageUrl = "NullPhotoLink"
                // Insert uid of user
                contentDTO.uid = auth?.currentUser?.uid

                contentDTO.pathData = path

                // Insert userId
                contentDTO.userId = auth?.currentUser?.email

                // Insert explain of content
                contentDTO.explain = addphoto_edit_explain.text.toString()

                contentDTO.title = addphoto_edit_mamo.text.toString()

                // Insert timestamp
                contentDTO.timestamp = System.currentTimeMillis()

                contentDTO.imageUrl ="https://img.khan.co.kr/news/2020/06/11/l_2020061201001441700115431.jpg"

                firestore?.collection("images")?.document(path)?.set(contentDTO)


                setResult(Activity.RESULT_OK)

                finish()



    }   
    
사진이 없을경우 데이터저장   

# [게시글 업데이트(수정)] AddUpdateActivity   

   
![iage](https://im4.ezgif.com/tmp/ezgif-4-63c18f044f7b.gif)


사진이 있을때 
사진 변경유무 구분 처리

사진이 없을때도
사진 변경유무 구분 처리



<코드>

   

       //사진이 있을시 업로드 데이터
   
       fun contentUpload() {
         // Make filename
         val now = Date()
         val timestamp = now.time
         val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA)
         val createdAt = sdf.format(timestamp)
         var imageFileName = "IMAGE_" + createdAt + "_.png"

        // Callback method
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        //사진이 변경 구분
        when (photoUri) {
            //사진 변경을 안 했을 시
            null -> {

                val name = SharedPreferenceFactory.getStrValue("userName", null)
                Log.d(Constants.TAG, "네임확인 : $name ")
                contentDTO?.name = name

                contentDTO!!.let { it ->

                    it.uid = auth?.currentUser?.uid
                    // Insert userId
                    it.userId = auth?.currentUser?.email
                    it.explain = addphoto_edit_explain.text.toString()
                    it.title = addphoto_edit_mamo.text.toString()

                }
                Log.d("로그", "링크 ${contentDTO!!.pathData}")

                val mamoText = addphoto_edit_mamo.text.toString()
                val explainText = addphoto_edit_explain.text.toString()
                val update = firestore?.collection("images")?.document(contentDTO!!.pathData.toString())

                val mamoTextmap = HashMap<String, Any>()
                val explainTextmap = HashMap<String, Any>()
                mamoTextmap["title"] = mamoText
                explainTextmap["explain"] = explainText

                update?.set(contentDTO!!)?.addOnSuccessListener {
                    Log.d(Constants.TAG, "제목수정완료: ")
                    setResult(Activity.RESULT_OK)
                    finish()
                }

                update?.set(contentDTO!!)?.addOnSuccessListener {
                    Log.d(Constants.TAG, "내용수정완료: ")
                    setResult(Activity.RESULT_OK)
                    finish()
                }



                setResult(Activity.RESULT_OK)

                finish()

            }
            else -> {  //변경 했을시


                storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG)
                        .show()
                    storageRef.downloadUrl.addOnSuccessListener { uri ->

                        val name = SharedPreferenceFactory.getStrValue("userName", null)
                        Log.d(Constants.TAG, "네임확인 : $name ")

                        contentDTO?.name = name

                        // Insert downloadUrl of image
                        contentDTO?.imageUrl = uri.toString()

                        contentDTO?.let { it ->
                            // Insert uid of user
                            it.uid = auth?.currentUser?.uid
                            // Insert userId
                            it.userId = auth?.currentUser?.email
                            // Insert explain of content
                            it.explain = addphoto_edit_explain.text.toString()

                            it.title = addphoto_edit_mamo.text.toString()

                        }



                        firestore?.collection("images")?.document(contentDTO?.pathData.toString())
                            ?.set(contentDTO!!)
                            ?.addOnSuccessListener {
                                hideAndShowUi(false)
                                setResult(Activity.RESULT_OK)
                                finish()
                            }?.addOnFailureListener {
                                uploadErrorMessage(it)
                            }
                    }

                }
            }


        }
   
1. 사진이 있을시   
   




        //사진이 없을시  업로드 데이터
        fun contentUploadNoPhoto() {


        // Make filename
        val now = Date()
        val timestamp = now.time
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA)
        val createdAt = sdf.format(timestamp)
        val contentDTO = ContentDTO()
        val email = FirebaseAuth.getInstance().currentUser?.email
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val name = SharedPreferenceFactory.getStrValue("userName", null)
        Log.d(Constants.TAG, "네임확인 : $name ")
        contentDTO.name = name

        contentDTO.let {
            // Insert downloadUrl of image

            it.imageUrl = "NullPhotoLink"
            // Insert uid of user
            it.uid = auth?.currentUser?.uid

            // Insert userId
            it.userId = auth?.currentUser?.email

            // Insert explain of content
            it.explain = addphoto_edit_explain.text.toString()

            it.title = addphoto_edit_mamo.text.toString()



            it.imageUrl =
                "https://img.khan.co.kr/news/2020/06/11/l_2020061201001441700115431.jpg"

        }   
         
   2. 사진이 없을시     
         
            
                
 SNS 부가기능
-------------
  

# [좋아요 힘내요 및 팔로우]

# [좋아요,힘내요]. HomeViewRecyclerViewAdapter   
![image](https://im4.ezgif.com/tmp/ezgif-4-6fb8bd15f310.gif)
  







<코드>   
   
# [좋아요,힘내요] 구문   
   
    //좋아요 이벤트 기능
    private fun favoriteEvent(position: Int) {
        val tsDoc = firestore?.collection("images")?.document(contentUidList[position])
        firestore?.runTransaction { transaction ->

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

            if (contentDTO!!.favorites.containsKey(uid)) {
                // When the button is clicked
                contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                contentDTO.favorites.remove(uid)
            } else {
                // When the button is not clicked
                contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                contentDTO.favorites[uid] = true
                favoriteAlarm(contentArray[position].uid!!)
            }
            transaction.set(tsDoc, contentDTO)
        }
    }


    //힘내요 이벤트 기능
    private fun meaningEvent(position: Int) {
        val tsDoc = firestore?.collection("images")?.document(contentUidList[position])
        firestore?.runTransaction { transaction ->

            val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
            val uid = FirebaseAuth.getInstance().currentUser!!.uid

            if (contentDTO!!.meaning.containsKey(uid)) {

                // When the button is clicked
                contentDTO.meaningCount = contentDTO.meaningCount - 1
                contentDTO.meaning.remove(uid)
            } else {

                // When the button is not clicked
                contentDTO.meaningCount = contentDTO.meaningCount + 1
                contentDTO.meaning[uid] = true

                meaningAlarm(contentArray[position].uid!!)
            }
            transaction.set(tsDoc, contentDTO)
        }

    }   
    
       
#  [팔로우 구문] .CustomBottomDialog   

       
    
     //팔로우
    fun requestFollow() {

        // Save data to my account

        var tsDocFollowing = firestore!!.collection("users").document(currentUserUid!!)
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uid!!] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction

            }


            if (followDTO?.followings?.containsKey(uid)!!) {
                // It remove following third person when a third person follow me
                followDTO?.followingCount = followDTO?.followingCount - 1
                followDTO?.followings.remove(uid)
            } else {
                // It remove following third person when a third person not follow me
                followDTO?.followingCount = followDTO?.followingCount + 1
                followDTO?.followings[uid!!] = true
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }

        // Save data to third person
        var tsDocFollower = firestore!!.collection("users").document(uid!!)
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true

                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }

            if (followDTO!!.followers.containsKey(currentUserUid!!)) {
                // It cancel my follower when I follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid!!)
            } else {
                // It cancel my follower when I don't follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true
                followerAlarm(uid)
            }
            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }


    }

유저 정보 
-------------   
   
# [닉네임 변경] FirstVisitActivity    
    
![image](https://im4.ezgif.com/tmp/ezgif-4-1526738fafeb.gif)   
   
      
  

   
   
        // 파이어베이스 데이터 조회후 닉네임 변경
        private fun updateData() {

        SharedPreferenceFactory.getStrValue("userToken", "")  // 유저 uid
        val path = SharedPreferenceFactory.getStrValue("userPath", "")  // 유저 디비 위치)
        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        var contentDTO = ContentDTO()
        var map = HashMap<String, Any>()
        val nameing = name_edit.text.toString()
        var email = FirebaseAuth.getInstance().currentUser?.email
        Log.d(Constants.TAG, "uid = $uid")
        map["name"] = nameing
        

        if (path != null) {
            firestore?.collection("user_auth")?.document(path)?.update(map)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {

                        SharedPreferenceFactory.putStrValue("userName", nameing)   // 유저 닉네임
                        Toast.makeText(this, "닉네임이 변경 되었습니다", Toast.LENGTH_SHORT).show()
                        finish()


                    }
                }
        }
        if (path != null) {
        FirebaseFirestore.getInstance().collection("profileName").document(uid).set(map)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {

                    SharedPreferenceFactory.putStrValue("userMainName", nameing)   // 유저 닉네임
                    finish()


                }
            }
        }


    }
  
  
        
파이어 베이스에서 유저의 데이터를 조회 해서 
유저 닉네임을 변경   
   
      
      
  
 Fcm 알림 서비스 
-------------
# [Fcm 알림] FcmPush 

![image](https://im4.ezgif.com/tmp/ezgif-4-2dc6faf27b11.gif)

   
   
      
    fun sendMessage(destinationUid: String, title: String, message: String) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var token = task.result!!["pushToken"].toString()
                println(token)
                var pushDTO = PushDTO()
                pushDTO.to = token
                pushDTO.notification?.title = title
                pushDTO.notification?.body = message

                var body = RequestBody.create(JSON, gson?.toJson(pushDTO))
                var request = Request
                    .Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "key=" + serverKey)
                    .url(url)
                    .post(body)
                    .build()
                okHttpClient?.newCall(request)?.enqueue(object : Callback {
                    override fun onFailure(request: Request?, e: IOException?) {
                    }

                    override fun onResponse(response: Response?) {
                        println(response?.body()?.string())
                    }
                })
            }
        }
    }
       
          
          
서버 키를 가져와
JSON 파싱후  서버에 알림송출   
       
 # [좋아요,힘내요,댓글,팔로우 알림함수에 추가]
  
  좋아요  
  
     var message = alarmDTO.name + "님이 좋아요를 눌렀습니다"
        fcmPush?.sendMessage(destinationUid, "알림 메시지 입니다", message)
  힘내요  
  
     var message = alarmDTO.name + "님이 힘내요를 눌렀습니다"
        fcmPush?.sendMessage(destinationUid, "알림 메시지 입니다", message)
  팔로우
  
    var message = alarmDTO.name + getString(R.string.alarm_follow)
        fcmPush?.sendMessage(destinationUid!!, "알림 메시지 입니다", message)
  댓글
  
    var message = alarmDTO.name + getString(R.string.alarm_who) + message + "댓글을 남기셨습니다."
        fcmPush?.sendMessage(destinationUid, "알림 메시지 입니다", message)
        

