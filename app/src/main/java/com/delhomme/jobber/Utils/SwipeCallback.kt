package com.delhomme.jobber.Utils


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class SwipeCallback(
    context: Context,
    private val onSwipedLeft: (Int) -> Unit,
    private val onSwipedRight: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteIcon: Drawable = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete)!!
    private val editIcon: Drawable = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_edit)!!
    private val intrinsicWidth = deleteIcon.intrinsicWidth
    private val intrinsicHeight = deleteIcon.intrinsicHeight
    private val backgroundDelete = ColorDrawable()
    private val backgroundEdit = ColorDrawable()
    private val backgroundColorDelete = Color.parseColor("#f44336")
    private val backgroundColorEdit = Color.parseColor("#4CAF50")
    private val clearPaint = Paint().apply { xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR) }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.LEFT -> onSwipedLeft(viewHolder.adapterPosition)
            ItemTouchHelper.RIGHT -> onSwipedRight(viewHolder.adapterPosition)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        when {
            dX > 0 -> { // Swiping to the right
                backgroundEdit.color = backgroundColorEdit
                backgroundEdit.setBounds(
                    itemView.left, itemView.top, itemView.left + dX.toInt(),
                    itemView.bottom
                )
                backgroundEdit.draw(c)

                val editIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val editIconMargin = (itemHeight - intrinsicHeight) / 2
                val editIconLeft = itemView.left + editIconMargin
                val editIconRight = itemView.left + editIconMargin + intrinsicWidth
                val editIconBottom = editIconTop + intrinsicHeight

                editIcon.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
                editIcon.draw(c)
            }
            dX < 0 -> { // Swiping to the left
                backgroundDelete.color = backgroundColorDelete
                backgroundDelete.setBounds(
                    itemView.right + dX.toInt(), itemView.top, itemView.right,
                    itemView.bottom
                )
                backgroundDelete.draw(c)

                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight

                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                deleteIcon.draw(c)
            }
            else -> {
                backgroundEdit.setBounds(0, 0, 0, 0)
                backgroundDelete.setBounds(0, 0, 0, 0)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}