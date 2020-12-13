package ipcasergio.am2.creatingeffectswithshaders

import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.floor
import kotlin.random.Random

class SpotViewImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var paint = Paint()
    private var shouldDrawSoptLight = false
    private var gameOver = false

    private lateinit var winnerRect: RectF
    private var androidBitmapY = 0F
    private var androidBitmapX = 0F

    private var shader : Shader

    private val bitmapAndroid = BitmapFactory.decodeResource(
        resources,
        R.drawable.android

    )
    private val spotlight = BitmapFactory.decodeResource( resources, R.drawable.mask)
    private val shaderMatrix = Matrix()


    init {
        val bitmap = Bitmap.createBitmap(spotlight.width, spotlight.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        // Draw a black rectangle.
        shaderPaint.color = Color.BLACK
        canvas.drawRect( 0.0f, 0.0f, spotlight.width.toFloat(), spotlight.height.toFloat(), shaderPaint )

        // Use the DST_OUT compositing mode to mask out the spotlight from the black rectangle.
        shaderPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        canvas.drawBitmap(spotlight, 0.0f, 0.0f, shaderPaint)

        shader = BitmapShader ( bitmap, TileMode.CLAMP, TileMode.CLAMP)
        paint.shader = shader



    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)

        canvas.drawBitmap(bitmapAndroid, androidBitmapX, androidBitmapY, paint)

        if (!gameOver){
            if (shouldDrawSoptLight){
                canvas.drawRect(0.0F, 0.0F, width.toFloat(), height.toFloat(), paint)
            }else{
                canvas.drawColor(Color.BLACK)
            }
        }

    }
    private fun setupWinnerRect(){
        androidBitmapX = floor(Random.nextFloat()*(width - bitmapAndroid.width) )
        androidBitmapY = floor(Random.nextFloat()*(height - bitmapAndroid.height))

        winnerRect = RectF(
            (androidBitmapX),
            (androidBitmapX),
            (androidBitmapX + bitmapAndroid.width),
            (androidBitmapY + bitmapAndroid.height)
        )

    }

    override fun onSizeChanged(
        newWidth: Int,
        newHeight: Int,
        oldWidth: Int,
        oldHeight: Int) {


        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight)
        setupWinnerRect()
    }

    override fun onTouchEvent(montionEvent: MotionEvent): Boolean {
        val motionEventX = montionEvent.x
        val motionEventY = montionEvent.y

        when (montionEvent.action){
            MotionEvent.ACTION_DOWN -> {
                shouldDrawSoptLight = true
                if (gameOver){
                    gameOver = false
                    setupWinnerRect()
                }

            }
            MotionEvent.ACTION_UP ->{
                shouldDrawSoptLight = false
                gameOver = winnerRect.contains(motionEventX, motionEventY)

            }
        }
        shaderMatrix.setTranslate(
            motionEventX - spotlight.width / 2.0F,
            motionEventY - spotlight.height / 2.0f
        )

        shader.setLocalMatrix(shaderMatrix)
        invalidate()

        return true

    }

}

