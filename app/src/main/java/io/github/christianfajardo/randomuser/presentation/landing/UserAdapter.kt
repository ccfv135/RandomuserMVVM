package io.github.christianfajardo.randomuser.presentation.landing

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import de.hdodenhof.circleimageview.CircleImageView
import io.github.christianfajardo.randomuser.R
import io.github.christianfajardo.randomuser.domain.model.User
import io.github.christianfajardo.randomuser.presentation.userdetails.UserDetailsActivity
import io.github.christianfajardo.randomuser.presentation.userdetails.UserDetailsActivity.Companion.USER_DATA
import io.github.christianfajardo.randomuser.presentation.loadImage

import javax.inject.Inject


class UserAdapter @Inject constructor() : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    inner class UserViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {
        private val context: Context = view.context

        private var tvFullName: TextView = view.findViewById(R.id.tvFullName)
        private var tvCountry: TextView = view.findViewById(R.id.tvCountry)
        private var ivPortrait: CircleImageView = view.findViewById(R.id.ivPortrait)
        private var mcvUserCard: MaterialCardView = view.findViewById(R.id.mcvUserCard)



        @SuppressLint("SetTextI18n")
        fun bindTo(user: User) {
            tvFullName.text = "${user.name.first} ${user.name.last}"
            tvCountry.text = user.email
            ivPortrait.loadImage(user.picture.medium)


            mcvUserCard.setOnClickListener {
                val intent = Intent(view.context, UserDetailsActivity::class.java)
                // passes the user object to the user details activity
                intent.putExtra(USER_DATA, user)
                context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val userCard = inflater.inflate(R.layout.list_item_user, parent, false)

        return UserViewHolder(userCard)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = differ.currentList[position]

        holder.bindTo(user)
    }

    fun getList ()=differ.currentList

    override fun getItemCount(): Int = differ.currentList.size

    private val diffCallback =
        object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {

                // as only image, country, and name would be shown,
                // no need to add comparison between other contents

                if (oldItem.location.country != newItem.location.country) {
                    return false
                }

                if (oldItem.name.first != newItem.name.first) {
                    return false
                }

                if (oldItem.name.last != newItem.name.last) {
                    return false
                }

                if (oldItem.picture.medium != newItem.picture.medium) {
                    return false
                }

                return true
            }
        }


    /**
     * This will consume the list of users from the user list live data
     *
     * and will present the data in the UI
     */
    val differ = AsyncListDiffer(this, diffCallback)

}