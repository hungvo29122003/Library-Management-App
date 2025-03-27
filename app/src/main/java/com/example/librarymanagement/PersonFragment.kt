package com.example.librarymanagement

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagement.api.RetrofitClient
import com.example.librarymanagement.model.Account
import com.example.librarymanagement.model.DeleteAccountRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PersonFragment : Fragment() {
    private lateinit var rcvPerson: RecyclerView
    private lateinit var personAdapter: PersonAdapter
    private val accounts = mutableListOf<Account>()
    private lateinit var sharedPreferences: SharedPreferences
    private var role : String = "DocGia"
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        role = sharedPreferences.getString("vaiTro", null) // Sửa lỗi khoảng trắng trong "vai Tro"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_person, container, false)

        if (role == null) {
            Toast.makeText(requireContext(), "Không tìm thấy vai trò!", Toast.LENGTH_SHORT).show()
            return view
        }
        Toast.makeText(requireContext(), "Vai trò: $role", Toast.LENGTH_SHORT).show()

        rcvPerson = view.findViewById(R.id.rcv_person)
        personAdapter = PersonAdapter(
            accounts,
            onEditClick = { account ->
                // Xử lý chỉnh sửa
            },
            onDeleteClick = { account ->
                // Xử lý xóa
                showDeleteConfirmationDialog(account)
            },
            onLockClick = { account ->
                val intent = Intent(requireContext(), LockReaderActivity::class.java)
                intent.putExtra("TEN_DANG_NHAP", account.tenDangNhap)
                startActivity(intent)
            },
            onUnlockClick = { account ->
                val intent = Intent(requireContext(), UnlockReaderActivity::class.java)
                intent.putExtra("TEN_DANG_NHAP", account.tenDangNhap)
                startActivity(intent)
            }
        )

        rcvPerson.layoutManager = LinearLayoutManager(requireContext())
        rcvPerson.adapter = personAdapter

        fetchAccountsByRole(role!!)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAddPerson = requireView().findViewById<AppCompatButton>(R.id.btn_add)

        btnAddPerson.setOnClickListener {
            val intent = Intent(requireActivity(), Add_PersonActivity::class.java)
            startActivity(intent)
        }
    }
    private fun showDeleteConfirmationDialog(account: Account) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa tài khoản ${account.tenDangNhap}?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteAccount(account)
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteAccount(account: Account) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.deleteAccount(account.tenDangNhap)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Xóa tài khoản thành công!", Toast.LENGTH_SHORT).show()
                        accounts.remove(account)
                        personAdapter.updateAccounts(accounts)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    private fun fetchAccountsByRole(role: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getAccountsByRole(role)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val accountsResponse = response.body()
                        if (accountsResponse != null) {
                            accounts.clear()
                            accounts.addAll(accountsResponse.accounts)
                            personAdapter.updateAccounts(accounts)
                        } else {
                            Toast.makeText(requireContext(), "Không nhận được dữ liệu tài khoản!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAccountsByRole(role!!)
    }
}