package com.example.tominaga.simplememo

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    // メモ入力画面（Fragment）を識別するタグ
    val fragmentTag = "MEMO_DETAIL_FRAGMENT"

    // アプリの環境設定にメモ一覧を書き込む時に必要なタグ
    val memoListTag = "MEMO_LIST"

    // アプリの環境設定を保持する領域
    // lateinit = コード上で必ず初期化されることが保証されているなら、一旦初期化なしで宣言することを許される。
    lateinit var sharedPreferences: SharedPreferences

    // 作成したメモのリスト
    var memos = ArrayList<String>()

    // 型のうしろに "?" をつけるとnull許容型として、nullで初期化することができる。
    var listView: ListView? = null

    // アプリ立ち上げ後、まずここの処理が走る
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MainActivityで表示するレイアウトを指定
        setContentView(R.layout.activity_main)

        // アプリの環境設定領域を使用することを宣言、初期化。
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)

        // activity_main.xmlに含まれる、ツールバーをid指定して取ってきてインスタンス化
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        listView = findViewById(R.id.list_view)

        // リストビューに表示されてる項目を長押しした時の動作を設定。
        listView?.setOnItemLongClickListener { parent, view, position, id ->

            // AlertDialog: タイトル・メッセージ・ボタンをもつ一般的なダイアログを作ることができる。
            // setPositionButton、setNegativeButtonの第2引数には
            // ボタンを押した時の動作を指定。なにも動作させない場合はnullを渡す。
            AlertDialog.Builder(this)
                    .setTitle("削除確認")
                    .setMessage("このメモを削除しますか？")
                    .setPositiveButton("OK") { dialog, which ->
                        memos.removeAt(position)
                        updateMemoList()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()

            // 長押し操作に対するイベントを有効にするため、最終的にtrueをreturn。
            true
        }

        // Activityに対してツールバーを設定する。これでツールバーでの操作がActivityに影響を与えられるようになる
        setSupportActionBar(toolbar)

        // activity_main.xmlに含まれる、フローティングアクションボタンをインスタンス化
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        // フローティングアクションボタンに対して、タップ時の動作を設定する。
        fab.setOnClickListener { view ->
            val transaction = supportFragmentManager.beginTransaction()

            if (supportFragmentManager.fragments.isEmpty()) {
                // メモ入力画面に切り替え
                (view as FloatingActionButton).setImageDrawable(applicationContext.getDrawable(R.drawable.ic_check))

                val fragment = MemoDetailFragment()
                transaction.add(R.id.fragment_container, fragment, fragmentTag)
                transaction.commit()

            } else {
                // メモ入力画面から一覧画面に切り替え
                (view as FloatingActionButton).setImageDrawable(applicationContext.getDrawable(R.drawable.ic_add))

                val fragment = supportFragmentManager.findFragmentByTag(fragmentTag) as MemoDetailFragment
                memos.add(fragment.getMemoDetailText())

                updateMemoList()
                transaction.remove(fragment)
                transaction.commit()
            }
        }

        // activity_main.xmlのListViewをセットアップしていく
        setupListView()
    }

    // ツールバー右上のオプションメニューを追加する処理。
    // 今回はオプションメニュー使わないので、falseをreturn。
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return false
    }

    // オプションメニューのどれかをタップした時にここの処理が走る
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    private fun setupListView() {
        // GsonライブラリがKotlin対応してないため、この1行が必要。正直おまじない程度な認識。
        val type = object : TypeToken<ArrayList<String>>(){}.type

        val savedMemoListJson = sharedPreferences.getString(memoListTag, "")

        // Gsonライブラリで、Json（String型）をArrayList（memosの型）にパース。
        // 第1引数: Json。パース元になるソース。
        // 第2引数: パース先の型を指定。今回はArrayList型。
        if (savedMemoListJson.isNotEmpty()) {
            memos = Gson().fromJson(savedMemoListJson, type)
        }

        // ListViewで実際に表示するデータ群を、このArrayAdapterで指定する。
        // ArrayAdapter<String> ＝ String（文字列）型のデータを持つAdapterである、というのを指定してあげる
        // 第1引数: this = Contextを渡す。AppCompatActivityを継承しているMainActivityクラスなら「this」で渡すことができる
        // 第2引数: リストの1つのレイアウトを指定。ここでは自分で作ったレイアウトを渡してるが、Androidプリセットのレイアウトも用意されている。
        // 第3引数: memos = ListViewに表示するデータ群を指定。
        val adapter = ArrayAdapter<String>(this, R.layout.list_item, memos)

        // ListViewに、上で作ったAdapterをセット。これでメモ一覧が表示されるようになる
        listView?.adapter = adapter
    }

    // アプリの情報保持領域に、現在のメモ一覧全てを保存・上書き。その後リストビューを更新する
    private fun updateMemoList() {
        val editor = sharedPreferences.edit()
        editor.putString(memoListTag, Gson().toJson(memos))
        editor.apply()

        setupListView()
    }
}
