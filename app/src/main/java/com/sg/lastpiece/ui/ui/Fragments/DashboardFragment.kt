package com.sg.lastpiece.ui.ui.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.mancj.materialsearchbar.MaterialSearchBar
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.Book_Details
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.Activities.*
import com.sg.lastpiece.ui.adapters.DashboardBooksListAdapter
import com.sg.lastpiece.utils.Constants
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_product.*


class DashboardFragment : BaseFragment(), View.OnClickListener,
    MaterialSearchBar.OnSearchActionListener {

    lateinit var mView: View

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
        //dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        mView = inflater.inflate(R.layout.fragment_dashboard, container, false)


        mView.rb_All_Book.setOnClickListener(this)
        mView.rb_fkom.setOnClickListener(this)
        mView.rb_FTKKP.setOnClickListener(this)
        mView.rb_ftkee.setOnClickListener(this)
        mView.rb_FTKA.setOnClickListener(this)
        mView.rb_FTKPM.setOnClickListener(this)
        mView.rb_FTKMA.setOnClickListener(this)
        mView.rb_PSM.setOnClickListener(this)
        mView.rb_FIST.setOnClickListener(this)
        mView.rb_fim.setOnClickListener(this)

        mView.search_bar.setOnSearchActionListener(this)


        return mView
    }


    // Override the onCreateOptionMenu function and inflate the Dashboard menu file init.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)


    }

    // Override the onOptionItemSelected function and handle the action items init.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.action_shop_cart -> {

                // Launch the ShopCartActivity on click of action item.
                startActivity(Intent(activity, ShopCartActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        //ImageSiderCarousel()
        getDashboardBooksListFromFireStore()
    }


    //A function to get the successful product list from cloud firestore
    //productsList Will receive the product list from cloud firestore.
    fun successBooksListFromFireStore(dashboardBooksList: ArrayList<Book_Details>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if (dashboardBooksList.size > 0) {

            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE

            // Dashboard layout design
            rv_dashboard_items.layoutManager = GridLayoutManager(activity, 2)
            rv_dashboard_items.setHasFixedSize(true)

            val adapter = DashboardBooksListAdapter(requireActivity(), dashboardBooksList)
            rv_dashboard_items.adapter = adapter


            adapter.setOnClickListener(object :
                DashboardBooksListAdapter.OnClickListener {
                override fun onClick(position: Int, book: Book_Details) {
                    val intent = Intent(context, BookDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_BOOK_ID, book.book_id)
                    intent.putExtra(Constants.EXTRA_BOOK_OWNER_ID, book.user_id)
                    startActivity(intent)

                }
            })

        } else {
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }


    }

    private fun getDashboardBooksListFromFireStore() {
        //Show Progress Bar
        showProgressDialog(resources.getString(R.string.please_wait))

        //ImageSiderCarousel()
        FirestoreClass().getDashboardBooksList(this@DashboardFragment)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.rb_All_Book -> {
                    getDashboardBooksListFromFireStore()
                }

                R.id.rb_fkom -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getDashboardBooksListBySort(this, Constants.BOOK_FACULTY, resources.getString(R.string.til_sort_fkom))
                }

                R.id.rb_FTKKP -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getDashboardBooksListBySort(this, Constants.BOOK_FACULTY, resources.getString(R.string.til_sort_ftkkp))
                }

                R.id.rb_ftkee -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getDashboardBooksListBySort(this, Constants.BOOK_FACULTY, resources.getString(R.string.til_sort_ftkee))
                }

                R.id.rb_FTKMA -> {
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getDashboardBooksListBySort(this, Constants.BOOK_FACULTY, resources.getString(R.string.til_sort_ftkma))
                }

                R.id.rb_FTKA -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getDashboardBooksListBySort(this, Constants.BOOK_FACULTY, resources.getString(R.string.til_sort_ftka))
                }

                R.id.rb_FTKPM -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getDashboardBooksListBySort(this, Constants.BOOK_FACULTY, resources.getString(R.string.til_sort_ftkpm))
                }

                R.id.rb_PSM -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getDashboardBooksListBySort(this, Constants.BOOK_FACULTY, resources.getString(R.string.til_sort_psm))
                }

                R.id.rb_FIST -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getDashboardBooksListBySort(this, Constants.BOOK_FACULTY, resources.getString(R.string.til_sort_fist))
                }

                R.id.rb_fim -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getDashboardBooksListBySort(this, Constants.BOOK_FACULTY, resources.getString(R.string.til_sort_fim))
                }

            }
        }
    }

    override fun onSearchStateChanged(enabled: Boolean) {

    }

    override fun onSearchConfirmed(text: CharSequence?) {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getDashboardBooksListBySearch(this, mView.search_bar.text)
        //startSearch(text.toString(), true, null, true);
    }

    override fun onButtonClicked(buttonCode: Int) {
        when (buttonCode) {
           // MaterialSearchBar.BUTTON_NAVIGATION -> drawer.openDrawer(Gravity.LEFT)
        }
    }


}
