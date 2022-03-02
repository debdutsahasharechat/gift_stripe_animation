package com.example.virtualgiftingtest


import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
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
import kotlin.properties.Delegates

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
            Logger.d("SCROLLED!!!!!")
            invalidateHRecyclerView(isScrolling = true)
            super.onScrollStateChanged(recyclerView, newState)
        }
    }

    private var lastDragFraction = 0f //It's just for checking whether the dragFaction is changing or not if not then no need to perform any action
    private val pagerCallBack = object:ViewPager2.OnPageChangeCallback(){
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            pageScrolled(position)
        }
    }

    /********************** Data part **************************************/
    private lateinit var giftData: List<GiftData>
    private var column = DEFAULT_COLUMN_COUNT
    private val rowCount: Int = DEFAULT_ROW_COUNT
    private var lastPageItemCount by Delegates.notNull<Int>()
    private var lastPageIndex by Delegates.notNull<Int>()
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
            clipChildren = false
            elevation = 10f
            addOnScrollListener(scrollListener)
        }
        gridViewPager = ViewPager2(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, VgGiftStripeUtils.viewPagerHeight(context))
            alpha = 0f
        }
        targetThreshold = VgGiftStripeUtils.targetThreshold(context).toFloat()
        addView(gridViewPager)
        addView(horizontalRecyclerView)
        gridViewPager.registerOnPageChangeCallback(pagerCallBack)
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
        lastPageIndex = gridGiftData.size-1
        lastPageItemCount = gridGiftData.lastOrNull()?.size ?: 0
        gridAdapter.setData(gridGiftData)
        gridAdapter.column = column
        gridViewPager.adapter = gridAdapter
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cleanUpListener()
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
    private fun cleanUpListener(){
        gridViewPager.unregisterOnPageChangeCallback(pagerCallBack)
    }

    /*********************************** Drag Logic ************************************/
    /**
     * It will perform all the necessary actions to perform the animation during drag
     */
    override fun performDragFraction(dragFraction: Float) {
        if(isNotDraggable()) return
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
        if(isNotDraggable()) return
        val fraction = if (dragFraction < DISMISS_THRESHOLD) 0f else 1f
        animateCurrentHeight(fraction, onDismiss = true)
        if (isAnimationPossible) {
            animateHorizontalRecyclerView(fraction, onDismiss = true)
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

    private fun pageScrolled(position: Int){
        val firstVisibleItem = position.times(VgGiftStripeUtils.maxElements(column,rowCount))
        val currentFirstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        Logger.d("HERE IS GRID: $firstVisibleItem $currentFirstVisibleItem")
        if(currentFirstVisibleItem != firstVisibleItem && currentFirstVisibleItem >= 0) {
            resetRecyclerView()
            horizontalRecyclerView.scrollToPosition(firstVisibleItem)
            Logger.d("FIRST VISIBLE ITEM: ${layoutManager.findFirstVisibleItemPosition()}")
//            isGridLayoutMeasureDone = false
//            invalidateHRecyclerView(isScrolling = true)
//            invalidateGridLayout()
        }
    }

    private fun resetRecyclerView(){
        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
        for (i in firstVisibleItem until lastVisibleItem+1){
            val holder = horizontalRecyclerView.findViewHolderForAdapterPosition(i) as GiftViewHolder
            holder.itemView.apply {
                translationX = 0f
                translationY = 0f
                scaleX = 1f
                scaleY = 1f
            }
        }
    }

    /**
     * It's for checking if drag is possible or not
     * Checking if user in the last page or not
     * then also checking if the last page is containing less elements than the max elements
     */
    private fun isNotDraggable():Boolean {
        return giftData.isEmpty() || (lastPageIndex == gridViewPager.currentItem && lastPageItemCount < VgGiftStripeUtils.maxElements(column,rowCount))
    }
    /**
     * Calculating all the coordinates and also the height and width for horizontal recycler view
     */
    private fun invalidateHRecyclerView(isScrolling: Boolean = false) {
        if(giftData.isEmpty()) return
        if (!isScrolling && mapOfStartAndEndPoint.size > 0) {
            return
        }
        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        val lastItemPosition = layoutManager.findLastVisibleItemPosition() + 1
        isAnimationPossible = (firstVisibleItem % VgGiftStripeUtils.maxElements(column, rowCount) == 0)
        val currentPage = firstVisibleItem.div(VgGiftStripeUtils.maxElements(column, rowCount))
        Logger.d("FIRST VISIBLE ITEM: $firstVisibleItem $lastItemPosition")
        if (currentPage != gridViewPager.currentItem) {
            gridViewPager.setCurrentItem(currentPage, false)
            isGridLayoutMeasureDone = false
            invalidateGridLayout()
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
        if(giftData.isEmpty() || isGridLayoutMeasureDone) return
        //need to calculate the grid layout x and y points
        Logger.d("HERE I AM FROM GRID!!!!!")
        val gridViewHolder =
            (gridViewPager.getChildAt(0) as RecyclerView).findViewHolderForLayoutPosition(gridViewPager.currentItem) as? GiftGridViewHolder
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
            val containerView = gridLayout?.getChildAt(i)
            val imageView = containerView?.findViewById(R.id.grid_image_item) as? ImageView

            x = containerView?.x?.roundToInt()?.plus(imageView?.x?.roundToInt() ?: 0) ?: 0
            val absoluteYPosition = containerView?.y?.roundToInt()?.plus(imageView?.y?.roundToInt() ?: 0) ?: 0
            y = absoluteYPosition + (topInset - VgGiftStripeUtils.translateHeight(context))
            height = (imageView?.height?.toFloat() ?: 0f)
            width = (imageView?.width?.toFloat() ?: 0f)
            tempPoint = tempPoint?.copy(second = GiftDimension(x, y, height, width)) ?: Pair(
                GiftDimension(),
                GiftDimension(x, y, height, width)
            )
            mapOfStartAndEndPoint[data.uniqueId] = tempPoint
        }
        isGridLayoutMeasureDone = true
        Logger.d("MAP: $mapOfStartAndEndPoint")
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
        val maxElementsToAnimate = firstVisibleItem+VgGiftStripeUtils.maxElements(column,rowCount)
        if(horizontalRecyclerView.alpha == 0f && lastDragFraction != dragFraction){
            horizontalRecyclerView.alpha = 1f
            lastDragFraction = dragFraction
        }
        if(onDismiss){
            lastDragFraction = if(dragFraction < DISMISS_THRESHOLD) 0f else 1f
        }
        for (i in firstVisibleItem until lastItemPosition.coerceAtMost(maxElementsToAnimate)) {
            Logger.d("FirstVisibleItem: $firstVisibleItem LastVisibleItem: $lastItemPosition")
            val viewHolder =
                horizontalRecyclerView.findViewHolderForAdapterPosition(i) as GiftViewHolder
            val data = giftData[i]
            val pair = mapOfStartAndEndPoint[data.uniqueId]
            pair?.let {
                viewHolder.animate(
                    startPoint = pair.first,
                    endPoint = pair.second,
                    dragFraction = dragFraction,
                    onDismiss = onDismiss,
                    afterAnimation = ::animationEndHorizontalRCV
                )
            }
        }
        //So more items is still there but we can't animate them so need to fade out those view holders
        if(lastItemPosition>maxElementsToAnimate){
            for(i in maxElementsToAnimate until lastItemPosition){
                val viewHolder =
                    horizontalRecyclerView.findViewHolderForAdapterPosition(i) as GiftViewHolder
                val data = giftData[i]
                val pair = mapOfStartAndEndPoint[data.uniqueId]
                pair?.let {
                    viewHolder.fadingOut(
                        dragFraction = dragFraction
                    )
                }
            }
        }

    }

    /**
     * It will only animate the fade in effect of the viewpager
     */
    private fun animationEndHorizontalRCV(position:Int,dragFraction: Float){
        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        val maxElementsToAnimate = firstVisibleItem+VgGiftStripeUtils.maxElements(column,rowCount)
        val lastItemPos = layoutManager.findLastVisibleItemPosition().coerceAtMost(maxElementsToAnimate)-1
        if(position == lastItemPos){
            animateGridLayoutRecyclerView(dragFraction = dragFraction, onDismiss = true)
        }
    }

    /**
     * It's for animating the grid viewPager based on the drag fraction
     */
    private fun animateGridLayoutRecyclerView(dragFraction: Float, onDismiss: Boolean) {
        val alphaGrid = VgGiftStripeUtils.lerp(start = 0f, stop = 1f, fraction = dragFraction)
        if(onDismiss){
            lastDragFraction = if(dragFraction < DISMISS_THRESHOLD) 0f else 1f
            gridViewPager
                .animate()
                .alpha(alphaGrid)
                .withEndAction {
                    fadeOutHRCV(dragFraction)
                }
                .start()
        }
        else{
            if(gridViewPager.alpha == 1f && lastDragFraction != dragFraction){
                gridViewPager.alpha = 0f
                lastDragFraction = dragFraction
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




    override fun toString(): String {
        return "VirtualGiftingListContainer"
    }
}

