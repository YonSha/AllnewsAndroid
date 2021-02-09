package edu.yonatan.allnews.data_source;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.yonatan.allnews.activitys.SomethingWentWrong;
import edu.yonatan.allnews.models.News;
import edu.yonatan.allnews.adapters.NewsAdapter;

public class DataSourceMako extends AsyncTask<Void, Void, List<News>> {

    private WeakReference<ProgressBar> progressBar;
    private WeakReference<RecyclerView> recyclerView;
    private String address;
    private Context context;


    public DataSourceMako( RecyclerView recyclerView, String address, Context context, ProgressBar progressBar) {

        this.recyclerView = new WeakReference<>(recyclerView);
        this.address = address;
        this.context = context;
        this.progressBar = new WeakReference<>(progressBar);
    }


    @Override
    protected List<News> doInBackground(Void... voids) {
        List<News> myNews = new ArrayList<>();

        try {
            Document document = Jsoup.connect(address).get();
            Elements itemList = document.getElementsByTag("item");

            for (Element element : itemList) {

                String title = "";
                String descriptionHTML = "";
                String excerpt = "";


                title = element.getElementsByTag("title").first().text().trim();

                descriptionHTML = element.getElementsByTag("description").first().text();
                Document description = Jsoup.parse(descriptionHTML);
                excerpt = description.text().trim();
                String img = description.getElementsByTag("img").attr("src");

                String link = element.getElementsByTag("link").first().text().trim();


                News aNews = new News(title, excerpt, img, link);

                myNews.add(aNews);

            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return myNews;
    }


    @Override
    protected void onPostExecute(List<News> news) {
        super.onPostExecute(news);

        ProgressBar pb = this.progressBar.get();
        if(pb==null)return;
        pb.setVisibility(View.GONE);

        RecyclerView recycler = this.recyclerView.get();

        recycler.setAdapter(new NewsAdapter(news, context));
        recycler.setLayoutManager(new LinearLayoutManager(recycler.getContext()));

//        if (news == null) {
//            Intent swr = new Intent(context, SomethingWentWrong.class);
//            context.startActivity(swr);
//        } else {
//
//            recycler.setAdapter(new NewsAdapter(news, context));
//            recycler.setLayoutManager(new LinearLayoutManager(recycler.getContext()));
//        }

    }

}

