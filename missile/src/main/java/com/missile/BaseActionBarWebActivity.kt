package com.missile

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Slide
import android.util.TypedValue
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.missile.entity.ActionSetRightButtonByIndex
import com.missile.entity.PageInfo
import com.missile.entity.RightButtonIndex
import com.missile.entity.TitleBarInfo
import com.missile.plugin.PluginManager
import com.missile.util.ConfigXmlParser
import com.missile.util.WebViewEngine
import java.util.*

/**
 * 处理原生标题栏加webView结构交互逻辑的类
 */
abstract class BaseActionBarWebActivity : AppCompatActivity() {
    //页面栈
    var pages = Stack<PageInfo>()
    private var menu: PopupWindow? = null
    protected lateinit var titleBar: View
    protected lateinit var titleBarContent: View
    private lateinit var titleNormalContainer: View
    private lateinit var titleSpinner: ImageView
    private lateinit var titleTv: TextView
    private lateinit var titleSearchContainer: View
    private lateinit var leftText: TextView
    private lateinit var leftIcon: ImageView
    private lateinit var progress: ProgressBar
    private lateinit var titleEt: EditText
    private lateinit var titleEtBtn: ImageView
    protected lateinit var webView: WebViewEngine
    private var showProgress = true
    private var curProgress: Float = 0.toFloat()

