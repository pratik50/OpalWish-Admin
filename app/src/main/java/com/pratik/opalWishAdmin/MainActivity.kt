package com.pratik.opalWishAdmin

//noinspection SuspiciousImport
import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pratik.opalWishAdmin.databinding.ActivityMainBinding
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
}
    var productModel = ProductModel()
    var categoryList = arrayOf("T-Shirt", "Shirts", "Mens Bottom", "Dress", "Tops", "Women's Bottom", "Baby Dress", "Winters Collection", "Caps")
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                RESULT_OK -> {
                    binding.rootLayout.setBackgroundColor(Color.LTGRAY)
                    binding.mainLayout.visibility = View.GONE
                    binding.spinKit.visibility = View.VISIBLE

                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    Firebase.storage.reference.child("ProductImage/${UUID.randomUUID()}")
                        .putFile(fileUri).addOnCompleteListener { it ->

                            if (it.isSuccessful) {

                                it.result.storage.downloadUrl.addOnSuccessListener {
                                    productModel.imageUrl = it.toString()
                                    binding.productImage.setImageURI(fileUri)
                                    binding.mainLayout.visibility = View.VISIBLE
                                    binding.spinKit.visibility = View.GONE
                                    binding.rootLayout.setBackgroundColor(Color.WHITE)
                                }


                            }
                        }


                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.productImage.setOnClickListener {
            ImagePicker.with(this).crop().createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
        }
        val arrayAdapter =
            ArrayAdapter(this@MainActivity, R.layout.simple_list_item_1, categoryList)
        binding.category.adapter = arrayAdapter

        binding.category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                productModel.category = categoryList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.addProduct.setOnClickListener {

            var isValid = true

            if (productModel.imageUrl.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please provide Image", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            if (binding.price.text.toString().isEmpty()) {
                binding.price.error = "Please insert the product Price"
                isValid = false
            }

            if (binding.productName.text.toString().isEmpty()) {
                binding.productName.error = "Please insert the product Name"
                isValid = false
            } else {
                val nameText = binding.productName.text.toString().trim()
                val wordCount = nameText.split("\\s+".toRegex()).size

                if (wordCount > 5) {
                    binding.productName.error = "Name should be less than 4 words, please"
                    isValid = false
                }
            }

            if (binding.disp.text.toString().isEmpty()) {
                binding.disp.error = "Please insert the product Description"
                isValid = false
            } else {
                val dispText = binding.disp.text.toString().trim()
                val wordCount = dispText.split("\\s+".toRegex()).size

                if (wordCount > 7) {
                    binding.disp.error = "Description should be less than 7 words, please"
                    isValid = false
                }
            }

            if (binding.details.text.toString().isEmpty()) {
                binding.details.error = "Please insert the product Details"
                isValid = false
            } else {
                val detailsText = binding.details.text.toString().trim()
                val wordCount = detailsText.split("\\s+".toRegex()).size

                if (wordCount < 20) {
                    binding.details.error = "Details should be at least 20 words, please"
                    isValid = false
                }
            }

            if (isValid) {

                productModel.name = binding.productName.text.toString()
                productModel.disp = binding.disp.text.toString()
                productModel.details = binding.details.text.toString()
                productModel.price = binding.price.text.toString().toDouble()

                val key = Firebase.firestore.collection("Products").document().id
                productModel.prouct_id = key

                addProduct()
            }
        }
    }

    fun addProduct(){

        Firebase.firestore.collection("Products").document(UUID.randomUUID().toString())
            .set(productModel).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity,
                        "product Added !!!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to add Product",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

}