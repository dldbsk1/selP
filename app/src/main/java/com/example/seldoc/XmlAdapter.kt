package com.example.seldoc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.seldoc.databinding.ItemMainBinding

class XmlViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class XmlAdapter(val datas: MutableList<myXmlItem>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return XmlViewHolder(
            ItemMainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as XmlViewHolder).binding
        val model = datas?.get(position)

        binding.itemName.text = model?.itemName ?: "N/A"
        binding.entpName.text = model?.entpName ?: "N/A"
        binding.efcyQesitm.text = model?.efcyQesitm ?: "N/A"
        binding.useMethodQesitm.text = model?.useMethodQesitm ?: "N/A"
        binding.atpnQesitm.text = model?.atpnQesitm ?: "N/A"
        binding.atpnWarnQesitm.text = model?.atpnWarnQesitm ?: "N/A"
        binding.seQesitm.text = model?.seQesitm ?: "N/A"
        binding.intrcQesitm.text = model?.intrcQesitm ?: "N/A"
        binding.depositMethodQesitm.text = model?.depositMethodQesitm ?: "N/A"

        //model.itemImage
        Glide.with(binding.root)
            .load(model?.itemImage)
            .override(400, 300)
            .into(binding.itemImage)
    }
}
