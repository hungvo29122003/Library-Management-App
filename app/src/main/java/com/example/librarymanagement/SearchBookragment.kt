package com.example.librarymanagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagement.adapter.BookSearchAdapter
import com.example.librarymanagement.api.RetrofitClient
import com.example.librarymanagement.model.Sach
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchBookragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchBookragment : Fragment() {
    private lateinit var etSearchBook: EditText
    private lateinit var rcv_booksResults: RecyclerView
    private lateinit var btnSearchBook: Button
    private lateinit var bookAdapter: BookSearchAdapter
    private var bookList: MutableList<Sach> = mutableListOf()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_bookragment, container, false)

        // Khởi tạo các view
        etSearchBook = view.findViewById(R.id.etSearchBook)
        rcv_booksResults = view.findViewById(R.id.rcv_booksResults)
        btnSearchBook = view.findViewById(R.id.btnSearchBook)

        // Thiết lập RecyclerView
        rcv_booksResults.layoutManager = LinearLayoutManager(requireContext())
        bookAdapter = BookSearchAdapter()
        rcv_booksResults.adapter = bookAdapter

        // Lấy danh sách tất cả sách khi khởi tạo
        fetchAllBooks()

        // Xử lý sự kiện click nút tìm kiếm
        btnSearchBook.setOnClickListener {
            val tenSach = etSearchBook.text.toString().trim()
            if (tenSach.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Vui lòng nhập tên sách để tìm kiếm!",
                    Toast.LENGTH_SHORT
                ).show()
                fetchAllBooks() // Hiển thị lại toàn bộ danh sách nếu không nhập tên
            } else {
                searchBookByName(tenSach)
            }
        }
        return view
    }
    private fun fetchAllBooks() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getAllBooks()
                if (response.isSuccessful) {
                    val books = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        bookAdapter.updateBooks(books)
                        if (books.isEmpty()) {
                            Toast.makeText(requireContext(), "Không có sách nào!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun searchBookByName(tenSach: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.findBookByName(tenSach)
                if (response.isSuccessful) {
                    val bookResponse = response.body()
                    withContext(Dispatchers.Main) {
                        val books = bookResponse?.book ?: emptyList()
                        bookAdapter.updateBooks(books)
                        if (books.isEmpty()) {
                            Toast.makeText(requireContext(), "Không tìm thấy sách!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}