package com.example.soft1c.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soft1c.R
import com.example.soft1c.databinding.ItemAcceptanceSizeBinding
import com.example.soft1c.extension.inflateLayout
import com.example.soft1c.model.ItemClicked
import com.example.soft1c.model.SizeAcceptance

class AcceptanceSizeAdapter(private val onItemClicked: (acceptanceSize: SizeAcceptance.SizeData, itemClicked: ItemClicked) -> Unit) :
    ListAdapter<SizeAcceptance.SizeData, AcceptanceSizeAdapter.AcceptanceSizeHolder>(
        AcceptanceSizeDiffUtil()) {

    class AcceptanceSizeDiffUtil : DiffUtil.ItemCallback<SizeAcceptance.SizeData>() {
        override fun areItemsTheSame(
            oldItem: SizeAcceptance.SizeData,
            newItem: SizeAcceptance.SizeData,
        ): Boolean = (oldItem == newItem)

        override fun areContentsTheSame(
            oldItem: SizeAcceptance.SizeData,
            newItem: SizeAcceptance.SizeData,
        ): Boolean = (oldItem.seatNumber == newItem.seatNumber && oldItem.weight == newItem.weight)

    }

    class AcceptanceSizeHolder(
        private val onItemClicked: (acceptanceSize: SizeAcceptance.SizeData, itemClicked: ItemClicked) -> Unit,
        view: View,
    ) : RecyclerView.ViewHolder(view) {

        private val itemBinding = ItemAcceptanceSizeBinding.bind(view)
        fun onBind(acceptance: SizeAcceptance.SizeData) {
            itemView.setOnClickListener {
                onItemClicked(acceptance, ItemClicked.SIZE_ITEM)
            }
            with(itemBinding) {
                txtSeatNumber.text = acceptance.seatNumber.toString()
                txtLength.text = acceptance.length.toString()
                txtWidth.text = acceptance.width.toString()
                txtHeight.text = acceptance.height.toString()
                txtWeight.text = String.format("%.6f", acceptance.weight)
            }
        }
    }

    override fun getItemCount(): Int = currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptanceSizeHolder {
        return AcceptanceSizeHolder(onItemClicked,
            parent.inflateLayout(R.layout.item_acceptance_size))
    }

    override fun onBindViewHolder(holder: AcceptanceSizeHolder, position: Int) {
        holder.onBind(currentList[position])
    }

}