package com.example.task20

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.task20.databinding.FragmentRestAPIBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestAPIFragment : Fragment() {
    private lateinit var binding: FragmentRestAPIBinding
    private val BASE_URL = "https://jsonplaceholder.typicode.com/"
    private var bodyList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestAPIBinding.inflate(inflater, container, false)

        binding.btnNextFragment.setOnClickListener {
            findNavController().navigate(R.id.observerFragment)
        }

        binding.btnLoadUrl.setOnClickListener {
            if (bodyList.isEmpty()) {
                loadDataFromURL()
            } else {
                binding.tvLoadStatus.text = "The data has already been uploaded to the list"
            }
        }

        binding.btnLoadFile.setOnClickListener {
            if (bodyList.isEmpty()) {
                loadDataFromFile()
            } else {
                binding.tvLoadStatus.text = "The data has already been uploaded to the list"
            }
        }

        observable = Observable.create{ subscriber->
            for(body in bodyList){
                Thread.sleep(500)
                subscriber.onNext(body)
            }
            subscriber.onComplete()
        }

        return binding.root
    }

    private fun loadDataFromURL() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(apiInterface::class.java)

        val retrofitData = retrofitBuilder.getData()

        retrofitData.enqueue(object : Callback<List<MsgItem>?> {
            override fun onResponse(
                call: Call<List<MsgItem>?>,
                response: Response<List<MsgItem>?>
            ) {
                val responseBody = response.body()!!
                for (body in responseBody) {
                    bodyList.add(body.body)
                }
            }

            override fun onFailure(call: Call<List<MsgItem>?>, t: Throwable) {
                Log.d("Rest API", "onFailure: " + t.message)
            }
        })
    }

    private fun loadDataFromFile() {
        val jsonFileString = context?.assets?.open("posts.json")
            ?.bufferedReader().use { it?.readText() }
        val gson = Gson()
        val listMsgType = object : TypeToken<ArrayList<MsgItem>>() {}.type
        val msgList: ArrayList<MsgItem> = gson.fromJson(jsonFileString, listMsgType)
        for (msg in msgList) {
            bodyList.add(msg.body)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RestAPIFragment()
        lateinit var observable: Observable<String>
    }
}