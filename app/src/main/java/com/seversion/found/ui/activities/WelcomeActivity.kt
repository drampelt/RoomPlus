package com.seversion.found.ui.activities

import android.Manifest
import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.view.KeyEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.nightonke.wowoviewpager.*
import com.nightonke.wowoviewpager.Color.ColorChangeType
import com.nightonke.wowoviewpager.Eases.EaseType
import com.seversion.found.FoundApplication
import com.seversion.found.R
import kotlinx.android.synthetic.main.activity_welcome_wowo.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * Created by daniel on 2016-05-09.
 */

class WelcomeActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var screenW = 0f
    private var screenH = 0f

    private var animating = false

    private var inputMethodManager: InputMethodManager? = null

    init {
        FoundApplication.graph.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val welcomeSeen = sharedPreferences.getBoolean(resources.getString(R.string.settings_key_welcome_seen), false)
        if (welcomeSeen) {
            startActivity<MainActivity>()
            finish()
            return
        }

        setContentView(R.layout.activity_welcome_wowo)

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        screenW = WoWoUtil.getScreenWidth(this).toFloat()
        screenH = WoWoUtil.getScreenHeight(this).toFloat()

        setupWoWo()
        setupOtherUI()
        setupBackgroundAnimation()
        setupLogoAnimation()
        setupWelcomeScreenAnimation()
        setupServerPageAnimation()
        setupUsernamePageAnimation()
        setupGroupPageAnimation()
        setupPermissionPageAnimation()
    }

    private fun setupWoWo() {
        val adapter = WoWoViewPagerAdapter(supportFragmentManager)
        adapter.fragmentsNumber = 5
        wowo.adapter = adapter
        wowo.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                if (animating) return

                inputMethodManager?.hideSoftInputFromWindow(wowo.windowToken, 0)

                when (position) {
                    1 -> serverAddress.requestFocus()
                    2 -> username.requestFocus()
                    3 -> group.requestFocus()
                }

                when (position) {
                    0, 4 -> fab.hide()
                    else -> {
                        fab.show()
                        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
                    }
                }
            }
        })
        fab.hide()
    }

    private fun setupOtherUI() {
        val editorActionListener = TextView.OnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT -> {
                    animatePageSwitch()
                    return@OnEditorActionListener true
                }
            }
            return@OnEditorActionListener false
        }
        serverAddress.setOnEditorActionListener(editorActionListener)
        username.setOnEditorActionListener(editorActionListener)
        group.setOnEditorActionListener(editorActionListener)

        serverAddress.setText(sharedPreferences.getString(resources.getString(R.string.settings_key_server_address), ""), TextView.BufferType.EDITABLE)
        username.setText(sharedPreferences.getString(resources.getString(R.string.settings_key_username), ""), TextView.BufferType.EDITABLE)
        group.setText(sharedPreferences.getString(resources.getString(R.string.settings_key_group), ""), TextView.BufferType.EDITABLE)

        letsGo.onClick { animatePageSwitch() }
        fab.onClick { animatePageSwitch() }
        completionButton.onClick { validateInput() }

        // Permission check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            completionHeader.text = resources.getString(R.string.welcome_label_all_set)
            completionBody.text = resources.getString(R.string.welcome_label_ready_description)
        } else {
            completionHeader.text = resources.getString(R.string.welcome_label_almost_there)
            completionBody.text = resources.getString(R.string.welcome_label_permission_description)
        }
    }

    private fun validateInput() {
        var serverAddressStr = serverAddress.text.toString().trim()
        val usernameStr = username.text.toString().trim()
        val groupStr = group.text.toString().trim()

        if (serverAddressStr.length > 0) {
            if (!(serverAddressStr.startsWith("http://") || serverAddressStr.startsWith("https://"))) {
                serverAddressStr = "http://$serverAddressStr"
            }

            if (!Patterns.WEB_URL.matcher(serverAddressStr).matches()) {
                Snackbar.make(root, R.string.welcome_error_invalid_server_address, Snackbar.LENGTH_LONG).show()
                return
            }
        }

        if (usernameStr.length == 0) {
            Snackbar.make(root, R.string.welcome_error_invalid_username, Snackbar.LENGTH_LONG).show()
            return
        }

        if (groupStr.length == 0) {
            Snackbar.make(root, R.string.welcome_error_invalid_group, Snackbar.LENGTH_LONG).show()
            return
        }

        sharedPreferences.edit()
                .putString(resources.getString(R.string.settings_key_server_address), serverAddressStr)
                .putString(resources.getString(R.string.settings_key_username), usernameStr)
                .putString(resources.getString(R.string.settings_key_group), groupStr)
                .putBoolean(resources.getString(R.string.settings_key_welcome_seen), true)
                .commit()

        startActivity<MainActivity>()
        finish()
    }

    private fun setupBackgroundAnimation() {
        val backgroundAnimation = ViewAnimation(wowo)
        backgroundAnimation.addPageAnimaition(WoWoBackgroundColorAnimation(
                0, 0f, 1f,
                resources.getColor(R.color.apple, theme),
                resources.getColor(R.color.fruit_salad, theme),
                ColorChangeType.RGB,
                EaseType.EaseInOutSine,
                true
        ))
        backgroundAnimation.addPageAnimaition(WoWoBackgroundColorAnimation(
                1, 0f, 1f,
                resources.getColor(R.color.fruit_salad, theme),
                resources.getColor(R.color.shamrock, theme),
                ColorChangeType.RGB,
                EaseType.EaseInOutSine,
                true
        ))
        backgroundAnimation.addPageAnimaition(WoWoBackgroundColorAnimation(
                2, 0f, 1f,
                resources.getColor(R.color.shamrock, theme),
                resources.getColor(R.color.turquoise_blue, theme),
                ColorChangeType.RGB,
                EaseType.EaseInOutSine,
                true
        ))
        backgroundAnimation.addPageAnimaition(WoWoBackgroundColorAnimation(
                3, 0f, 1f,
                resources.getColor(R.color.turquoise_blue, theme),
                resources.getColor(R.color.aquamarine, theme),
                ColorChangeType.RGB,
                EaseType.EaseInOutSine,
                true
        ))
        wowo.addAnimation(backgroundAnimation)
    }

    private fun setupLogoAnimation() {
        val logoAnimation = ViewAnimation(logo)
        val globeOffsetX = WoWoUtil.dp2px(16, this).toFloat()
        val globeOffsetY = -screenH / 2 + WoWoUtil.dp2px(40 + 64 + 64 + 32 + 10, this) // 1/2 icon size + centered offset + 1/2 globe size + globe margin + extra
        logoAnimation.addPageAnimaition(WoWoTranslationAnimation(
                0, 0f, 1f,
                logo.translationX,
                logo.translationY,
                globeOffsetX,
                globeOffsetY,
                EaseType.EaseInOutSine,
                true
        ))
        logoAnimation.addPageAnimaition(WoWoScaleAnimation(
                0, 0f, 1f,
                0.2f,
                0.2f,
                EaseType.EaseInOutSine,
                true
        ))
        logoAnimation.addPageAnimaition(WoWoTranslationAnimation(
                1, 0f, 1f,
                globeOffsetX,
                globeOffsetY,
                globeOffsetX,
                -screenH,
                EaseType.EaseInOutSine,
                true
        ))
        wowo.addAnimation(logoAnimation)
    }

    private fun setupWelcomeScreenAnimation() {
        val textAnimation = ViewAnimation(welcomeText)
        textAnimation.addPageAnimaition(WoWoTranslationAnimation(
                0, 0f, 1f,
                logo.translationX,
                logo.translationY,
                -screenW,
                logo.translationY,
                EaseType.Linear,
                true
        ))
        wowo.addAnimation(textAnimation)

        val buttonAnimation = ViewAnimation(letsGo)
        buttonAnimation.addPageAnimaition(WoWoTranslationAnimation(
                0, 0f, 1f,
                letsGo.translationX,
                letsGo.translationY,
                letsGo.translationX,
                screenH / 2,
                EaseType.EaseOutQuad,
                false
        ))
        wowo.addAnimation(buttonAnimation)
    }

    private fun setupServerPageAnimation() {
        val animation = ViewAnimation(serverPage)
        animation.addPageAnimaition(WoWoTranslationAnimation(
                0, 0f, 1f,
                screenW,
                serverPage.translationY,
                -screenW,
                serverPage.translationY,
                EaseType.Linear,
                true
        ))
        animation.addPageAnimaition(WoWoTranslationAnimation(
                1, 0f, 1f,
                0f,
                serverPage.translationY,
                -screenW,
                serverPage.translationY,
                EaseType.Linear,
                true
        ))
        wowo.addAnimation(animation)
    }

    private fun setupUsernamePageAnimation() {
        val animation = ViewAnimation(usernamePage)
        animation.addPageAnimaition(WoWoTranslationAnimation(
                0, 0f, 1f,
                screenW,
                usernamePage.translationY,
                0f,
                usernamePage.translationY,
                EaseType.Linear,
                true
        ))
        animation.addPageAnimaition(WoWoTranslationAnimation(
                1, 0f, 1f,
                screenW,
                usernamePage.translationY,
                -screenW,
                usernamePage.translationY,
                EaseType.Linear,
                true
        ))
        animation.addPageAnimaition(WoWoTranslationAnimation(
                2, 0f, 1f,
                0f,
                usernamePage.translationY,
                -screenW,
                usernamePage.translationY,
                EaseType.Linear,
                true
        ))
        wowo.addAnimation(animation)
    }

    private fun setupGroupPageAnimation() {
        val animation = ViewAnimation(groupPage)
        animation.addPageAnimaition(WoWoTranslationAnimation(
                0, 0f, 1f,
                screenW,
                groupPage.translationY,
                0f,
                groupPage.translationY,
                EaseType.Linear,
                true
        ))
        animation.addPageAnimaition(WoWoTranslationAnimation(
                2, 0f, 1f,
                screenW,
                groupPage.translationY,
                -screenW,
                groupPage.translationY,
                EaseType.Linear,
                true
        ))
        animation.addPageAnimaition(WoWoTranslationAnimation(
                3, 0f, 1f,
                0f,
                groupPage.translationY,
                -screenW,
                groupPage.translationY,
                EaseType.Linear,
                true
        ))
        wowo.addAnimation(animation)
    }

    private fun setupPermissionPageAnimation() {
        val animation = ViewAnimation(permissionPage)
        animation.addPageAnimaition(WoWoTranslationAnimation(
                0, 0f, 1f,
                screenW,
                permissionPage.translationY,
                0f,
                permissionPage.translationY,
                EaseType.Linear,
                true
        ))
        animation.addPageAnimaition(WoWoTranslationAnimation(
                3, 0f, 1f,
                screenW,
                permissionPage.translationY,
                -screenW,
                permissionPage.translationY,
                EaseType.Linear,
                true
        ))
        animation.addPageAnimaition(WoWoTranslationAnimation(
                4, 0f, 1f,
                0f,
                permissionPage.translationY,
                -screenW,
                permissionPage.translationY,
                EaseType.Linear,
                true
        ))
        wowo.addAnimation(animation)
    }

    private fun animatePageSwitch(pages: Int = 1) {
        val animator = ValueAnimator.ofInt(0, wowo.width * Math.abs(pages))
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                animating = true
            }

            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationCancel(animation: Animator?) {
                animating = false
                wowo.endFakeDrag()
            }

            override fun onAnimationEnd(animation: Animator?) {
                animating = false
                wowo.endFakeDrag()
            }
        })

        animator.interpolator = AccelerateDecelerateInterpolator()
        var oldDragPosition = 0
        animator.addUpdateListener { animation ->
            val dragPosition = animation.animatedValue as Int
            val offset = dragPosition - oldDragPosition
            oldDragPosition = dragPosition
            wowo.fakeDragBy(offset.toFloat() * if (pages > 0) -1 else 1)
        }
        animator.duration = 500
        wowo.beginFakeDrag()
        animator.start()
    }
}
