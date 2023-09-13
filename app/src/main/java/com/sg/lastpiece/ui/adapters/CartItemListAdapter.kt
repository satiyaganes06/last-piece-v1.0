package com.sg.lastpiece.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sg.lastpiece.Firestore.FirestoreClass
import com.sg.lastpiece.Models.Cart_Details
import com.sg.lastpiece.R
import com.sg.lastpiece.ui.Activities.BookDetailsActivity
import com.sg.lastpiece.ui.Activities.EbookDetailsActivity
import com.sg.lastpiece.ui.Activities.ShopCartActivity
import com.sg.lastpiece.utils.Constants
import com.sg.lastpiece.utils.GlideLoader
import kotlinx.android.synthetic.main.item_cart_layout.view.*

open class CartItemListAdapter(
        private val context: Context,
        private var list: ArrayList<Cart_Details>,
        private val updateCartItems: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_cart_layout,
                        parent,
                        false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadBookPicture(model.book_image, holder.itemView.iv_cart_item_image)

            holder.itemView.tv_cart_item_title.text = model.book_title
            holder.itemView.tv_cart_item_price.text = "RM ${model.book_price}0"
            holder.itemView.tv_cart_quantity.text = model.cart_quantity


            if (model.cart_quantity == "0") {
                holder.itemView.ib_remove_cart_item.visibility = View.GONE
                holder.itemView.ib_add_cart_item.visibility = View.GONE

                if (updateCartItems) {
                    holder.itemView.ib_delete_cart_item.visibility = View.VISIBLE
                } else {
                    holder.itemView.ib_delete_cart_item.visibility = View.GONE
                }

                holder.itemView.tv_cart_quantity.text =
                        context.resources.getString(R.string.lbl_out_of_stock)

                holder.itemView.tv_cart_quantity.setTextColor(
                        ContextCompat.getColor(
                                context,
                                R.color.colorSnackBarError
                        )
                )
            } else {

                if (updateCartItems) {
                    holder.itemView.ib_remove_cart_item.visibility = View.VISIBLE
                    holder.itemView.ib_add_cart_item.visibility = View.VISIBLE
                    holder.itemView.ib_delete_cart_item.visibility = View.VISIBLE
                } else {

                    holder.itemView.ib_remove_cart_item.visibility = View.GONE
                    holder.itemView.ib_add_cart_item.visibility = View.GONE
                    holder.itemView.ib_delete_cart_item.visibility = View.GONE
                }

                holder.itemView.tv_cart_quantity.setTextColor(
                        ContextCompat.getColor(
                                context,
                                R.color.dark_silver
                        )
                )
            }

            holder.itemView.ib_remove_cart_item.setOnClickListener {

                if (model.cart_quantity == "1") {
                    FirestoreClass().removeItemFromCart(context, model.cart_id)
                } else {

                    val cartQuantity: Int = model.cart_quantity.toInt()

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    // Show the progress dialog.

                    if (context is ShopCartActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.cart_id, itemHashMap)
                }
            }

            holder.itemView.ib_add_cart_item.setOnClickListener {

                val cartQuantity: Int = model.cart_quantity.toInt()

                if (cartQuantity < model.book_quantity.toInt()) {

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    // Show the progress dialog.
                    if (context is ShopCartActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.cart_id, itemHashMap)
                } else {
                    if (context is ShopCartActivity) {
                        context.showErrorSnackBar(
                                context.resources.getString(
                                        R.string.msg_for_available_stock,
                                        model.book_quantity
                                ),
                                true
                        )
                    }
                }
            }

            holder.itemView.ib_delete_cart_item.setOnClickListener {

                when (context) {
                    is ShopCartActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                }

                FirestoreClass().removeItemFromCart(context, model.cart_id)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, BookDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_BOOK_ID, model.book_id)
                context.startActivity(intent)
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}