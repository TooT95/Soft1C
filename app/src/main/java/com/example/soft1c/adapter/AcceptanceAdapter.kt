package com.example.soft1c.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soft1c.R
import com.example.soft1c.databinding.ItemAcceptanceBinding
import com.example.soft1c.extension.inflateLayout
import com.example.soft1c.model.Acceptance

class AcceptanceAdapter :
    ListAdapter<Acceptance, AcceptanceAdapter.AcceptanceHolder>(AcceptanceDiffUtil()) {

    class AcceptanceHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemAcceptanceBinding.bind(view)

        fun onBind(acceptance: Acceptance) {
            with(binding) {
                binding.txtDocumentNumber.text = acceptance.number
                binding.txtClient.text = acceptance.client
                binding.txtPackage.text = acceptance._package

                binding.txtEmptyWeight.isVisible = !acceptance.weight
                binding.txtEmptyCapacity.isVisible = !acceptance.capacity

                binding.ivWeight.isVisible = acceptance.weight
                binding.ivCapacity.isVisible = acceptance.capacity
            }
        }
    }

    class AcceptanceDiffUtil : DiffUtil.ItemCallback<Acceptance>() {
        override fun areItemsTheSame(oldItem: Acceptance, newItem: Acceptance): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Acceptance, newItem: Acceptance): Boolean {
            return oldItem.ref == newItem.ref
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptanceHolder {
        return AcceptanceHolder(parent.inflateLayout(R.layout.item_acceptance))
    }

    override fun onBindViewHolder(holder: AcceptanceHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size
}