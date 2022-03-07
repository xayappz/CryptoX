package com.xayappz.cryptox.adapters

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.xayappz.cryptox.BR
import com.xayappz.cryptox.R
import com.xayappz.cryptox.databinding.RecyclerItemBinding
import com.xayappz.cryptox.models.Data
import java.math.BigDecimal
import java.text.DecimalFormat

class CoinAdapter(var context: Context, var list: ArrayList<Data?>) :
    RecyclerView.Adapter<CoinAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecyclerItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.recycler_item,
            parent,
            false
        )

        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return list.size


    }

    // Bind data
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val df = DecimalFormat("#,###.00")

        holder.binding.fullname.text = list.get(position)?.name
        holder.binding.shortname.text = list.get(position)?.symbol
        holder.binding.price.text =
            "$" + df.format(BigDecimal(list.get(position)?.quote?.USD?.price.toString()))
        var changeP = list.get(position)?.quote?.USD?.percent_change_24h?.toDouble()
        holder.binding.priceChange.text =
            changeP.toString() + "%"

        if (changeP != null) {
            if (changeP < 0) {
                holder.binding.updownIV.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_trending_down_24))
                holder.binding.priceChange.setTextColor(context.getColor(R.color.design_default_color_error))
            } else {


                holder.binding.updownIV.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_trending_up_24))
                holder.binding.priceChange.text =
                    "+" + changeP.toString() + "%"
                holder.binding.priceChange.setTextColor(context.getColor(R.color.green))

            }
        }

    }

    // Creating ViewHolder
    class ViewHolder(val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Any) {
            binding.setVariable(
                BR._all,
                data
            ) //BR - generated class; BR.user - 'user' is variable name declared in layout
            binding.executePendingBindings()
        }
    }

}