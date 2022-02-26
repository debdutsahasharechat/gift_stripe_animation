package com.example.virtualgiftingtest


import android.content.Context
import android.util.AttributeSet
import android.view.*
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.virtualgiftingtest.VgGiftStripeConstant.DEFAULT_COLUMN_COUNT
import com.example.virtualgiftingtest.VgGiftStripeConstant.DEFAULT_ROW_COUNT
import com.example.virtualgiftingtest.VgGiftStripeConstant.DISMISS_THRESHOLD
import com.example.virtualgiftingtest.vgrecyclerview.grid.GiftGridAdapter
import com.example.virtualgiftingtest.vgrecyclerview.grid.GiftGridViewHolder
import com.example.virtualgiftingtest.vgrecyclerview.horizontal.GiftAdapter
import com.example.virtualgiftingtest.vgrecyclerview.horizontal.GiftViewHolder
import kotlin.math.roundToInt

class VirtualGiftingListContainer @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : VirtualGiftingTouchHelper(context, attributeSet) {

    /****************************** View Part *******************************/
    private var horizontalRecyclerView: RecyclerView
    private val adapter by lazy { GiftAdapter() }
    private val gridAdapter by lazy { GiftGridAdapter() }
    private val layoutManager by lazy {
        LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }
    private var gridViewPager: ViewPager2

    private var isAnimationPossible = true

