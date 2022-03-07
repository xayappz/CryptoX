package com.xayappz.cryptox.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
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
import com.xayappz.cryptox.SearchingHM
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
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private lateinit var _coin_ViewModel: CoinViewModel
    private val _retrofitService = ApiInterfaceService.getInstance()
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private var _responseCoinsData = ArrayList<Data?>()
    private var _searchCoinsData = ArrayList<Data?>()
    private var searchDatalist = ArrayList<String?>()
    private var progressDialog: ProgressDialog? = null
    val searchHM: SearchingHM<String, Data?> = SearchingHM<String, Data?>()
    val dataHM: HashMap<String, String?> =
        HashMap<String, String?>()
    var retry = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _coin_ViewModel = ViewModelProvider(
            this,
            CoinVMFactory(RepositoryCoin(_retrofitService))
        )[CoinViewModel::class.java]
        activity?.actionBar?.setDisplayShowHomeEnabled(false)
        activity?.actionBar?.setDisplayShowTitleEnabled(false)
        loaderShowDialog()

        loadDataFromApi()


    }

    private fun searchData() {
        var yes = false
        _binding?.searchcoin?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {

                checkList(query)

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.equals("")) {
                    setAdapter(_responseCoinsData)
                } else {
                    checkList(newText)

                }
                return false
            }
        })

        _binding?.searchcoin?.setOnClickListener {

            yes = if (!yes) {
                _coin_ViewModel.isSearchEnabled(true)
                true
            } else {
                _coin_ViewModel.isSearchEnabled(false)
                false

            }
        }

        _binding?.searchcoin?.setOnSearchClickListener(View.OnClickListener {
            _binding?.logoiV?.visibility = View.GONE
            _binding?.hamIV?.visibility = View.GONE
        })



        _binding?.searchcoin?.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                _binding?.logoiV?.visibility = View.VISIBLE
                _binding?.hamIV?.visibility = View.VISIBLE
                _searchCoinsData.clear()
                setAdapter(_responseCoinsData)
                return false
            }
        })
    }

    private fun checkList(newText: String) {

        _searchCoinsData.clear()
        _responseCoinsData.forEach {

            if (it?.name?.lowercase(Locale.getDefault())
                    ?.contains(newText.lowercase(Locale.getDefault()))!!
                ||
                it?.symbol?.lowercase(Locale.getDefault()).contains(
                    (newText.lowercase(Locale.getDefault())
                            )
                )

            ) {

            _searchCoinsData.add(it)
            setAdapter(_searchCoinsData)
        }
//
//
        }
    }


    private fun loadDataFromApi() {
        searchData()
        _coin_ViewModel.getAllCoinsReponse()

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
                    searchHM.clear()
                    for (data in _coin_ViewModel.coinListData.value?.getDataCoin()!!) {
                        searchHM.put(data?.name.toString(), data)
                        searchHM.put(data?.symbol.toString(), data)
                    }

                    searchDatalist.clear()
                    for (key in searchHM.keys) {
                        dataHM.put(key, null)
                        searchDatalist.add(key)
                    }
                    setAdapter(_responseCoinsData)

                } else {
                    if (!Util_Connection.isOnline(this.requireContext())) {
                        retry("No Internet")  // error
                    } else {
                        if (!_searchCoinsData.isEmpty()) {
                            retry("Api Error")  //api error

                        }
                    }

                }


            })

    }


    private fun loaderShowDialog() {
        progressDialog =
            ProgressDialog(this@HomeFragment.requireContext(), R.style.AppCompatAlertDialogStyle)
        progressDialog?.setTitle("Please wait")
        progressDialog?.setCancelable(false)
        progressDialog?.setMessage("Loading Data")
        progressDialog?.show()
    }


    private fun retry(error: String) {
        progressDialog?.cancel()
        _binding?.fab?.visibility = View.VISIBLE
        _binding?.errorTV?.visibility = View.VISIBLE
        _binding?.errorTV?.text = error
        binding.fab.setOnClickListener {
            if (Util_Connection.isOnline(this.requireContext())) {
                _binding?.fab?.visibility = View.GONE
                _binding?.errorTV?.visibility = View.GONE
                loaderShowDialog()
                loadDataFromApi()

            } else {
                retry("No Internet")
            }
        }

    }


    private fun setAdapter(data: ArrayList<Data?>) {
        lifecycleScope.launch(Dispatchers.Main) {
            progressDialog?.cancel()
        }

        _binding?.CryptoRV?.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = CoinAdapter(this.context, data)
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