package com.sg.lastpiece.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.Wishlist_Details
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.Activities.EbookDetailsActivity
import com.sg.lastpiece.ui.Activities.ShopCartActivity
import com.sg.lastpiece.utils.Constants
import com.sg.lastpiece.utils.GlideLoader
import kotlinx.android.synthetic.main.item_cart_layout.view.*
import kotlinx.android.synthetic.main.item_list_layout.view.*


open class WishItemListAdapter(
        private val context: Context,
        private var list: ArrayList<Wishlist_Details>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_list_layout,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadBookPicture(
                    model.ebook_image,
                    holder.itemView.iv_item_image
            )

            holder.itemView.tv_item_name.text = model.ebook_title
            holder.itemView.tv_item_price.text = model.ebook_category

            holder.itemView.ib_delete_product.visibility = View.VISIBLE

            holder.itemView.setOnClickListener {
                val intent = Intent(context, EbookDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_EBOOK_ID, model.ebook_id)
                context.startActivity(intent)
            }

           holder.itemView.ib_delete_product.setOnClickListener {

                FirestoreClass().removeItemFromWishlist(context, model.wishlish_id)

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}