    private val statusBarHeight: Int
        get() {
            var statusBarHeight = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusBarHeight = resources.getDimensionPixelSize(resourceId)
            }
            return statusBarHeight
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_actionbar)
        webActivityCount++
        initViews()
        PluginManager.loadPlugins(this, webView)

        //设置状态栏透明,通过标题栏模拟沉浸状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4 全透明状态栏
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
            }

            val statusBarHeight = statusBarHeight

            val actionBarParam = titleBar.layoutParams
            actionBarParam.height += statusBarHeight
            titleBar.layoutParams = actionBarParam

            val contentParam = titleBarContent.layoutParams as LinearLayout.LayoutParams
            contentParam.setMargins(0, statusBarHeight, 0, 0)
            titleBarContent.layoutParams = contentParam
        }

        loadUrl(PageInfo(intent.getStringExtra("url"),
                Gson().fromJson(intent.getStringExtra("titleBarInfo"), TitleBarInfo::class.java),
                intent.getStringExtra("params")))
    }

    private fun initViews() {
        webView = ConfigXmlParser.getEngine(this)
        webView.init(this)
        val webContainer = findViewById<FrameLayout>(R.id.webView)
        webContainer.addView(webView.view)
        progress = findViewById(R.id.title_bar_progress)
        titleBar = findViewById(R.id.title_bar)
        titleBarContent = findViewById(R.id.title_bar_content)
        titleNormalContainer = findViewById(R.id.title_bar_title_normal_container)
        titleSearchContainer = findViewById(R.id.title_bar_title_search_container)
        titleSpinner = findViewById(R.id.title_bar_title_spinner)
        titleTv = findViewById(R.id.title_bar_title)
        titleEt = findViewById(R.id.title_bar_title_search)
        titleEtBtn = findViewById(R.id.title_bar_title_search_btn)
        leftText = findViewById(R.id.left_text)
        leftIcon = findViewById(R.id.left_icon)
    }

    @JvmOverloads
    fun onEvent(event: String, obj: Any? = null) {
        when (event) {
            "onPageStart" -> {
                execJs(pages.lastElement().onLoadStartedCallback, pages.lastElement().params)
                if (showProgress) {
                    progress.visibility = View.VISIBLE
                    progressTimer.schedule(object : TimerTask() {
                        override fun run() {
                            if (!showProgress) {
                                curProgress = 0f
                                cancel()
                                runOnUiThread { progress.visibility = View.GONE }
                            }
                            curProgress += (if (curProgress > 50) if (curProgress > 75) 0.4f else 0.7f else 1f)
                            if (curProgress >= 100) {
                                cancel()
                            }
                            if (Build.VERSION.SDK_INT >= 24)
                            //setProgress 做了线程同步处理，不存在UI线程问题
                                progress.setProgress(curProgress.toInt(), true)
                            else
                                progress.progress = curProgress.toInt()
                        }
                    }, 50, 50)
                }
            }
            "onPageFinished" -> {
                execJs(pages.lastElement().onLoadStoppedCallback, pages.lastElement().params)
                if (showProgress) {
                    progressTimer.schedule(object : TimerTask() {
                        override fun run() {
                            curProgress += 5f
                            if (!showProgress || curProgress >= 100) {
                                curProgress = 0f
                                cancel()
                                runOnUiThread { progress.visibility = View.GONE }
                            }
                            progress.progress = curProgress.toInt()
                        }
                    }, 10, 10)
                }
            }
        }
    }

    open fun loadUrl(info: PageInfo) {
        if (info.url == null)
            return

        //替换url
        //        try {
        //            if (UrlParserUtil.shouldRepalceLoadUrl(info.url))
        //                info.url = UrlParserUtil.ReplaceLoadUrl(info.url);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //            return;
        //        }

        val url = info.url
        webView.loadUrl(url)
        pages.push(info)

        if (info.titleBarInfo == null)
            info.titleBarInfo = TitleBarInfo()
        setTitleBar(info.titleBarInfo)
    }

    override fun onBackPressed() {
        handleBackClick()
    }

    protected fun handleBackClick() {
        val callback = pages.lastElement().titleBarInfo.leftCallback
        if (callback == null || callback == "") {
            goBack(1, null)
        } else {
            execJs(callback)
        }
    }

    fun goBack(backCount: Int, backParams: String?) {
        val intent = Intent()
        intent.putExtra("backParams", backParams)
        if (pages.size - backCount > 0) {
            intent.putExtra("backCount", backCount + 1)//因为是本activity内调用，故加1
            onActivityResult(0, 0, intent)
        } else {
            intent.putExtra("backCount", backCount - pages.size + 1)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (intent != null && intent.extras != null) {
            var backCount = intent.getIntExtra("backCount", 0)

            if (backCount > 1) {//中文习惯，从1开始
                backCount--
                if (pages.size - backCount > 0) {//activity内
                    while (backCount-- > 0) {
                        pages.pop()
                    }
                    val info = pages.lastElement().titleBarInfo
                    info.titleSpinnerOpen = false
                    setTitleBar(info)

                    execJs(pages.lastElement().onResultCallback, intent.getStringExtra("backParams"))
                } else {
                    intent.putExtra("backCount", backCount + 1 - pages.size)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            } else {//已回到了目标页
                execJs(pages.lastElement().onResultCallback, intent.getStringExtra("backParams"))
            }
        }
    }

    /******************************* methods about title  */
    fun setTitleBar(titleBarInfoNew: TitleBarInfo?) {
        if (titleBarInfoNew == null) {
            leftIcon.visibility = View.VISIBLE
            leftText.isClickable = false
            leftIcon.setImageResource(R.drawable.ic_title_back)
            leftIcon.setOnClickListener { handleBackClick() }
            return
        }
        if (pages.lastElement().titleBarInfo == null)
            pages.lastElement().titleBarInfo = titleBarInfoNew
        else
            pages.lastElement().titleBarInfo.change(titleBarInfoNew)
        val titleBarInfo = pages.lastElement().titleBarInfo


        if (titleBarInfo.hideTitleAndStatus != null && titleBarInfo.hideTitleAndStatus) {
            titleBar.visibility = View.GONE
            return
        } else if (titleBarInfo.hideTitle != null && titleBarInfo.hideTitle) {
            titleBar.visibility = View.VISIBLE
            titleBarContent.visibility = View.GONE
            val actionBarParam = titleBar.layoutParams
            actionBarParam.height = statusBarHeight
            titleBar.layoutParams = actionBarParam
            return
        } else {
            titleBar.visibility = View.VISIBLE
            titleBarContent.visibility = View.VISIBLE
            val actionBarParam = titleBar.layoutParams
            actionBarParam.height = (statusBarHeight + resources.getDimension(R.dimen.actionbar_height)).toInt()
            titleBar.layoutParams = actionBarParam
        }

        if ((titleBarInfo.darkStatusBarText != null && titleBarInfo.darkStatusBarText || titleBarInfo.darkStatusBarText == null && titleBarInfo.titleBarColor != null && isLightEnough(titleBarInfo.titleBarColor)) && Build.VERSION.SDK_INT >= 23) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        if (titleBarInfo.titleBarColor != null) {
            titleBar.setBackgroundColor(Color.parseColor(titleBarInfo.titleBarColor))
        } else {
            titleBar.setBackgroundColor(resources.getColor(R.color.actionbar_bg))
        }

        if (titleBarInfo.leftIcon != null) {
            when (titleBarInfo.leftIcon) {
                "hide" -> {
                    leftIcon.visibility = View.GONE
                    leftText.gravity = Gravity.CENTER
                    leftText.isClickable = true
                    leftText.setOnClickListener { handleBackClick() }
                }
                else -> {
                    leftIcon.visibility = View.VISIBLE
                    leftText.isClickable = false
                    leftText.gravity = Gravity.CENTER_VERTICAL
                    Glide.with(this@BaseActionBarWebActivity).load(titleBarInfo.leftIcon).into(leftIcon)
                    leftIcon.setOnClickListener { handleBackClick() }
                }
            }
        } else {
            leftIcon.visibility = View.VISIBLE
            leftText.isClickable = false
            leftText.gravity = Gravity.CENTER_VERTICAL
            leftIcon.setImageResource(R.drawable.ic_title_back)
            leftIcon.setOnClickListener { handleBackClick() }
        }

        if (titleBarInfo.leftText != null) {
            leftText.visibility = View.VISIBLE
            leftText.text = titleBarInfo.leftText
        } else {
            leftText.visibility = View.GONE
            leftText.isClickable = false
        }
        if (titleBarInfo.leftTextColor != null) {
            leftText.setTextColor(Color.parseColor(titleBarInfo.leftTextColor))
        } else {
            leftText.setTextColor(resources.getColor(android.R.color.white))
        }
        if (titleBarInfo.leftTextSize != null && titleBarInfo.leftTextSize > 0) {
            leftText.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleBarInfo.leftTextSize.toFloat())
        } else {
            leftText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }

        if (titleBarInfo.titleType == null)
            titleBarInfo.titleType = TYPE_NORMAL
        when (titleBarInfo.titleType) {
            TYPE_SEARCH -> {
                titleNormalContainer.visibility = View.GONE
                titleSearchContainer.visibility = View.VISIBLE
                titleEtBtn.setOnClickListener { execJs(titleBarInfo.titleCallback, titleEt.text.toString()) }
                if (titleBarInfo.searchTextColor != null) {
                    titleEt.setTextColor(Color.parseColor(titleBarInfo.searchTextColor))
                } else {//not default,can not use needReset
                    titleEt.setTextColor(Color.GRAY)
                }
                if (titleBarInfo.searchTextSize != null && titleBarInfo.searchTextSize > 0) {
                    titleEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleBarInfo.searchTextSize.toFloat())
                } else {
                    titleEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                }
                if (titleBarInfo.searchBtnImg != null) {
                    Glide.with(this@BaseActionBarWebActivity).load(titleBarInfo.searchBtnImg).into(titleEtBtn)
                } else {
                    titleEtBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_title_search))
                }
                if (titleBarInfo.searchBg != null) {
                    Glide.with(this@BaseActionBarWebActivity).load(titleBarInfo.searchBg).into(object : SimpleTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            titleEt.background = resource
                        }
                    })
                } else {
                    titleEt.setBackgroundColor(Color.parseColor("#dddddd"))
                }
                if (titleBarInfo.searchHintColor != null) {
                    titleEt.setHintTextColor(Color.parseColor(titleBarInfo.searchHintColor))
                } else {
                    titleEt.setHintTextColor(Color.GRAY)
                }
                if (titleBarInfo.searchHint != null) {
                    titleEt.hint = titleBarInfo.searchHint
                } else {
                    titleEt.hint = ""
                }
            }
            TYPE_WITH_SPINNER, TYPE_NORMAL -> {
                titleNormalContainer.visibility = View.VISIBLE
                titleSearchContainer.visibility = View.GONE
                if (titleBarInfo.title != null) {
                    titleTv.text = titleBarInfo.title
                } else {
                    titleTv.text = ""
                }

                if (titleBarInfo.titleSpinnerImg != null)
                    Glide.with(titleSpinner).load(titleBarInfo.titleSpinnerImg).into(titleSpinner)
                else
                    titleSpinner.setImageDrawable(resources.getDrawable(R.drawable.ic_title_spinner))

                if (titleBarInfo.titleCallback == null || titleBarInfo.titleCallback == "" || titleBarInfo.titleType == TYPE_NORMAL) {
                    titleSpinner.visibility = View.GONE
                } else {
                    titleSpinner.visibility = View.VISIBLE
                }

                if (titleBarInfo.titleType == TYPE_WITH_SPINNER)
                    changeSpinner(titleBarInfo.titleSpinnerOpen != null && titleBarInfo.titleSpinnerOpen)

                titleNormalContainer.setOnClickListener {
                    execJs(titleBarInfo.titleCallback)
                    if (titleBarInfo.titleType == TYPE_WITH_SPINNER)
                        changeSpinner(!(titleBarInfo.titleSpinnerOpen != null && titleBarInfo.titleSpinnerOpen))
                }
                if ("left" == titleBarInfo.titlePosition) {
                    leftText.visibility = View.GONE
                    leftIcon.visibility = View.VISIBLE
                    leftIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_title_back))
                    val lp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    lp.leftMargin = if (leftIcon.measuredWidth == 0) resources.getDimension(R.dimen.actionbar_height).toInt() else leftIcon.measuredWidth
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
                    lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                    titleNormalContainer.layoutParams = lp
                } else {
                    val lp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    lp.leftMargin = 0
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    if (titleBarInfo.leftText != null)
                        leftText.visibility = View.VISIBLE
                    titleNormalContainer.layoutParams = lp
                }
                if (titleBarInfo.titleTextColor != null) {
                    titleTv.setTextColor(Color.parseColor(titleBarInfo.titleTextColor))
                } else {
                    titleTv.setTextColor(Color.WHITE)
                }
                if (titleBarInfo.titleTextSize != null && titleBarInfo.titleTextSize > 0) {
                    titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleBarInfo.titleTextSize.toFloat())
                } else {
                    titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                }
            }
            else -> {
            }
        }

        if (titleBarInfo.rightButtons != null)
            setRightButtons(titleBarInfo.rightButtons)
    }

    protected fun changeSpinner(toOpen: Boolean) {
        val animator: ValueAnimator
        if (toOpen && titleSpinner.rotation == 0f) {
            animator = ValueAnimator.ofInt(0, 180)
            pages.lastElement().titleBarInfo.titleSpinnerOpen = true
        } else if (!toOpen && titleSpinner.rotation == 180f) {
            animator = ValueAnimator.ofInt(180, 0)
            pages.lastElement().titleBarInfo.titleSpinnerOpen = false
        } else {
            return
        }
        animator.duration = 200
        animator.addUpdateListener { animation ->
            titleSpinner.pivotX = (titleSpinner.width / 2).toFloat()
            titleSpinner.pivotY = (titleSpinner.height / 2).toFloat()//支点在图片中心
            titleSpinner.rotation = (animation.animatedValue as Int).toFloat()
        }
        animator.start()
    }

    private fun isLightEnough(colorString: String): Boolean {
        if (colorString[0] == '#') {
            val r: Long
            val g: Long
            val b: Long
            when {
                colorString.length == 7 -> {
                    r = java.lang.Long.parseLong(colorString.substring(1, 3), 16)
                    g = java.lang.Long.parseLong(colorString.substring(3, 5), 16)
                    b = java.lang.Long.parseLong(colorString.substring(5, 7), 16)
                }
                colorString.length == 9 -> {
                    r = java.lang.Long.parseLong(colorString.substring(3, 5), 16)
                    g = java.lang.Long.parseLong(colorString.substring(5, 7), 16)
                    b = java.lang.Long.parseLong(colorString.substring(7, 9), 16)
                }
                else -> throw IllegalArgumentException("Unknown color")
            }
            return r * 0.299 + g * 0.587 + b * 0.114 >= 192
        }
        throw IllegalArgumentException("Unknown color")
    }

    private fun setRightButtons(rightButtonInfos: List<TitleBarInfo.RightButtonInfo>?) {
        val operatorLayout = findViewById<LinearLayout>(R.id.title_bar_operator)
        operatorLayout.removeAllViews()
        if (rightButtonInfos == null)
            return
        val operatorSize = operatorLayout.measuredHeight
        for (i in rightButtonInfos.indices.reversed()) {//倒序排列
            val operatorInfo = rightButtonInfos[i]

            val operator = TextImageView(this@BaseActionBarWebActivity)
            val lp = LinearLayout.LayoutParams(operatorSize, operatorSize)
            operator.layoutParams = lp
            operator.setGravity(Gravity.CENTER)
            operator.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
            operator.background = resources.getDrawable(R.drawable.title_click_bg)
            operatorLayout.addView(operator)
            if (operatorInfo.img != null) {
                Glide.with(this@BaseActionBarWebActivity).load(operatorInfo.img).into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        operator.setImageDrawable(resource)
                    }
                })
            }
            if (operatorInfo.text != null) {
                operator.setText(operatorInfo.text)
            }
            if (operatorInfo.textColor != null) {
                operator.setTextColor(Color.parseColor(operatorInfo.textColor))
            } else {
                operator.setTextColor(resources.getColor(android.R.color.white))
            }
            if (operatorInfo.textSize != null && operatorInfo.textSize > 0) {
                operator.setTextSize(TypedValue.COMPLEX_UNIT_SP, operatorInfo.textSize)
            } else {
                operator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14)
            }

            operator.setOnClickListener { v ->
                if (operatorInfo.subMenu != null) {
                    showPopWindow(v, i, operatorInfo.subMenu)
                } else {
                    execJs(operatorInfo.callback)
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showPopWindow(v: View, parentIndex: Int, operator: List<TitleBarInfo.SubRightButtonInfo>) {
        val recyclerView: RecyclerView
        if (menu == null) {
            menu = PopupWindow(LayoutInflater.from(this).inflate(R.layout.layout_operator_pop, null), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            if (Build.VERSION.SDK_INT >= 23) {
                menu?.enterTransition = Slide(Gravity.TOP)
                menu?.exitTransition = Slide(Gravity.TOP)
            }
            recyclerView = menu?.contentView as RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = MenuAdapter(menu!!, operator)
        } else {
            recyclerView = menu?.contentView as RecyclerView
            (recyclerView.adapter as MenuAdapter).setData(operator)
        }
        if (pages.lastElement().titleBarInfo.titleBarColor != null)
            recyclerView.setBackgroundColor(Color.parseColor(pages.lastElement().titleBarInfo.titleBarColor))
        else
            recyclerView.setBackgroundColor(resources.getColor(R.color.actionbar_bg))
        recyclerView.minimumWidth = v.width * (1 + parentIndex)
        menu?.showAsDropDown(v)
    }

    fun setRightButtonByIndex(action: ActionSetRightButtonByIndex) {
        val titleBarInfo = pages.lastElement().titleBarInfo
        if (titleBarInfo.rightButtons == null || action.index == null || action.index < 0 || action.index >= titleBarInfo.rightButtons.size)
            return

        if (action.subIndex == null) {
            val cur = titleBarInfo.rightButtons[action.index]
            if (action.button.callback != null)
                cur.callback = action.button.callback
            if (action.button.text != null)
                cur.text = action.button.text
            if (action.button.textColor != null)
                cur.textColor = action.button.textColor
            if (action.button.textSize != null)
                cur.textSize = action.button.textSize
            if (action.button.img != null)
                cur.img = action.button.img
            if (action.button.subMenu != null)
                cur.subMenu = action.button.subMenu
        } else {
            val sub = titleBarInfo.rightButtons[action.index].subMenu ?: return

            val cur = sub[action.index]

            if (action.subIndex < 0 || action.subIndex >= titleBarInfo.rightButtons[action.index].subMenu.size)
                return

            if (action.button.callback != null)
                cur.callback = action.button.callback
            if (action.button.text != null)
                cur.text = action.button.text
            if (action.button.textColor != null)
                cur.textColor = action.button.textColor
            if (action.button.textSize != null)
                cur.textSize = action.button.textSize
            if (action.button.img != null)
                cur.img = action.button.img
        }
        setRightButtons(pages.lastElement().titleBarInfo.rightButtons)
    }

    fun removeRightButtonByIndex(action: RightButtonIndex) {
        val titleBarInfo = pages.lastElement().titleBarInfo

        if (titleBarInfo.rightButtons == null ||
                action.index == null || action.index < 0 || action.index >= titleBarInfo.rightButtons.size)
            return

        if (action.subIndex == null) {
            titleBarInfo.rightButtons.removeAt(action.index as Int)
        } else {
            val sub = titleBarInfo.rightButtons[action.index].subMenu
            if (sub == null || action.subIndex < 0 || action.subIndex >= sub.size)
                return

            titleBarInfo.rightButtons[action.index].subMenu.removeAt(action.subIndex as Int)
        }
        setRightButtons(titleBarInfo.rightButtons)
    }

    fun addRightButton(info: TitleBarInfo.RightButtonInfo?) {
        if (info == null)
            return
        val titleBarInfo = pages.lastElement().titleBarInfo
        if (titleBarInfo.rightButtons == null)
            titleBarInfo.rightButtons = ArrayList<TitleBarInfo.RightButtonInfo>()
        titleBarInfo.rightButtons.add(info)
        setRightButtons(titleBarInfo.rightButtons)
    }

    /******************************* methods about title end  */


    override fun onDestroy() {
        super.onDestroy()
        webView.onDestroy()
        webActivityCount--
    }

    fun setShowProgress(showProgress: Boolean) {
        this.showProgress = showProgress
        progress.visibility = if (showProgress) View.VISIBLE else View.GONE
    }

    internal inner class MenuAdapter(private val menu: PopupWindow, private var subButtons: List<TitleBarInfo.SubRightButtonInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        @SuppressLint("InflateParams")
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return Holder(LayoutInflater.from(parent.context).inflate(R.layout.layout_menu_item, null))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val sub = subButtons[position]
            if (sub.img != null) {
                (holder as Holder).img.visibility = View.VISIBLE
                Glide.with(holder.img).load(sub.img).into(holder.img)
            }
            if (sub.text != null) {
                (holder as Holder).text.visibility = View.VISIBLE
                holder.text.text = sub.text
                if (sub.textColor != null) {
                    holder.text.setTextColor(Color.parseColor(sub.textColor))
                } else {
                    holder.text.setTextColor(Color.WHITE)
                }
                if (sub.textSize != null) {
                    holder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, sub.textSize.toFloat())
                } else {
                    holder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                }
            } else {
                (holder as Holder).text.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                execJs(subButtons[holder.getAdapterPosition()].callback)
                menu.dismiss()
            }
        }

        override fun getItemCount(): Int {
            return subButtons.size
        }

        fun setData(operator: List<TitleBarInfo.SubRightButtonInfo>) {
            this.subButtons = operator
            notifyDataSetChanged()
        }

        internal inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var img: ImageView
            var text: TextView

            init {
                val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                itemView.layoutParams = lp
                img = itemView.findViewById(R.id.img)
                text = itemView.findViewById(R.id.text)
            }
        }
    }

    @JvmOverloads
    fun execJs(method: String?, param: String? = null) {
        if (method == null || method == "")
            return
        var callback: String = method
        if (param != null && !param.isEmpty()) {
            var fixedCallback: String = method
            val index = method.indexOf("();")
            if (index >= method.length - 3)
                fixedCallback = fixedCallback.replace("();", "")
            callback = "$fixedCallback('$param');"
        }
        val jsFunc = "javascript:$callback"
        webView.evaluateJs(jsFunc, null)
    }

    companion object {
        //标题类型
        private const val TYPE_NORMAL = "normal"
        private const val TYPE_SEARCH = "search"
        private const val TYPE_WITH_SPINNER = "spinner"

        var webActivityCount = 0
        private val progressTimer = Timer()//使用一个静态的timer来处理进度条，因为并未持有引用，不会导致内存泄漏
    }
}
