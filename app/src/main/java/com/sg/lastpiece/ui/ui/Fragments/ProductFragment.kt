package com.sg.lastpiece.ui.ui.Fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.Book_Details
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.Activities.AddingProductActivity
import com.sg.lastpiece.ui.adapters.MyBooksListAdapter
import com.sg.lastpiece.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_address.*
import kotlinx.android.synthetic.main.fragment_product.*

class ProductFragment : BaseFragment() {

    // Override the onCreate function and add the piece of code to inflate the option menu in fragment.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      //  homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_product, container, false)


        return root
    }

    // Override the onCreateOptionMenu function and inflate the Dashboard menu file init.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Override the onOptionItemSelected function and handle the action items init.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_add_product) {

            startActivity(Intent(activity, AddingProductActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun getBooksListFromFireStore() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getBooksList(this@ProductFragment)
    }

    override fun onResume() {
        super.onResume()
        getBooksListFromFireStore()
    }


    //A function to get the successful product list from cloud firestore
    //productsList Will receive the product list from cloud firestore.
    fun successBooksListFromFireStore(booksList: ArrayList<Book_Details>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if(booksList.size > 0){
            rv_my_book_items.visibility = View.VISIBLE
            tv_no_book_found.visibility = View.GONE

            rv_my_book_items.layoutManager = LinearLayoutManager(activity)
            rv_my_book_items.setHasFixedSize(true)

            val adapterBooks = MyBooksListAdapter(requireActivity(), booksList, this@ProductFragment)
            rv_my_book_items.adapter = adapterBooks
        } else {
            rv_my_book_items.visibility = View.GONE
            tv_no_book_found.visibility = View.VISIBLE
        }

    }

    fun deleteBooks(bookID : String){
        showAlertDialogToDeleteBook(bookID)
    }

    fun bookDeleteSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        val toast: Toast = Toast.makeText(
            requireActivity(),
            resources.getString(R.string.book_delete_success_message),
            Toast.LENGTH_SHORT
        )

        val view = toast.view
        view?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        toast.show()


        // Get the latest products list from cloud firestore.
        getBooksListFromFireStore()
    }

    //A function to show the alert dialog for the confirmation of delete product from cloud firestore.
    private fun showAlertDialogToDeleteBook(bookID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)


        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Call the function of Firestore class.
            FirestoreClass().deleteBooks(this@ProductFragment, bookID)

            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}

