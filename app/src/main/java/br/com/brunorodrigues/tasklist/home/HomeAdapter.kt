package br.com.brunorodrigues.tasklist.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.brunorodrigues.tasklist.databinding.HeaderTaskBinding
import br.com.brunorodrigues.tasklist.databinding.ItemTaskBinding
import br.com.brunorodrigues.tasklist.model.TaskModel

class HomeAdapter(
    private val list: List<Any> = emptyList(),
    private val listener: (task: TaskModel) -> Unit
) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val ITEM_TYPE_HEADER = 0
        const val ITEM_TYPE_TASK = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == ITEM_TYPE_HEADER) {
            return ViewHolderHeader(
                HeaderTaskBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else if (viewType == ITEM_TYPE_TASK) {
            return ViewHolderTask(
                ItemTaskBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
        }

        throw RuntimeException("Error Adapter")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderHeader -> holder.bind(list[position] as String)
            is ViewHolderTask -> holder.bind(list[position] as TaskModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is String -> ITEM_TYPE_HEADER
            is TaskModel -> ITEM_TYPE_TASK
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun getItemCount(): Int = list.size

}

class ViewHolderTask(
    private val binding: ItemTaskBinding,
    private val listener: (task: TaskModel) -> Unit
) :
    ViewHolder(binding.root) {

    fun bind(item: TaskModel) {
        binding.apply {
            tvTitle.text = item.title
            root.setOnClickListener { listener(item) }
        }
    }
}

class ViewHolderHeader(private val binding: HeaderTaskBinding) :
    ViewHolder(binding.root) {

    fun bind(date: String) {
        binding.apply {
            tvHeader.text = date
        }
    }
}

