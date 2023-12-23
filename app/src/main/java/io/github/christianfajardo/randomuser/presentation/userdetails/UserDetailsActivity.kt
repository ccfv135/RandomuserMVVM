package io.github.christianfajardo.randomuser.presentation.userdetails

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.github.christianfajardo.randomuser.domain.model.User
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import io.github.christianfajardo.randomuser.R
import io.github.christianfajardo.randomuser.presentation.loadImage
import java.text.SimpleDateFormat
import java.util.Locale


@AndroidEntryPoint
class UserDetailsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var ivPortraitDetails: ImageView
    private lateinit var tvFullNameDetails: TextView
    private lateinit var tvAddressDetails: TextView
    private lateinit var tvCityDetails: TextView
    private lateinit var tvEmailDetails: TextView
    private lateinit var tvCellPhoneDetails: TextView
    private lateinit var llEmail: LinearLayout
    private lateinit var mcvUserCard: MaterialCardView
    private lateinit var tvGenderDetails: TextView
    private lateinit var tvRegisteredDetails: TextView
    private lateinit var tvCoordinatesDetails: TextView
    private lateinit var map: GoogleMap



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        initViews()
        showUpButton()
        getUserData()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    // shows the back button in the action bar

    private fun showUpButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // change the navigate up functionality to the onBackPressed
    // so that, the activity in the back stack gets loaded
    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return false
    }


    private fun initViews() {
        ivPortraitDetails = findViewById(R.id.ivPortrait)
        tvFullNameDetails = findViewById(R.id.tvFullNameDetails)
        tvAddressDetails = findViewById(R.id.tvAddressDetails)
        tvCityDetails = findViewById(R.id.tvCityDetails)
        tvEmailDetails = findViewById(R.id.tvEmailDetails)
        tvCellPhoneDetails = findViewById(R.id.tvCellPhoneDetails)
        llEmail = findViewById(R.id.llEmail)
        mcvUserCard = findViewById(R.id.mcvUserCard)
        tvGenderDetails= findViewById(R.id.tvGenderDetails)
        tvRegisteredDetails=findViewById(R.id.tvRegisteredDetails)
        tvCoordinatesDetails=findViewById(R.id.tvCoordinatesDetails)
    }

    // gets the user data sent by the previous intent
    // and loads the UI if the user data can be retrieved
    private fun getUserData() {
        val userData = intent.getParcelableExtra<User>(USER_DATA)

        if (userData != null) {
            loadUi(userData)
        } else {
            Toast.makeText(this, "User details could not be retrieved.", Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun loadUi(user: User) {

        ivPortraitDetails.loadImage(user.picture.large)

        tvFullNameDetails.text = "${user.name.first} ${user.name.last}"
        tvAddressDetails.text = user.location.street.name
        tvCityDetails.text = "${user.location.city}, ${user.location.state}, ${user.location.country}"
        tvEmailDetails.text = user.email
        tvCellPhoneDetails.text = user.cell
        tvGenderDetails.text= user.gender

        val registrationDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val registrationDate = registrationDateFormat.parse(user.registered.date)
        val formattedRegistrationDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(registrationDate)

        tvRegisteredDetails.text = formattedRegistrationDate

        tvCoordinatesDetails.text= "${user.location.coordinates.latitude} ${user.location.coordinates.longitude}"



        llEmail.setOnClickListener {
            navigateToEmail(user.email)
        }

        // sets the action bar title to the first name of the user
        supportActionBar?.subtitle = "${user.name.first} ${user.name.last}"

    }

    // launch email app to send an mail to the user's mail
    private fun navigateToEmail(email: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        startActivity(Intent.createChooser(emailIntent, "Send email"))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val coordinates = intent.getParcelableExtra<User>(USER_DATA)?.location?.coordinates
        if (coordinates != null) {
            val location = LatLng(coordinates.latitude.toDouble(), coordinates.longitude.toDouble())
            map.addMarker(MarkerOptions().position(location).title("User's Location"))
            map.moveCamera(CameraUpdateFactory.newLatLng(location))
            map.animateCamera(CameraUpdateFactory.zoomTo(10.0f))
        }
    }

    companion object {
        const val USER_DATA = "user_data"
    }


}