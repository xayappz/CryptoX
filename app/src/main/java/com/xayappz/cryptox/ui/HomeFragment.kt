package com.xayappz.cryptox.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.common.util.concurrent.ListenableFuture
import com.xayappz.cryptox.CoinVMFactory
import com.xayappz.cryptox.adapters.CoinAdapter
import com.xayappz.cryptox.apiinterfaces.ApiInterfaceService
import com.xayappz.cryptox.databinding.FragmentFirstBinding
import com.xayappz.cryptox.models.Data
import com.xayappz.cryptox.network.GetCoinsWM
import com.xayappz.cryptox.repository.RepositoryCoin
import com.xayappz.cryptox.utils.Progress
import com.xayappz.cryptox.viewmodels.CoinViewModel
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment() {
    private lateinit var _coin_ViewModel: CoinViewModel
    private val _retrofitService = ApiInterfaceService.getInstance()
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private var _responseCoinsData = ArrayList<Data?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Progress.loadProgress(requireActivity())

        _coin_ViewModel = ViewModelProvider(
            requireActivity(),
            CoinVMFactory(RepositoryCoin(_retrofitService))
        )[CoinViewModel::class.java]
        activity?.actionBar?.setDisplayShowHomeEnabled(false)
        activity?.actionBar?.setDisplayShowTitleEnabled(false)


        loadDataFromApi()
        _coin_ViewModel.getSearchData().observe(requireActivity(), Observer { it ->
            if (it != null) {
                _responseCoinsData.clear()
                setAdapter(it)
            }

        })


    }


    private fun loadDataFromApi() {
        _coin_ViewModel.getData().observe(
            this.requireActivity(),
            Observer {
                if (_coin_ViewModel.getData().value != null) {
                    _responseCoinsData.clear()
                    if (it != null) {
                        if (it.getStatus()?.error_code == 0) {
                            for (data in it.getDataCoin()!!) {
                                _responseCoinsData.add(data)
                            }

                        }
                    }
                    setAdapter(_responseCoinsData)

                }


            })

    }


    private fun setAdapter(data: List<Data?>) {
        Progress.cancelDialog()

        _binding?.CryptoRV?.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = CoinAdapter(this.context, data)
        }
        //fetchDataWM()
    }

    fun fetchDataWM() {
        val request =
            PeriodicWorkRequest.Builder(GetCoinsWM::class.java, 15, TimeUnit.MINUTES).build()
        WorkManager.getInstance().enqueue(
            request
        )

        WorkManager.getInstance().getWorkInfoByIdLiveData(request.id).observeForever {

            if (it != null) {
                if (it.state.isFinished) {
                    _responseCoinsData.clear()
                    loadDataFromApi()
                }

            }
        }
        //isWorkScheduled(request.id.toString())
    }

    private fun isWorkScheduled(tag: String): Boolean {
        val instance = WorkManager.getInstance()
        val statuses: ListenableFuture<List<WorkInfo>> = instance.getWorkInfosByTag(tag)
        return try {
            var running = false
            val workInfoList: List<WorkInfo> = statuses.get()
            for (workInfo in workInfoList) {
                val state = workInfo.state
                running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }


}