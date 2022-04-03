package com.example.task20

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.task20.databinding.FragmentObserverBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ObserverFragment : Fragment() {
    private lateinit var binding: FragmentObserverBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentObserverBinding.inflate(inflater, container, false)

        binding.btnSubscribe.setOnClickListener {
            RestAPIFragment.observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.tvText.text = it
                    println(it)
                }, {
                    binding.tvText.text = it.message
                }, {
                    binding.tvText.text = "Completed"
                })
        }

        binding.btnPrevFragment.setOnClickListener {
            findNavController().navigate(R.id.restAPIFragment)
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ObserverFragment()
    }
}