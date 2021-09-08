package com.adityaagusw.pdfkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.adityaagusw.pdfkotlin.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //cover header
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_cover)
        scaleBitmap = Bitmap.createScaledBitmap(bitmap, 1200, 518, false)

        //permission
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PackageManager.PERMISSION_GRANTED
        )
        createPDF()
    }

    private fun createPDF() = with(binding) {
        btnPrint.setOnClickListener {
            dateTime = Date()

            //get input
            if (etName.text.toString().isEmpty() || etNoTlp.text.toString()
                    .isEmpty() || etJmlOne.text.toString().isEmpty() || etJmlTwo.text.toString()
                    .isEmpty()
            ) {
                Toast.makeText(this@MainActivity, "Data tidak boleh kosong!", Toast.LENGTH_LONG)
                    .show()
            } else {
                val pdfDocument = PdfDocument()
                val paint = Paint()
                val titlePaint = Paint()
                val pageInfo = PageInfo.Builder(1200, 2010, 1).create()

                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                canvas.drawBitmap(scaleBitmap, 0f, 0f, paint)
                paint.color = Color.WHITE
                paint.textSize = 30f
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText("Berbagai macam jenis Kopi", 1160f, 40f, paint)
                canvas.drawText("Pesan di : 08123456789", 1160f, 80f, paint)
                titlePaint.textAlign = Paint.Align.CENTER
                titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                titlePaint.color = Color.WHITE
                titlePaint.textSize = 70f
                canvas.drawText("Tagihan Anda", (pageWidth / 2).toFloat(), 500f, titlePaint)
                paint.textAlign = Paint.Align.LEFT
                paint.color = Color.BLACK
                paint.textSize = 35f
                canvas.drawText("Nama Pemesan: " + etName.text, 20f, 590f, paint)
                canvas.drawText("Nomor Tlp: " + etNoTlp.text, 20f, 640f, paint)
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText("No. Pesanan: " + "232425", (pageWidth - 20).toFloat(), 590f, paint)
                dateFormat = SimpleDateFormat("dd/MM/yy")
                canvas.drawText(
                    "Tanggal: " + dateFormat.format(dateTime),
                    (pageWidth - 20).toFloat(),
                    640f,
                    paint
                )
                dateFormat = SimpleDateFormat("HH:mm:ss")
                canvas.drawText(
                    "Pukul: " + dateFormat.format(dateTime),
                    (pageWidth - 20).toFloat(),
                    690f,
                    paint
                )
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 2f
                canvas.drawRect(20f, 780f, (pageWidth - 20).toFloat(), 860f, paint)
                paint.textAlign = Paint.Align.LEFT
                paint.style = Paint.Style.FILL
                canvas.drawText("No.", 40f, 830f, paint)
                canvas.drawText("Menu Pesanan", 200f, 830f, paint)
                canvas.drawText("Harga", 700f, 830f, paint)
                canvas.drawText("Jumlah", 900f, 830f, paint)
                canvas.drawText("Total", 1050f, 830f, paint)
                canvas.drawLine(180f, 790f, 180f, 840f, paint)
                canvas.drawLine(680f, 790f, 680f, 840f, paint)
                canvas.drawLine(880f, 790f, 880f, 840f, paint)
                canvas.drawLine(1030f, 790f, 1030f, 840f, paint)
                var totalOne = 0f
                var totalTwo = 0f
                if (itemSpinnerOne.selectedItemPosition != 0) {
                    canvas.drawText("1.", 40f, 950f, paint)
                    canvas.drawText(itemSpinnerOne.selectedItem.toString(), 200f, 950f, paint)
                    canvas.drawText(
                        harga[itemSpinnerOne.selectedItemPosition].toString(),
                        700f,
                        950f,
                        paint
                    )
                    canvas.drawText(etJmlOne.text.toString(), 900f, 950f, paint)
                    totalOne = etJmlOne.text.toString()
                        .toFloat() * harga[itemSpinnerOne.selectedItemPosition]
                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(totalOne.toString(), (pageWidth - 40).toFloat(), 950f, paint)
                    paint.textAlign = Paint.Align.LEFT
                }
                if (itemSpinnerTwo.selectedItemPosition != 0) {
                    canvas.drawText("2.", 40f, 1050f, paint)
                    canvas.drawText(itemSpinnerTwo.selectedItem.toString(), 200f, 1050f, paint)
                    canvas.drawText(
                        harga[itemSpinnerTwo.selectedItemPosition].toString(),
                        700f,
                        1050f,
                        paint
                    )
                    canvas.drawText(etJmlTwo.text.toString(), 900f, 1050f, paint)
                    totalTwo = etJmlTwo.text.toString()
                        .toFloat() * harga[itemSpinnerTwo.selectedItemPosition]
                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(totalTwo.toString(), (pageWidth - 40).toFloat(), 1050f, paint)
                    paint.textAlign = Paint.Align.LEFT
                }
                val subTotal = totalOne + totalTwo
                canvas.drawLine(400f, 1200f, (pageWidth - 20).toFloat(), 1200f, paint)
                canvas.drawText("Sub Total", 700f, 1250f, paint)
                canvas.drawText(":", 900f, 1250f, paint)
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText(subTotal.toString(), (pageWidth - 40).toFloat(), 1250f, paint)
                paint.textAlign = Paint.Align.LEFT
                canvas.drawText("PPN (10%)", 700f, 1300f, paint)
                canvas.drawText(":", 900f, 1300f, paint)
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText(
                    (subTotal * 10 / 100).toString(),
                    (pageWidth - 40).toFloat(),
                    1300f,
                    paint
                )
                paint.textAlign = Paint.Align.LEFT
                paint.color = Color.rgb(247, 147, 30)
                canvas.drawRect(680f, 1350f, (pageWidth - 20).toFloat(), 1450f, paint)
                paint.color = Color.BLACK
                paint.textSize = 50f
                paint.textAlign = Paint.Align.LEFT
                canvas.drawText("Total", 700f, 1415f, paint)
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText(
                    (subTotal + subTotal * 10 / 100).toString(),
                    (pageWidth - 40).toFloat(),
                    1415f,
                    paint
                )
                pdfDocument.finishPage(page)
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "${System.currentTimeMillis()}.pdf"
                )
                try {
                    pdfDocument.writeTo(FileOutputStream(file))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                pdfDocument.close()
                Toast.makeText(this@MainActivity, "PDF sudah dibuat", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private lateinit var bitmap: Bitmap
        private lateinit var scaleBitmap: Bitmap
        private var pageWidth = 1200
        private lateinit var dateTime: Date
        private lateinit var dateFormat: DateFormat
        var harga = floatArrayOf(0f, 21000f, 22000f, 25000f, 22500f, 21500f)
    }

}