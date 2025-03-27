package com.example.librarymanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagement.model.Account

class PersonAdapter(
    private var accounts: List<Account>,
    private val onEditClick: (Account) -> Unit,
    private val onDeleteClick: (Account) -> Unit,
    private val onLockClick: (Account) -> Unit,
    private val onUnlockClick: (Account) -> Unit
) : RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsername: TextView = itemView.findViewById(R.id.tv_str_username)
        val tvVaiTro: TextView = itemView.findViewById(R.id.tv_str_role)
        val tvLockAccount: TextView = itemView.findViewById(R.id.tv_str_lockAccount)
        val iconMenu: ImageView = itemView.findViewById(R.id.image_icon_3_cham)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_rcv_person, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val account = accounts[position]
        holder.tvUsername.text = account.tenDangNhap
        holder.tvVaiTro.text = account.vaiTro
        holder.tvLockAccount.text = when (account.lockAccount) {
            true -> "True"
            false -> "False"
            null -> "Unknown"
        }
        holder.tvLockAccount.setTextColor(
            if (account.lockAccount == true)
                holder.itemView.context.getColor(android.R.color.holo_red_dark)
            else
                holder.itemView.context.getColor(android.R.color.holo_green_dark)
        )
        // Xử lý sự kiện bấm vào icon menu
        holder.iconMenu.setOnClickListener { view ->
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.menu_person_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        onEditClick(account)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick(account)
                        true
                    }
                    R.id.action_lock -> {
                        onLockClick(account)
                        true
                    }
                    R.id.action_unlock -> {
                        onUnlockClick(account)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int = accounts.size

    fun updateAccounts(newAccounts: List<Account>) {
        accounts = newAccounts
        notifyDataSetChanged()
    }
}