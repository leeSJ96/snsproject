package com.sjkorea.meetagain.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

// 에딧 텍스트에 대한 익스텐션
fun EditText.onMyTextChanged(completion : (Editable?) -> Unit){

    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(editable: Editable?) {
            completion(editable)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    })

}