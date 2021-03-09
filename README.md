# snsproject
sns

안녕하세요

    private fun moveNext(uid: String, email: String?, name: String?) {

        val authStore = FirebaseFirestore.getInstance().collection("user_auth")


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
    }