    private var isGridLayoutMeasureDone = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            invalidateHRecyclerView(isScrolling = true)
        }
    }

    /********************** Data part **************************************/
    private lateinit var giftData: List<GiftData>
    private var column = DEFAULT_COLUMN_COUNT
    private val rowCount: Int = DEFAULT_ROW_COUNT

    private val mapOfStartAndEndPoint = hashMapOf<String, Pair<GiftDimension, GiftDimension>>()

    /************************* Helper variables ********************************/


    /************************* Initialization Part **********************************/
    init {
        clipChildren = false
        horizontalRecyclerView = RecyclerView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                VgGiftStripeUtils.itemHeight(context, isExpanded = false)
            ).apply {
                gravity = Gravity.BOTTOM
            }
            addOnScrollListener(scrollListener)
        }
        gridViewPager = ViewPager2(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, VgGiftStripeUtils.viewPagerHeight(context))
            alpha = 0f
            //visibility = View.INVISIBLE
        }
        targetThreshold = VgGiftStripeUtils.targetThreshold(context).toFloat()
        addView(gridViewPager)
        addView(horizontalRecyclerView)
    }

    /**
     * It will setup the horizontal recyclerview
     */
    private fun setUpRecyclerView() {
        adapter.setData(data = giftData)
        horizontalRecyclerView.adapter = adapter
        horizontalRecyclerView.layoutManager = layoutManager
        horizontalRecyclerView.clipChildren = false
    }

    /**
     * It will set up view pager
     */
    private fun setUpViewPager() {
        val gridGiftData = giftData.chunked(size = VgGiftStripeUtils.maxElements(column, rowCount))
        gridAdapter.setData(gridGiftData)
        gridAdapter.column = column
        gridViewPager.adapter = gridAdapter
    }

    /********************************** Setters ****************************************/

    /**
     * It will set the data from outside
     */
    fun setData(data: List<GiftData>) {
        this.giftData = data
        setUpRecyclerView()
        setUpViewPager()
    }

    /******************************* Clean up logic ***********************************/


    /*********************************** Drag Logic ************************************/
    /**
     * It will perform all the necessary actions to perform the animation during drag
     */
    override fun performDragFraction(dragFraction: Float) {
        animateCurrentHeight(dragFraction, onDismiss = false)
        if (isAnimationPossible) {
            animateHorizontalRecyclerView(dragFraction, onDismiss = false)
            animateGridLayoutRecyclerView(dragFraction, onDismiss = false)
        } else {
            //fallBack animation
            crossFadeAnimation(dragFraction, onDismiss = false)
        }
    }

    /**
     * It will do the necessary animation actions after user leave the control
     */
    override fun onDismissFraction(dragFraction: Float) {
        val fraction = if (dragFraction < DISMISS_THRESHOLD) 0f else 1f
        animateCurrentHeight(fraction, onDismiss = true)
        if (isAnimationPossible) {
            animateHorizontalRecyclerView(fraction, onDismiss = true)
            animateGridLayoutRecyclerView(fraction, onDismiss = true)
        } else {
            crossFadeAnimation(dragFraction = fraction, onDismiss = true)
        }
    }

    /****************************** Helper functions *********************************/

    /**
     * It will be called after the whole children has been drawn by the parent frame layout
     */
    override fun onLayoutChange() {
        invalidateHRecyclerView()
        invalidateGridLayout()
    }

    /**
     * Calculating all the coordinates and also the height and width for horizontal recycler view
     */
    private fun invalidateHRecyclerView(isScrolling: Boolean = false) {
        if (!isScrolling && mapOfStartAndEndPoint.size > 0) {
            return
        }
        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        val lastItemPosition = layoutManager.findLastVisibleItemPosition() + 1
        val topDeckLastItem = firstVisibleItem + column
        val restOfDecCount = (lastItemPosition - topDeckLastItem)
        isAnimationPossible = (firstVisibleItem % VgGiftStripeUtils.maxElements(column, rowCount) == 0)
        val currentPage = firstVisibleItem.div(VgGiftStripeUtils.maxElements(column, rowCount))
        if (currentPage != gridViewPager.currentItem) {
            gridViewPager.setCurrentItem(currentPage, false)
        }
        var data: GiftData
        var tempPoint: Pair<GiftDimension, GiftDimension>?
        var x: Int
        var y: Int
        var width: Float
        var height: Float
        for (i in firstVisibleItem until lastItemPosition) {
            data = giftData[i]
            tempPoint = mapOfStartAndEndPoint[data.uniqueId]
            x =
                horizontalRecyclerView.findViewHolderForAdapterPosition(i)?.itemView?.x?.roundToInt()
                    ?: 0
            y =
                (horizontalRecyclerView.findViewHolderForAdapterPosition(i)?.itemView?.y?.roundToInt()
                    ?: 0) + topInset
            width =
                (horizontalRecyclerView.findViewHolderForAdapterPosition(i)?.itemView?.width?.toFloat()
                    ?: 0f)
            height =
                horizontalRecyclerView.findViewHolderForAdapterPosition(i)?.itemView?.height?.toFloat()
                    ?: 0f
            //Logger.d("X VALUE: $x and $y $topInset")
            tempPoint = tempPoint?.copy(first = GiftDimension(x, y, height, width)) ?: Pair(
                GiftDimension(
                    x,
                    y,
                    height,
                    width
                ), GiftDimension()
            )
            mapOfStartAndEndPoint[data.uniqueId] = tempPoint
        }
    }

    /**
     * Calculating the all the coordinates and also the height width of Gridlayout of viewpager current item
     */
    private fun invalidateGridLayout() {
        //need to calculate the grid layout x and y points
        if (isGridLayoutMeasureDone) {
            return
        }
        val gridViewHolder =
            (gridViewPager.getChildAt(0) as RecyclerView).findViewHolderForLayoutPosition(0) as? GiftGridViewHolder
        val gridLayout = gridViewHolder?.view
        val elementPerPage = gridLayout?.childCount ?: 0
        val currentPage = gridViewPager.currentItem
        var currentIndex: Int
        var data: GiftData
        var tempPoint: Pair<GiftDimension, GiftDimension>?
        var x: Int
        var y: Int
        var height: Float
        var width: Float
        for (i in 0 until elementPerPage) {
            currentIndex = (currentPage * elementPerPage) + i
            data = giftData[currentIndex]
            tempPoint = mapOfStartAndEndPoint[data.uniqueId]
            x = gridLayout?.getChildAt(i)?.x?.roundToInt() ?: 0
            y = (gridLayout?.getChildAt(i)?.y?.roundToInt()
                ?: 0) + (topInset - VgGiftStripeUtils.translateHeight(context))
            height = (gridLayout?.getChildAt(i)?.height?.toFloat() ?: 0f) - VgGiftStripeUtils.textHeight(
                context
            )
            width = ((gridLayout?.getChildAt(i))?.width?.toFloat() ?: 0f)
            tempPoint = tempPoint?.copy(second = GiftDimension(x, y, height, width)) ?: Pair(
                GiftDimension(),
                GiftDimension(x, y, height, width)
            )
            mapOfStartAndEndPoint[data.uniqueId] = tempPoint
        }
        isGridLayoutMeasureDone = true
    }


    /****************************** Animation logic **********************************/
    /**
     * Animating the current height
     * @param dragFraction How much user drags basically it's a percentage drag
     * @param onDismiss It's a boolean indicator indicating the trigger point if it is true it is coming from performDrag event other wise from onDismiss event
     */
    private fun animateCurrentHeight(dragFraction: Float, onDismiss: Boolean) {
        val currentHeight = VgGiftStripeUtils.lerp(
            start = originalHeight,
            stop = targetThreshold,
            fraction = dragFraction
        )
        if (!onDismiss) {
            updateLayoutParams {
                this.height = currentHeight.toInt()
            }
        } else {
            animateHeight(
                currentHeight = layoutParams.height,
                targetHeight = currentHeight.toInt()
            )
        }
    }

    /**
     * Animating the horizontal recyclerview based on the dragFraction
     */
    private fun animateHorizontalRecyclerView(dragFraction: Float, onDismiss: Boolean) {
        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        val lastItemPosition = layoutManager.findLastVisibleItemPosition() + 1
        if(horizontalRecyclerView.alpha == 0f){
            horizontalRecyclerView.alpha = 1f
        }
        for (i in firstVisibleItem until lastItemPosition) {
            val viewHolder =
                horizontalRecyclerView.findViewHolderForAdapterPosition(i) as GiftViewHolder
            val data = giftData[i]
            val pair = mapOfStartAndEndPoint[data.uniqueId]
            pair?.let {
                viewHolder.animate(
                    startPoint = pair.first,
                    endPoint = pair.second,
                    dragFraction = dragFraction,
                    onDismiss = onDismiss
                )
            }
        }

    }

    /**
     * It's a fallback animation when the actual animation is not possible
     * If user drags the horizontal recycler view to a position from where you can't map the items to the specific grid item then only this animation will be done
     * */
    private fun crossFadeAnimation(dragFraction: Float, onDismiss: Boolean) {
        val alphaHVg = VgGiftStripeUtils.lerp(start = 1f, stop = 0f, fraction = dragFraction)
        val alphaGridVG = VgGiftStripeUtils.lerp(start = 0f, stop = 1f, fraction = dragFraction)
        if (onDismiss.not()) {
            horizontalRecyclerView.apply {
                alpha = alphaHVg
            }
            gridViewPager.apply {
                alpha = alphaGridVG
            }
        } else {
            horizontalRecyclerView
                .animate()
                .alpha(alphaHVg)
                .start()
            gridViewPager
                .animate()
                .alpha(alphaGridVG)
                .start()
        }
    }

    /**
     * It's for animating the grid viewPager based on the drag fraction
     */
    private fun animateGridLayoutRecyclerView(dragFraction: Float, onDismiss: Boolean) {
        val alphaGrid = VgGiftStripeUtils.lerp(start = 0f, stop = 1f, fraction = dragFraction)
        if(onDismiss){
            gridViewPager
                .animate()
                .alpha(alphaGrid)
                .withEndAction {
                    fadeOutHRCV(dragFraction)
                }
                .start()
        }else{
            if(gridViewPager.alpha == 1f){
                gridViewPager.alpha = 0f
            }
        }
    }

    private fun fadeOutHRCV(dragFraction: Float){
        val alphaHRV = VgGiftStripeUtils.lerp(start = 1f, stop = 0f, fraction = dragFraction)
        horizontalRecyclerView
            .animate()
            .alpha(alphaHRV)
            .start()
    }


    override fun toString(): String {
        return "VirtualGiftingListContainer"
    }
}

