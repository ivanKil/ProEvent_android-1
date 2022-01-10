package ru.myproevent.ui.fragments.authorization

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.FragmentAuthorizationBinding
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.authorization.authorization.AuthorizationPresenter
import ru.myproevent.ui.presenters.authorization.authorization.AuthorizationView
import ru.myproevent.ui.presenters.main.BottomNavigation
import ru.myproevent.ui.presenters.main.RouterProvider
import ru.myproevent.ui.presenters.main.Tab
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.view.isVisible
import ru.myproevent.domain.utils.pxValue

class AuthorizationFragment :
    BaseMvpFragment<FragmentAuthorizationBinding>(FragmentAuthorizationBinding::inflate),
    AuthorizationView {

    private var emailInvalidError = false
    private var passwordInvalidError = false

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun setLayoutParams() = with(binding) {
        // https://stackoverflow.com/a/24035591/11883985
        body.post {
            val availableHeight = root.height

            Log.d("[MYLOG]", "passwordRecovery.lineCount: ${passwordRecovery.lineCount}; registration.lineCount: ${registration.lineCount}")
            if(passwordRecovery.lineCount > 1|| registration.lineCount > 1){
                bottomOptionsContainer.orientation = LinearLayout.VERTICAL
                bottomOptionsHorizontalSeparatorSpace.visibility = GONE
            }

            space.layoutParams = space.layoutParams.apply { height = availableHeight }
            body.post {
                val diffrence = body.height - availableHeight
                authorizationConfirmMarginTop.layoutParams =
                    authorizationConfirmMarginTop.layoutParams.apply {
                        height -= if (diffrence <= pxValue(40f)) {
                            diffrence
                        } else {
                            pxValue(40f)
                        }.toInt()
                    }
                Log.d("[MYLOG]", "diffrence: $diffrence; pxValue(80f + 48f): ${pxValue(80f + 48f)}; pxValue(80f + 48f + 24f + 28f): ${pxValue(80f + 48f + 24f + 28f)}")
                if(diffrence > pxValue(40f)){
                    logo.isVisible = false
                    Log.d("[MYLOG]", "logo.isVisible: ${logo.isVisible}")
                }
                if(diffrence > pxValue(80f + 48f)){
                    formTitle.isVisible = false
                    Log.d("[MYLOG]", "logo.isVisible: ${formTitle.isVisible}")
                }
            }
        }
    }

    override val presenter by moxyPresenter {
        AuthorizationPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        fun newInstance() = AuthorizationFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutParams()
        with(binding) {
            authorizationConfirm.setOnClickListener {
                presenter.authorize(
                    emailEdit.text.toString(),
                    passwordEdit.text.toString(),
                    rememberMeCheckbox?.isChecked ?: true
                )
            }
            registration.setOnClickListener {
                presenter.openRegistration()
            }
            registrationHitArea.setOnClickListener {
                registration.performClick()
            }
            passwordRecovery.setOnClickListener { presenter.recoverPassword() }
            passwordRecoveryHitArea.setOnClickListener { passwordRecovery.performClick() }

            val colorState = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_active),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(-android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_hovered),
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_enabled)
                ),
                intArrayOf(
                    requireContext().getColor(R.color.ProEvent_blue_300),
                    requireContext().getColor(R.color.ProEvent_blue_600),
                    requireContext().getColor(R.color.ProEvent_blue_600),
                    requireContext().getColor(R.color.ProEvent_blue_300),
                    requireContext().getColor(R.color.ProEvent_blue_300),
                    requireContext().getColor(R.color.ProEvent_blue_300)
                )
            )
            emailEdit.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    emailInvalidError = false
                    emailInput.setBoxStrokeColorStateList(colorState)

                    if (!passwordInvalidError) {
                        errorMessage.visibility = GONE
                    }
                }
            }
            passwordEdit.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    passwordInvalidError = false
                    passwordInput.setBoxStrokeColorStateList(colorState)

                    if (!emailInvalidError) {
                        errorMessage.visibility = GONE
                    }

                    passwordInput.setEndIconTintList(
                        ColorStateList(
                            arrayOf(intArrayOf()),
                            intArrayOf(requireContext().getColor(R.color.ProEvent_blue_300))
                        )
                    )
                } else {
                    passwordInput.setEndIconTintList(
                        ColorStateList(
                            arrayOf(intArrayOf()),
                            intArrayOf(requireContext().getColor(R.color.ProEvent_blue_800))
                        )
                    )
                }
            }
        }
    }

    override fun authorizationDataInvalid() {
        with(binding) {
            val colorState = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_active),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(-android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_hovered),
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_enabled)
                ),
                intArrayOf(
                    requireContext().getColor(R.color.ProEvent_bright_orange_300),
                    requireContext().getColor(R.color.ProEvent_blue_600),
                    requireContext().getColor(R.color.ProEvent_blue_600),
                    requireContext().getColor(R.color.ProEvent_bright_orange_300),
                    requireContext().getColor(R.color.ProEvent_bright_orange_300),
                    requireContext().getColor(R.color.ProEvent_bright_orange_300)
                )
            )
            emailInvalidError = true
            passwordInvalidError = true
            emailInput.setBoxStrokeColorStateList(colorState)
            passwordInput.setBoxStrokeColorStateList(colorState)
            errorMessage.text = getString(R.string.authorization_data_invalid)
            errorMessage.visibility = VISIBLE
            false
        }
    }

    override fun finishAuthorization() {
        (requireActivity() as BottomNavigation).openTab(Tab.HOME)
        Log.d(
            "[MYLOG]",
            "AuthorizationFragment (requireActivity() as BottomNavigationView).openTab(Tab.HOME)"
        )
    }
}