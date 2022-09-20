package com.ln.toptop.util

import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.forEach
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView


fun TextView.fromHtml(@StringRes stringRes: Int) {
    val html = context.getString(stringRes)
    text =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(
            html,
            Html.FROM_HTML_MODE_COMPACT
        ) else Html.fromHtml(html)
}

fun SpannableString.withClickableSpan(
    clickablePart: String,
    onClickListener: () -> Unit
): SpannableString {
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) = onClickListener.invoke()
    }
    val clickablePartStart = indexOf(clickablePart)
    setSpan(
        clickableSpan,
        clickablePartStart,
        clickablePartStart + clickablePart.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return this
}

fun BottomNavigationView.disableTooltip() {
    val content: View = getChildAt(0)
    if (content is ViewGroup) {
        content.forEach {
            it.setOnLongClickListener {
                return@setOnLongClickListener true
            }
            it.isHapticFeedbackEnabled = false
        }
    }
}

fun ImageView.loadUrl(avatar: String?, circle: Boolean = false) {
    if (avatar == null)
        return
    val builder = Glide.with(context)
        .load(avatar)
    if (circle)
        builder.circleCrop()
    builder.into(this)
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}