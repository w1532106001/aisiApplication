package com.rance.aisiapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rance.aisiapplication.R
import com.rance.aisiapplication.model.PicturesSet
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jsoup.Connection
import org.jsoup.Jsoup

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    var picturesSetList = arrayListOf<PicturesSet>()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val job = GlobalScope.launch {
            val time = System.currentTimeMillis()
           val document =  Jsoup.connect("https://www.24tupian.org/meinv").ignoreContentType(false)
               .method(Connection.Method.GET)
               .execute().body()
            picturesSetList = PicturesSet.htmlToPicturesSetList(document) as ArrayList<PicturesSet>
            picturesSetList.forEach {
                it.parsePicturesSet()
            }
            println("总时长${System.currentTimeMillis()-time}")
        }

    }
}