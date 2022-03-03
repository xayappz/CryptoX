package com.xayappz.cryptox.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.common.util.concurrent.ListenableFuture
import com.xayappz.cryptox.CoinVMFactory
import com.xayappz.cryptox.R
import com.xayappz.cryptox.adapters.CoinAdapter
import com.xayappz.cryptox.apiinterfaces.ApiInterfaceService
import com.xayappz.cryptox.databinding.FragmentFirstBinding
import com.xayappz.cryptox.models.Data
import com.xayappz.cryptox.network.GetCoinsWM
import com.xayappz.cryptox.network.Util_Connection
import com.xayappz.cryptox.repository.RepositoryCoin
import com.xayappz.cryptox.viewmodels.CoinViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


class FirstFragment : Fragment() {
    lateinit var _coin_ViewModel: CoinViewModel
    private val _retrofitService = ApiInterfaceService.getInstance()
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private var anyError = Boolean
    private var _responseCoinsData = ArrayList<Data?>()
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _coin_ViewModel =
            ViewModelProvider(this, CoinVMFactory(RepositoryCoin(_retrofitService))).get(
                CoinViewModel::class.java
            )
        loaderShowDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadDataFromApi()
    }

    private fun loadDataFromApi() {

        lifecycleScope.launch(Dispatchers.IO) {

            _coin_ViewModel.getAllCoinsReponse()

            withContext(Dispatchers.Main)
            {

                _coin_ViewModel.coinListData.observe(
                    this@FirstFragment.requireActivity(),
                    Observer {

                        if (_coin_ViewModel.coinListData.value != null) {
                            if (it.getStatus()?.error_code == 0) {
                                for (data in it.getDataCoin()!!) {
                                    _responseCoinsData.add(data)

                                }
                                setAdapter()
                            }

                        } else {
                            retry("Api Error")  //api error

                        }


                    })
                _coin_ViewModel.errorMessage.observe(
                    this@FirstFragment.requireActivity(),
                    Observer {
                        _binding?.fab?.visibility = View.GONE
                        _binding?.errorTV?.visibility = View.GONE
                        if (it.contains(" No address")) {  //network error
                            retry("No Internet")
                        } else if (!it.equals("OK")) {           //server side error
                            retry("Some Error")

                        }

                    })


            }


        }


    }


    private fun loaderShowDialog() {
        progressDialog =
            ProgressDialog(this@FirstFragment.requireContext(), R.style.AppCompatAlertDialogStyle)
        progressDialog?.setTitle("Please wait")
        progressDialog?.setCancelable(false)
        progressDialog?.setMessage("Loading Data")
        progressDialog?.show()
    }


    private fun retry(error: String) {
        _binding?.fab?.visibility = View.VISIBLE
        _binding?.errorTV?.visibility = View.VISIBLE
        _binding?.errorTV?.text = error
        progressDialog?.cancel()
        binding.fab.setOnClickListener { view ->
            if (Util_Connection.isOnline(this.requireContext())) {
                loaderShowDialog()
                loadDataFromApi()

            } else {
                retry("No Internet")
            }
        }

    }


    private fun setAdapter() {
        lifecycleScope.launch(Dispatchers.Main) {
            progressDialog?.cancel()
        }

        _binding?.CryptoRV?.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = CoinAdapter(this.context, _responseCoinsData)
        }
        fetchDataWM()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                Log.d("periodicWorkRequest", "Status changed to ${it.state.isFinished}")

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