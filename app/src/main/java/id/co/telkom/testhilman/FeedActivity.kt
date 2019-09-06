package id.co.telkom.testhilman

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hackernews.api.entity.Feed
import hackernews.api.entity.service.FeedService
import id.co.telkom.testhilman.databinding.ActivityFeedBinding
import id.co.telkom.testhilman.databinding.ActivityFeedCommentBinding
import id.co.telkom.testhilman.databinding.ActivityMainGridBinding
import id.co.telkom.testhilman.module.DaggerHackerNewsComponent
import kotlinx.android.synthetic.main.activity_feed.*
import java.io.Serializable
import java.lang.Exception
import javax.inject.Inject

class FeedActivity : AppCompatActivity() {
    lateinit var feed: Feed;
    lateinit var mBinding: ActivityFeedBinding;
    lateinit @Inject
    var feedService: FeedService;
    lateinit var adapter:CommentAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerHackerNewsComponent.builder().build().inject(this);
        feed = intent.getSerializableExtra("EXTRA_DATA") as Feed
        adapter = CommentAdapter(feedService, layoutInflater, this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_feed);
        mBinding.feed = feed;
        mBinding.act = this;
        mBinding.recycler.layoutManager = LinearLayoutManager(this)
        mBinding.recycler.adapter = adapter
        setSupportActionBar(toolbar)
        adapter.listFeeds = feed.kids
        adapter.notifyDataSetChanged()
    }

    fun toggleFavorite() {
        feed.favorite = !feed.favorite
        mBinding.feed = feed

    }


    class CommentAdapter(feedService: FeedService, inflater: LayoutInflater, act: Activity) :
        RecyclerView.Adapter<CommentViewHolder>() {


        var listFeeds: List<Long> = ArrayList<Long>();
        val feedService: FeedService;
        val inflater: LayoutInflater;
        val act: Activity;

        init {
            this.feedService = feedService;
            this.inflater = inflater;
            this.act = act;
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
            val v = inflater.inflate(R.layout.activity_feed_comment, parent, false);
            return CommentViewHolder(v, act);
        }

        override fun getItemCount(): Int {
            return listFeeds.size
        }

        override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
            feedService.getFeed(listFeeds[position], FeedViewHolderCallback(holder, position));
        }

        class FeedViewHolderCallback(holder: CommentViewHolder, position: Int) : FeedService.FeedFetchCallback {
            val holder: CommentViewHolder;
            val position: Int;


            init {
                this.holder = holder;
                this.position = position;
            }

            override fun onGetFeed(p0: Feed?) {

                holder.bind(p0!!, position);
            }

            override fun onErrorGetFeed(p0: Exception?) {
                Log.e("XLOG", "ERR ", p0);
                holder.error(p0!!)
            }

        }

    }

    class CommentViewHolder(itemView: View, act: Activity) : RecyclerView.ViewHolder(itemView) {
        val mBinding: ActivityFeedCommentBinding;
        val act: Activity;

        init {
            mBinding = DataBindingUtil.bind(itemView)!!;
            this.act = act;
        }

        fun bind(feed: Feed, position: Int) {
            Log.e("XLOG", "BIND " + feed.text)
            mBinding.data = feed;
            mBinding.holder = this;
            mBinding.position = position;
            mBinding.state = MainActivity.NO_ERROR;

        }

        fun startDetail(position: Int, feed: Feed) {
            var i = Intent(act, FeedActivity::class.java);
            i.putExtra("EXTRA_POSITION", position)
            i.putExtra("EXTRA_DATA", feed)
            act.startActivity(i)
        }

        fun error(feed: Exception) {
            Log.e("XLOG", "ERR ", feed);
            mBinding.state = MainActivity.ERROR;
        }
    }

}
