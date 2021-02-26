package com.liangtg.fivechess

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.properties.Delegates

class GameView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    var game: Game? by Delegates.observable(null) { p, o, n -> calcSize() }
    val whiteDrawable: Drawable? = context?.getDrawable(R.drawable.white)?.mutate()
    val blackDrawable: Drawable? = context?.getDrawable(R.drawable.black)?.mutate()
    private val gameRect: RectF = RectF()
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1f,
            context?.resources?.displayMetrics
        )
    }
    private var cellSize: Float = 0f
    private val drawableRect: Rect = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var size = measuredWidth.coerceAtMost(measuredHeight)
        if (size <= 0) size = measuredWidth.coerceAtLeast(measuredHeight)
        setMeasuredDimension(size, size)
        calcSize()
    }

    private fun calcSize() {
        val game: Game? = game
        if (null == game || measuredWidth <= 0) return
        cellSize = measuredWidth * 1f / (game.gameWidth)
        gameRect.left = cellSize / 2
        gameRect.top = cellSize / 2
        gameRect.right = measuredWidth - cellSize / 2
        gameRect.bottom = measuredWidth - cellSize / 2
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawColor(0xFFE4EDD0.toInt())
        val game: Game = game ?: return
        var lx: Float
        paint.color = 0xFF000000.toInt()
        for (i in 0 until game.gameWidth) {
            lx = gameRect.left + gameRect.width() * 1f * i / (game.gameWidth - 1)
            canvas?.drawLine(gameRect.left, lx, gameRect.right, lx, paint)
            canvas?.drawLine(lx, gameRect.top, lx, gameRect.bottom, paint)
        }
        game.actions.forEachIndexed { i, point ->
            val d: Drawable =
                when (i % 2) {
                    0 -> blackDrawable!!
                    else -> whiteDrawable!!
                }
            drawCell(point, d, canvas!!)
        }
        game.tmpPoint?.let {
            val d = if (game.actions.size % 2 == 0) blackDrawable!! else whiteDrawable!!
            drawCell(it, d, canvas!!)
        }
    }

    private fun drawCell(point: Point, d: Drawable, canvas: Canvas) {
        drawableRect.left = (point.x * cellSize).toInt()
        drawableRect.top = (point.y * cellSize).toInt()
        drawableRect.right = (drawableRect.left + cellSize).toInt()
        drawableRect.bottom = (drawableRect.top + cellSize).toInt()
        drawableRect.inset((cellSize / 4).toInt(), (cellSize / 4).toInt())
        d.bounds = drawableRect
        d.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (game?.gameEnd == true) return false
        val action = event?.action ?: return false
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> tmpLocation(event.x, event.y)
            MotionEvent.ACTION_UP -> endLocation(event!!.x, event!!.y)
        }
        return true
    }

    private fun tmpLocation(x: Float, y: Float) {
        mapLocation(x, y).let {
            if (game!!.canAdd(it.x, it.y)) {
                game!!.addTmpPoint(it.x, it.y)
            } else {
                game!!.removeTmpPoint()
            }
        }
        invalidate()
    }

    private fun endLocation(x: Float, y: Float) {
        mapLocation(x, y).let {
            if (game!!.canAdd(it.x, it.y)) {
                game!!.addChess(it.x, it.y)
            }
            game!!.removeTmpPoint()
        }
        invalidate()
    }

    private fun mapLocation(x: Float, y: Float): Point {
        val width = game!!.gameWidth
        val loc = Point((1.0 * x / cellSize).toInt(), (1.0 * y / cellSize).toInt())
        loc.x = Math.max(0, Math.min(loc.x, width))
        loc.y = Math.max(0, Math.min(loc.y, width))
        return loc
    }

}