package ru.myproevent.ui.presenters.settings.account

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface AccountView : BaseMvpView {
    fun showProfile(profile: Profile)
    fun makeProfileEditable()
}
