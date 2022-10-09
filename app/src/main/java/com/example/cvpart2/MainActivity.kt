package com.example.cvpart2
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

const val NAME = "NAME"
const val EMAIL = "EMAIL"
const val AGE = "age"
const val GENDER = "GENDER"

class MainActivity : AppCompatActivity()
{

    private var fullName : TextInputEditText? = null
    private var email : TextInputEditText? = null
    private var age : TextInputEditText? = null

    private var btnNext : Button? = null

    private var male : RadioButton? = null
    private var female : RadioButton? = null

    private var errorName: TextInputLayout?=null
    private var errorEmail: TextInputLayout?=null
    private var errorAge: TextInputLayout?=null

    private var userPic:ImageView?=null

   private var pickedPhoto: Uri? = null
    private  var pickedBitMap: Bitmap? = null


    private val REQUEST_IMAGE_GALLERY=1
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "Create your Resume"

        fullName = findViewById(R.id.fullName)
        email = findViewById(R.id.email)
        age = findViewById(R.id.age)
        btnNext = findViewById(R.id.Next)
        male = findViewById(R.id.Male)
        female = findViewById(R.id.Female)

        errorName= findViewById(R.id.FullName)
        errorEmail= findViewById(R.id.Email)
        errorAge=findViewById(R.id.Age)

        var userPic: ImageView = findViewById(R.id.UserPic)
        btnNext!!.setOnClickListener()
        {
            clickNext()
        }

        userPic.setOnClickListener {

            val galleryIntent=Intent(Intent.ACTION_PICK)
            galleryIntent.type="image/*"
            startActivityForResult(galleryIntent,REQUEST_IMAGE_GALLERY)

        }

    }


    fun pickPhoto(view: View){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent,2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                val galleryIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==2 && resultCode== Activity.RESULT_OK && data != null){
            pickedPhoto=data.data
            if (pickedPhoto != null){
                if (Build.VERSION.SDK_INT>=28){
                    val source= ImageDecoder.createSource(this.contentResolver,pickedPhoto!!)
                    pickedBitMap=ImageDecoder.decodeBitmap(source)
                    userPic?.setImageBitmap(pickedBitMap)

                }
                else{
                    pickedBitMap=MediaStore.Images.Media.getBitmap(this.contentResolver,pickedPhoto)
                    userPic?.setImageBitmap(pickedBitMap)
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun clickNext()
    {
        if(validate())
        {

            val nAme = fullName!!.text.toString()
            val eMail = email!!.text.toString()
            val aGe = age!!.text.toString()
            val gEnder = if(male?.isChecked!!) {
                "Male"
            } else "Female"

            val intent = Intent(this,ThirdActivity::class.java).apply{
                putExtra(NAME,nAme)
                putExtra(EMAIL,eMail)
                putExtra(AGE,aGe)
                putExtra(GENDER,gEnder)
            }
            startActivity(intent)
        }
    }


    private fun validate():Boolean
    {
        var name=true
        var mail=true
        var aGe=true

        errorName?.error =null
        errorEmail?.error =null
        errorAge?.error =null

        if(fullName?.text!!.isEmpty())
        {
            errorName?.error="Must not be empty !"
            name=false
        }
        if(email?.text!!.isEmpty())
        {
            errorEmail?.error="Must not be empty !"
            mail=false
        }
        if(age?.text!!.isEmpty())
        {
            errorAge?.error="Must not be empty !"
            aGe=false
        }

        if(!male?.isChecked!! && !female?.isChecked!! )
        {
            Toast.makeText(this, "Choose your gender !", Toast.LENGTH_SHORT).show()
            return false
        }
        if(!EMAIL_ADDRESS.matcher(email?.text!!).matches() && fullName?.text!!.isNotEmpty())
        {

            errorEmail?.error="Check you email !"
            mail= false
        }

        if (!name || !mail || !aGe)
        {
            return false
        }
        return true
    }}


