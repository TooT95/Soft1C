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
import com.example.soft1c.model.ItemClicked

class AcceptanceAdapter(
    private val onItemClicked: (itemClicked: ItemClicked, acceptance: Acceptance) -> Unit,
    private val showColumnZone: Boolean,
) :
    ListAdapter<Acceptance, AcceptanceAdapter.AcceptanceHolder>(AcceptanceDiffUtil()) {

    class AcceptanceHolder(
        private val onItemClicked: (itemClicked: ItemClicked, acceptance: Acceptance) -> Unit,
        private val showColumnZone: Boolean,
        view: View,
    ) : RecyclerView.ViewHolder(view) {

        private val binding = ItemAcceptanceBinding.bind(view)

        fun onBind(acceptance: Acceptance) {
            itemView.setOnClickListener {
                onItemClicked(ItemClicked.ITEM, acceptance)
            }
            with(binding) {
                if (ACCEPTANCE_GUID == acceptance.ref)
                    binding.linearContainer.setBackgroundResource(R.color.selectedItem)
                txtDocumentNumber.text = acceptance.number
                txtClient.text = acceptance.client
                txtPackage.text = acceptance._package
                txtZone.text = acceptance.zone
                txtZone.isVisible = showColumnZone

                txtEmptyWeight.isVisible = !acceptance.weight
                txtEmptyCapacity.isVisible = !acceptance.capacity

                ivWeight.isVisible = acceptance.weight
                ivCapacity.isVisible = acceptance.capacity
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
        return AcceptanceHolder(onItemClicked,
            showColumnZone,
            parent.inflateLayout(R.layout.item_acceptance))
    }

    override fun onBindViewHolder(holder: AcceptanceHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    companion object {
        var ACCEPTANCE_GUID = ""
    }
}