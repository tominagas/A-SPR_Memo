package com.example.tominaga.simplememo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

/**
 * Created by tominaga on 2017/11/25.
 */
class MemoDetailFragment: Fragment() {

    var memoDetailView: View? = null
    var position: Int = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        memoDetailView = inflater?.inflate(R.layout.memo_detail_fragment, container, false)
        // bundle（更新情報）がある場合は情報をセット
        if(arguments != null){
            memoDetailView?.findViewById<EditText>(R.id.edit_text)?.setText(arguments.getString(MainActivity.updateMemoKey), TextView.BufferType.NORMAL)
            position = arguments.getInt(MainActivity.updateMemoPositionKey)

        }
        return memoDetailView
    }

    // メモ本文入力画面で入力された文字列を返す
    fun getMemoDetailText() : String {
        val edit = memoDetailView?.findViewById<EditText>(R.id.edit_text)
        return edit?.text.toString()
    }

    // 更新するメモのpositionを返す
    fun getMemoPosition() : Int {
        return position
    }

}