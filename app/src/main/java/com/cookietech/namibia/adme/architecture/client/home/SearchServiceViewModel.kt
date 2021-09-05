package com.cookietech.namibia.adme.architecture.client.home

import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.ViewModel
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class SearchServiceViewModel:ViewModel() {

    val searchServiceRepository = SearchServiceRepository()


    fun bindSearch(searchView: EditText, searchCallback: SearchServiceRepository.SearchCallback) {
        searchView.textChanges()
            .skipInitialValue()
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { charSequence ->
                Log.d("debounce_debug", "accept: $charSequence")
                if (!TextUtils.isEmpty(charSequence)){
                    searchCallback.onFetchStarted()
                    searchServiceRepository.getSearchResults(charSequence.toString(),searchCallback)
                }

            }
    }
}