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
import com.example.librarymanagement.model.Sach
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BookFragment : Fragment() {
    private lateinit var rcvBook: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private val books = mutableListOf<Sach>()
    private lateinit var sharedPreferences: SharedPreferences
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book, container, false)

        rcvBook = view.findViewById(R.id.rcv_book)
        bookAdapter = BookAdapter(
            books,
            onEditClick = { book ->
                val intent = Intent(requireContext(), EditBookActivity::class.java)
                intent.putExtra("BOOK_KEY", book)
                startActivity(intent)
            },
            onDeleteClick = { book ->
                showDeleteConfirmationDialog(book)
            }
        )

        rcvBook.layoutManager = LinearLayoutManager(requireContext())
        rcvBook.adapter = bookAdapter

        fetchBooks()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAddBook = requireView().findViewById<AppCompatButton>(R.id.btn_addBook)

        btnAddBook.setOnClickListener {
            val intent = Intent(requireActivity(), Add_BookActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showDeleteConfirmationDialog(book: Sach) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sách ${book.tenSach}?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteBook(book)
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteBook(book: Sach) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.deleteBook(book.tenSach)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Xóa sách thành công!", Toast.LENGTH_SHORT).show()
                        books.remove(book)
                        bookAdapter.updateBooks(books)
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

    private fun fetchBooks() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getAllBooks()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val bookList = response.body() ?: emptyList()
                        books.clear()
                        books.addAll(bookList)
                        bookAdapter.updateBooks(books)
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
        fetchBooks()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}