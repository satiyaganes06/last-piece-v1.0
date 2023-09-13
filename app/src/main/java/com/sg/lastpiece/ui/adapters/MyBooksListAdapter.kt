package com.sg.lastpiece.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.lastpiece.Models.Book_Details
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.Activities.BookDetailsActivity
import com.sg.lastpiece.ui.ui.Fragments.ProductFragment
import com.sg.lastpiece.utils.Constants
import com.sg.lastpiece.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

/**
 * A adapter class for products list items.
 */
open class MyBooksListAdapter(
        private val context: Context,
        private var list: ArrayList<Book_Details>,
        private val fragment: ProductFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    // Inflates the item views which is designed in xml layout file

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_list_layout,
                        parent,
                        false
                )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

            if (holder is MyViewHolder) {

                GlideLoader(context).loadBookPicture(model.book_image, holder.itemView.iv_item_image)

                holder.itemView.tv_item_name.text = model.book_name
                holder.itemView.tv_item_price.text = "RM ${model.book_price}0"
                holder.itemView.tv_item_quantity.text = "Quantity : ${model.book_quantity}"

               holder.itemView.ib_delete_product.setOnClickListener {

                    fragment.deleteBooks(model.book_id)
                }


                    holder.itemView.setOnClickListener {
                        // Launch Product details screen.
                        val intent = Intent(context, BookDetailsActivity::class.java)
                        intent.putExtra(Constants.EXTRA_BOOK_ID, model.book_id)
                        intent.putExtra(Constants.EXTRA_BOOK_OWNER_ID, model.user_id)
                        context.startActivity(intent)
                    }
                }
        }

    // Gets the number of items in the list
    override fun getItemCount(): Int {
        return list.size
    }


    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.

     class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}




