package com.sg.lastpiece.ui.ui.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.eBook_Details
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.Activities.*
import com.sg.lastpiece.ui.adapters.eBookDashboardListAdapter
import com.sg.lastpiece.utils.Constants
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.rb_All_Book
import kotlinx.android.synthetic.main.fragment_ebook_dashboard.*
import kotlinx.android.synthetic.main.fragment_ebook_dashboard.rv_dashboard_items
import kotlinx.android.synthetic.main.fragment_ebook_dashboard.view.*

class EbookDashboardFragment : BaseFragment(), View.OnClickListener {

    lateinit var mView: View

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
         mView = inflater.inflate(R.layout.fragment_ebook_dashboard, container, false)

         mView.rb_All_Book.setOnClickListener(this)
         mView.rb_education.setOnClickListener(this)
         mView.rb_action_A.setOnClickListener(this)
         mView.rb_thrillers.setOnClickListener(this)
         mView.rb_fantasy.setOnClickListener(this)
         mView.rb_historical.setOnClickListener(this)
         mView.rb_Sci_Fi.setOnClickListener(this)
         mView.rb_Biographies.setOnClickListener(this)
         mView.rb_Anime.setOnClickListener(this)


        return mView
    }

    // Override the onCreateOptionMenu function and inflate the ebook Dashboard menu file init.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.wishlist_ebook_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        //The selected user only can visible this button
        if(FirestoreClass().getCurrentUserID() == Constants.SELECTED_USER_ID){
            menu.findItem(R.id.action_add_ebook).setVisible(true)
        }else{
            menu.findItem(R.id.action_add_ebook).setVisible(false)
        }

    }

    // Override the onOptionItemSelected function and handle the action items init.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when (id) {

            R.id.action_wishlist -> {
                // Launch the ShopCartActivity on click of action item.
                startActivity(Intent(activity, WishlistActivity::class.java))
                return true
            }

        }

        if(FirestoreClass().getCurrentUserID() == Constants.SELECTED_USER_ID){

            when (id) {
                R.id.action_add_ebook -> {
                    // Launch the ShopCartActivity on click of action item.
                    startActivity(Intent(activity, Admin_AddEbookActivity::class.java))
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        getDashboardBooksListFromFireStore()
    }

    fun successEbooksListFromFireStore(EbookdashboardEbooksList: ArrayList<eBook_Details>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if (EbookdashboardEbooksList.size > 0) {

            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_ebook_found.visibility = View.GONE

            // Dashboard layout design
            rv_dashboard_items.layoutManager = GridLayoutManager(activity, 2)
            rv_dashboard_items.setHasFixedSize(true)

            val adapter = eBookDashboardListAdapter(requireActivity(), EbookdashboardEbooksList)
            rv_dashboard_items.adapter = adapter


            adapter.setOnClickListener(object :
                eBookDashboardListAdapter.OnClickListener {
                override fun onClick(position: Int, ebook: eBook_Details) {
                    val intent = Intent(context, EbookDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_EBOOK_ID, ebook.ebook_id)
                    startActivity(intent)

                }
            })

        } else {
            rv_dashboard_items.visibility = View.GONE
            tv_no_ebook_found.visibility = View.VISIBLE
        }

    }

    private fun getDashboardBooksListFromFireStore() {
        //Show Progress Bar
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getEbookDashboardBooksList(this@EbookDashboardFragment)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.rb_All_Book -> {
                    getDashboardBooksListFromFireStore()
                }

                R.id.rb_education -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().geteBooksDashboardListBySort(this, Constants.EBOOK_CATEGORY, resources.getString(R.string.til_category_education))
                }

                R.id.rb_action_A -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().geteBooksDashboardListBySort(this, Constants.EBOOK_CATEGORY, resources.getString(R.string.til_category_Action_Adventure))
                }

                R.id.rb_thrillers -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().geteBooksDashboardListBySort(this, Constants.EBOOK_CATEGORY, resources.getString(R.string.til_category_thrillers))
                }

                R.id.rb_fantasy -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().geteBooksDashboardListBySort(this, Constants.EBOOK_CATEGORY, resources.getString(R.string.til_category_fantasy))
                }

                R.id.rb_historical -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().geteBooksDashboardListBySort(this, Constants.EBOOK_CATEGORY, resources.getString(R.string.til_category_historical))
                }

                R.id.rb_Sci_Fi -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().geteBooksDashboardListBySort(this, Constants.EBOOK_CATEGORY, resources.getString(R.string.til_category_Sci_Fi))
                }

                R.id.rb_Biographies -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().geteBooksDashboardListBySort(this, Constants.EBOOK_CATEGORY, resources.getString(R.string.til_category_biographies))
                }

                R.id.rb_Anime -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().geteBooksDashboardListBySort(this, Constants.EBOOK_CATEGORY, resources.getString(R.string.til_category_anime))
                }

            }
        }
    }

}