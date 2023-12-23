package io.github.christianfajardo.randomuser.presentation.landing

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.christianfajardo.randomuser.BuildConfig
import io.github.christianfajardo.randomuser.utils.Resource
import io.github.christianfajardo.randomuser.utils.Constants
import io.github.christianfajardo.randomuser.utils.print
import io.github.christianfajardo.randomuser.R
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject


@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {

    private lateinit var rvFriends: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private lateinit var ivNoInternet: ImageView
    private lateinit var parentLayout: View
    private lateinit var searchView: SearchView

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    lateinit var userAdapter: UserAdapter

    private val userViewModel: UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        initViews()
        observeViewModel()
        setUpRecyclerView()
        setupSearchView()

    }

    private fun setupSearchView() {
        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    userViewModel.getUsers()
                    compositeDisposable.add(
                        userViewModel.userListFlowable.subscribe { resource ->
                            when (resource) {
                                is Resource.Success -> {
                                    userAdapter.differ.submitList(resource.data)
                                }
                            }
                        }
                    )
                    return true
                }
                rvFriends.visibility = View.VISIBLE
                val userAdapterList = userAdapter.getList()
                val filteredList = if (!newText.isNullOrBlank()) {
                    val list=userAdapterList.filter { user ->
                        val fullName = "${user.name.first} ${user.name.last}"
                        fullName.contains(newText, ignoreCase = true) ||
                                user.email.contains(newText, ignoreCase = true)
                    }
                    list

                } else {
                    Log.d("No","No action")
                    emptyList()
                }
                userAdapter.differ.submitList(filteredList)
                return true
            }
        })
    }


    private fun initViews() {
        parentLayout = findViewById(android.R.id.content)
        rvFriends = findViewById(R.id.rvFriends)
        pbLoading = findViewById(R.id.pbLoading)
        ivNoInternet = findViewById(R.id.ivNoInternet)
    }


    private fun observeViewModel() {


        compositeDisposable.add(
            userViewModel
                .userListFlowable
                .subscribe { resource ->

                    when (resource) {

                        is Resource.Loading -> {
                            pbLoading.visibility = View.VISIBLE
                            ivNoInternet.visibility = View.GONE

                        }

                        is Resource.Error -> {

                            pbLoading.visibility = View.GONE
                            ivNoInternet.visibility = View.VISIBLE


                            if (BuildConfig.DEBUG) {
                                Log.e(tag, "setUpObserver: ${resource.message}")
                            }


                            showErrorSnackBar()
                        }

                        is Resource.Success -> {


                            pbLoading.visibility = View.GONE
                            ivNoInternet.visibility = View.GONE
                            rvFriends.visibility = View.VISIBLE
                            userAdapter.differ.submitList(resource.data)
                        }
                    }

                })

    }


    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }


    private fun showErrorSnackBar() {


        val snackBar = Snackbar.make(parentLayout, "Something went wrong", Snackbar.LENGTH_SHORT)


        compositeDisposable.add(userViewModel.internetStatus.subscribe { internetStatus ->
            if (!internetStatus) {


                snackBar.setText("Your Internet Connection is off")

                snackBar.setAction(
                    "TURN ON"
                ) {
                    try {

                        launchWifiSettings()
                        userViewModel.userListFlowable.retry()


                    } catch (e: Exception) {

                        e.print(tag, "launchwifi set")

                        snackBar.setText("Could not open Wi-Fi settings")
                        snackBar.show()

                    }
                }

                snackBar.show()

            } else {
                snackBar.show()

            }
        })

    }

    private fun setUpRecyclerView() {


        rvFriends.apply {


            val gridViewItemAmount: Int = 3

            layoutManager =
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    GridLayoutManager(
                        this@LandingActivity,
                        gridViewItemAmount
                    )
                } else {
                    LinearLayoutManager(this@LandingActivity)
                }

            adapter = userAdapter

            overScrollMode = View.OVER_SCROLL_NEVER

        }
    }



    private fun launchWifiSettings() {
        val intent = Intent(Constants.WIFI_SETTING_ACTION)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    private val tag = "LandingActivity"

}