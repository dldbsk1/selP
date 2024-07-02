package com.example.seldoc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.seldoc.databinding.ItemPharmBinding

class MainViewHolder(val binding: ItemPharmBinding) : RecyclerView.ViewHolder(binding.root)

class MainAdapter(val datas: MutableList<PharmacyItem>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MainViewHolder(
            ItemPharmBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MainViewHolder).binding
        val model = datas?.get(position)

        binding.yadmNm.text = model?.yadmNm ?: "N/A"
        binding.addr.text = model?.addr ?: "N/A"
        binding.clCdNm.text = model?.clCdNm ?: "N/A"
        binding.telno.text = model?.telno ?: "N/A"
    }
}
