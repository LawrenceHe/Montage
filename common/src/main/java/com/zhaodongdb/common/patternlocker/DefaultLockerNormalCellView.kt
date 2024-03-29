package com.zhaodongdb.common.patternlocker

import android.graphics.Canvas
import android.graphics.Paint

/**
 * Created by hsg on 22/02/2018.
 */

class DefaultLockerNormalCellView(val styleDecorator: DefaultStyleDecorator) : INormalCellView {
    private val paint: Paint by lazy {
        DefaultConfig.createPaint()
    }

    init {
        this.paint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas, cellBean: CellBean) {
        val saveCount = canvas.save()

        // draw outer circle
        this.paint.color = this.styleDecorator.normalColor
        canvas.drawCircle(cellBean.x, cellBean.y, cellBean.radius, this.paint)

        // draw fill circle
        this.paint.color = this.styleDecorator.fillColor
        canvas.drawCircle(cellBean.x, cellBean.y, cellBean.radius - this.styleDecorator.lineWidth, this.paint)

        canvas.restoreToCount(saveCount)
    }
}
