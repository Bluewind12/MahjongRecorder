package momonyan.mahjongrecorder

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //検索
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                // 検索キーが押下された
                Log.d("Search", "submit text: $text")
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                // テキストが変更された
                Log.d("Search", "change text: $text")
//                val itemListFragment = fragmentManager.findFragmentById(R.id.container)
//                if (itemListFragment is ItemListFragment && text != null) {
//                    itemListFragment.searchRequest(text)
//                }
                return false
            }

        })
        searchView.queryHint = "検索文字を入力して下さい。"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuPlayerData -> {
                //TODO プレイヤー個人の成績を出力するページへ（グラフとかあるといいかもしれぬ）
                Log.d("Menu",item.itemId.toString())
                return true
            }
            R.id.menuResult -> {
                //TODO 存在するプレイヤーの成績を簡易出力
                Log.d("Menu",item.itemId.toString())
                return true
            }
            R.id.menuPrivacy -> {
                //TODO プライバシーポリシーに飛ばす
                Log.d("Menu",item.itemId.toString())
                return true
            }
            R.id.menuReview -> {
                //TODO レビューへ飛ばす
                Log.d("Menu",item.itemId.toString())
                return true
            }
        }
        return true
    }
}
