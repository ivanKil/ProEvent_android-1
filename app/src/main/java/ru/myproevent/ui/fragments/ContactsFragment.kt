package ru.myproevent.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentContactsBinding
import ru.myproevent.ui.adapters.contacts.ContactsRVAdapter
import ru.myproevent.ui.presenters.contacts.ContactsPresenter
import ru.myproevent.ui.presenters.contacts.ContactsView
import ru.myproevent.ui.presenters.main.MainView
import ru.myproevent.ui.presenters.main.Menu

class ContactsFragment : BaseMvpFragment(), ContactsView {

    companion object {
        fun newInstance() = ContactsFragment()
    }

    private var _vb: FragmentContactsBinding? = null
    private val vb get() = _vb!!

    override val presenter by moxyPresenter {
        ContactsPresenter().apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    var adapter: ContactsRVAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainView).selectItem(Menu.CONTACTS)
        _vb = FragmentContactsBinding.inflate(inflater, container, false)
        return vb.apply {
            addContact.setOnClickListener { presenter.addContact() }
            addFirstContact.setOnClickListener { presenter.addContact() }
        }.root
    }

    override fun onResume() {
        super.onResume()
        presenter.init()
    }

    override fun init() = with(vb) {
        rvContacts.layoutManager = LinearLayoutManager(context)
        adapter = ContactsRVAdapter(presenter.contactsListPresenter)
        rvContacts.adapter = adapter
    }

    override fun updateList() {
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
            with(vb) {
                if (adapter!!.itemCount == 0) {
                    progressBar.visibility = GONE
                    scroll.visibility = VISIBLE
                    noContacts.visibility = VISIBLE
                    noContactsText.visibility = VISIBLE
                    addFirstContact.visibility = VISIBLE
                } else if (adapter!!.itemCount > 0) {
                    progressBar.visibility = GONE
                    noContacts.visibility = GONE
                    noContactsText.visibility = GONE
                    addFirstContact.visibility = GONE
                    scroll.visibility = VISIBLE
                    rvContacts.visibility = VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _vb = null
    }

}