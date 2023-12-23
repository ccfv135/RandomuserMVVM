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
        initViews() // initialises the views by their id
        observeViewModel() // sets up user list data observer from view model
        setUpRecyclerView() // sets up the recycler view
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
                    // Recargar los datos del LiveData o hacer una nueva petición
                    userViewModel.getUsers() // Esta llamada ya debería estar disparando la actualización
                    compositeDisposable.add(
                        userViewModel.userListFlowable.subscribe { resource ->
                            when (resource) {
                                is Resource.Success -> {
                                    userAdapter.differ.submitList(resource.data)
                                }
                                // Manejo de otros estados (Loading, Error) si es necesario
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
                            // if it is loading,

                            // show the loading bar
                            pbLoading.visibility = View.VISIBLE
                            ivNoInternet.visibility = View.GONE

                        }

                        is Resource.Error -> {

                            // if there is an error,

                            // hide the loading bar
                            pbLoading.visibility = View.GONE
                            ivNoInternet.visibility = View.VISIBLE


                            // log the error message if its a debug build
                            if (BuildConfig.DEBUG) {
                                Log.e(tag, "setUpObserver: ${resource.message}")
                            }

                            // show snack bar based on the error type
                            showErrorSnackBar()
                        }

                        is Resource.Success -> {

                            // if there was no error,

                            // hide the loading bar
                            pbLoading.visibility = View.GONE
                            ivNoInternet.visibility = View.GONE

                            // show the recycler view
                            rvFriends.visibility = View.VISIBLE


                            // and make the adapter differ consume the user list
                            userAdapter.differ.submitList(resource.data)
                        }
                    }

                })

    }


    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }


    // shows snack bar based on error type
    // if the error is due to the internet connection is off
    // then it would show to turn on the internet
    private fun showErrorSnackBar() {

        // create a snack bar with generic body, which can be changed based on the situation
        val snackBar = Snackbar.make(parentLayout, "Something went wrong", Snackbar.LENGTH_SHORT)


        compositeDisposable.add(userViewModel.internetStatus.subscribe { internetStatus ->
            if (!internetStatus) {

                // shows this snack bar when internet is turned off
                snackBar.setText("Your Internet Connection is off")

                snackBar.setAction(
                    "TURN ON"
                ) {
                    try {
                        // to turn on the wifi launch the wifi settings
                        launchWifiSettings()
                        userViewModel.userListFlowable.retry()


                    } catch (e: Exception) {

                        e.print(tag, "launchwifi set")

                        // if for some reason the wifi settings page could not be opened
                        // show another toast message to inform the user it
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

    // sets up the recycler with view adapter, layout manager and other configurations
    private fun setUpRecyclerView() {


        rvFriends.apply {

            /*
                 if the device is in landscape mode, recycler view
                 will show a grid view with 2 span count.

                 and if it is in portrait mode, this will
                 show a linear layout with 1 span count
             */

            // span count in grid view
            val gridViewItemAmount: Int = 3

            layoutManager =
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    GridLayoutManager(
                        this@LandingActivity,
                        gridViewItemAmount
                    ) // for landscape mode
                } else {
                    LinearLayoutManager(this@LandingActivity) // for portrait mode
                }

            adapter = userAdapter

            overScrollMode = View.OVER_SCROLL_NEVER // hide the overscroll effect

        }
    }


    // launch wifi settings in another task
    private fun launchWifiSettings() {
        val intent = Intent(Constants.WIFI_SETTING_ACTION)
        // opens the new activity in another task
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    private val tag = "LandingActivity"

}