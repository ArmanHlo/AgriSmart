package com.shoping.agrismartapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.shoping.agrismartapp.data.local.entity.FarmActivityEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfReportGenerator {

    fun generateFarmReport(context: Context, activities: List<FarmActivityEntity>) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        titlePaint.textSize = 20f
        titlePaint.isFakeBoldText = true
        canvas.drawText("KrishiMitra - Farm Journal Report", 150f, 50f, titlePaint)

        paint.textSize = 12f
        var y = 100f

        canvas.drawText("Generated on: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}", 50f, y, paint)
        y += 30f

        // Table Header
        paint.isFakeBoldText = true
        canvas.drawText("Date", 50f, y, paint)
        canvas.drawText("Activity", 150f, y, paint)
        canvas.drawText("Crop", 300f, y, paint)
        canvas.drawText("Cost (₹)", 450f, y, paint)
        canvas.drawText("Income (₹)", 520f, y, paint)
        y += 20f
        canvas.drawLine(50f, y, 550f, y, paint)
        y += 20f

        paint.isFakeBoldText = false
        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

        activities.forEach { activity ->
            if (y > 800) {
                // Should handle new page in a real app
            }
            canvas.drawText(dateFormat.format(Date(activity.date)), 50f, y, paint)
            canvas.drawText(activity.type, 150f, y, paint)
            canvas.drawText(activity.cropName, 300f, y, paint)
            canvas.drawText(activity.cost?.toString() ?: "0", 450f, y, paint)
            canvas.drawText(activity.income?.toString() ?: "0", 520f, y, paint)
            y += 20f
        }

        pdfDocument.finishPage(page)

        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "FarmReport_${System.currentTimeMillis()}.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
            Toast.makeText(context, "Report saved to: ${filePath.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error generating PDF", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}
