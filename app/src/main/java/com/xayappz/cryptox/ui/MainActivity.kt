package com.xayappz.cryptox.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.xayappz.cryptox.CoinVMFactory
import com.xayappz.cryptox.apiinterfaces.ApiInterfaceService
import com.xayappz.cryptox.databinding.ActivityMainBinding
import com.xayappz.cryptox.models.Data
import com.xayappz.cryptox.repository.RepositoryCoin
import com.xayappz.cryptox.utils.Util_Connection
import com.xayappz.cryptox.viewmodels.CoinViewModel
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var _coin_ViewModel: CoinViewModel
    private val _retrofitService = ApiInterfaceService.getInstance()
    private var _responseCoinsData = ArrayList<Data?>()
    private var _searchCoinsData = ArrayList<Data?>()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _coin_ViewModel = ViewModelProvider(
            this,
            CoinVMFactory(RepositoryCoin(_retrofitService))
        )[CoinViewModel::class.java]

        loadDatafromApi()

        search()
    }

    private fun loadDatafromApi() {
        _coin_ViewModel.getData().observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                for (data in it.getDataCoin()!!) {
                    _responseCoinsData.add(data)
                }
            } else {
                if (!Util_Connection.isOnline(this)) {
                    retry("No Internet")  // error
                } else {
                    if (!_searchCoinsData.isEmpty()) {
                        retry("Api Error")  //api error

                    }
                }
                binding.errorLay.visibility = View.VISIBLE
            }


        })

    }

    private fun retry(error: String) {

        binding.errorTV.text = error
        binding.fab.setOnClickListener {
            if (Util_Connection.isOnline(this)) {
                binding.errorLay.visibility = View.GONE
                retrygetData()

            } else {
                retry("No Internet")
            }
        }

    }

    private fun retrygetData() {
        _coin_ViewModel.getAllCoinsReponse()
    }

    private fun search() {

        binding.searchcoin.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                checkList(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.equals("")) {
                    _coin_ViewModel.addSearchData(_responseCoinsData)
                } else {
                    checkList(newText)

                }
                return false
            }
        })

        binding.searchcoin.setOnSearchClickListener {
            binding.searchcoin.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            binding.searchcoin.requestLayout();
            binding.logoiV.visibility = View.GONE
            binding.hamIV.visibility = View.GONE

        }
        binding.searchcoin.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                binding.searchcoin.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                binding.searchcoin.requestLayout();
                binding.logoiV.visibility = View.VISIBLE
                binding.hamIV.visibility = View.VISIBLE

                _coin_ViewModel.addSearchData(_responseCoinsData)

                return false
            }
        })
    }

    private fun checkList(newText: String) {
        _searchCoinsData.clear()
        var searchValue = newText.lowercase(Locale.getDefault())
        var filteredSearchData = _responseCoinsData.filter {

            it?.name?.lowercase(Locale.getDefault())
                ?.contains(searchValue)!!
                    ||
                    it.symbol.lowercase(Locale.getDefault()).contains(
                        (searchValue)
                    )


        }
        _coin_ViewModel.addSearchData(filteredSearchData)

    }


}