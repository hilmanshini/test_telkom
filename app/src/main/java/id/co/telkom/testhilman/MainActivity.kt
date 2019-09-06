package id.co.telkom.testhilman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import hackernews.api.entity.Feed
import hackernews.api.entity.service.FeedService
import id.co.telkom.testhilman.databinding.ActivityMainBinding
import id.co.telkom.testhilman.databinding.ActivityMainGridBinding
import id.co.telkom.testhilman.module.DaggerHackerNewsComponent
import id.co.telkom.testhilman.module.HackerNewsComponent
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import javax.inject.Inject

class MainActivity : AppCompatActivity(), FeedService.ListIdFetchCallback, SwipeRefreshLayout.OnRefreshListener {
    companion object {
        const val ERROR = -1;
        const val NO_ERROR = 0;
        const val LOADING = -2;
    }


    lateinit var mBinding: ActivityMainBinding;
    lateinit var mNewsAdapter: NewsFeedAdapter;
    @Inject
    public lateinit var feedService: FeedService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerHackerNewsComponent.builder().build().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.state = NO_ERROR;
        mNewsAdapter = NewsFeedAdapter(feedService,layoutInflater,this);
        mBinding.swipeRefresh.setOnRefreshListener(this);
        recycler.layoutManager = GridLayoutManager(this, 2);
        recycler.adapter = mNewsAdapter
    }

    override fun onRefresh() {
        mBinding.state = NO_ERROR;
        feedService.getTopStories(this)
        mBinding.state = LOADING;

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        feedService.getTopStories(this)
        mBinding.state = LOADING;
    }

    override fun onErrorGetListIds(p0: Exception?) {
        mBinding.state = ERROR;
    }

    override fun onGetListIds(p0: MutableList<Long>?) {
        mBinding.swipeRefresh.isRefreshing = false;
        //TIDAK ADA PAGINATION?
        mBinding.state = NO_ERROR;
        mNewsAdapter.listFeeds += p0!!.toList();
        mNewsAdapter.notifyDataSetChanged()


    }

    class NewsFeedAdapter(feedService: FeedService, inflater: LayoutInflater,act:MainActivity) : RecyclerView.Adapter<NewsFeedViewHolder>() {


        var listFeeds: List<Long> = ArrayList<Long>();
        val feedService: FeedService;
        val inflater: LayoutInflater;
        val act:MainActivity;

        init {
            this.feedService = feedService;
            this.inflater = inflater;
            this.act = act;
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsFeedViewHolder {
            val v = inflater.inflate(R.layout.activity_main_grid, parent, false);
            return NewsFeedViewHolder(v,act);
        }

        override fun getItemCount(): Int {
            return listFeeds.size
        }

        override fun onBindViewHolder(holder: NewsFeedViewHolder, position: Int) {
            feedService.getFeed(listFeeds[position], FeedViewHolderCallback(holder,position));
        }

        class FeedViewHolderCallback(holder: NewsFeedViewHolder,position: Int) : FeedService.FeedFetchCallback {
            val holder: NewsFeedViewHolder;
            val position: Int;


            init {
                this.holder = holder;
                this.position = position;
            }

            override fun onGetFeed(p0: Feed?) {

                holder.bind(p0!!,position);
            }

            override fun onErrorGetFeed(p0: Exception?) {
                Log.e("XLOG","ERR ",p0);
                holder.error(p0!!)
            }

        }

    }

    class NewsFeedViewHolder(itemView: View,act:MainActivity) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ActivityMainGridBinding;
        val act:MainActivity;
        init {
            mBinding = DataBindingUtil.bind(itemView)!!;
            this.act = act;
        }

        fun bind(feed: Feed,position: Int) {
            Log.e("XLOG","BIND "+feed.text)
            mBinding.data = feed;
            mBinding.holder = this;
            mBinding.position = position;
            mBinding.state = NO_ERROR;

        }

        fun  startDetail(position: Int,feed:Feed){
            var i = Intent(act,FeedActivity::class.java);
            i.putExtra("EXTRA_POSITION",position)
            i.putExtra("EXTRA_DATA",feed)
            act.startActivityForResult(i,4242)
        }

        fun error(feed: Exception) {
            Log.e("XLOG","ERR ",feed);
            mBinding.state = ERROR;
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
