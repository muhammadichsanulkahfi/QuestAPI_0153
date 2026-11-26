package com.example.mydatasiswa.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydatasiswa.repositori.RepositoryDataSiswa
import com.example.mydatasiswa.uicontroller.route.DestinasiEdit
import kotlinx.coroutines.launch

class EditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoryDataSiswa: RepositoryDataSiswa
) : ViewModel() {
    private val idSiswa: Int = checkNotNull(savedStateHandle[DestinasiEdit.itemIdArg])

    var uiStateSiswa by mutableStateOf(UIStateSiswa())
        private set

    init {
        viewModelScope.launch {
            try {
                uiStateSiswa = repositoryDataSiswa.getSatuSiswa(idSiswa)
                    .toUiStateSiswa(isEntryValid = true)
            } catch (e: Exception) {
                println("Gagal memuat data : ${e.message}")
            }
        }
    }

    private fun validasiInput(uiState: DetailSiswa = uiStateSiswa.detailSiswa): Boolean {
        return with(uiState) {
            nama.isNotBlank() && alamat.isNotBlank() && telpon.isNotBlank()
        }
    }

    fun updateUiState(detailSiswa: DetailSiswa) {
        uiStateSiswa = UIStateSiswa(
            detailSiswa = detailSiswa,
            isEntryValid = validasiInput(detailSiswa)
        )
    }

    suspend fun editSatuSiswa() {
        if (validasiInput()) {
            try {
                val response = repositoryDataSiswa.editSatuSiswa(
                    idSiswa, uiStateSiswa.detailSiswa.toDataSiswa()
                )
                if (response.isSuccessful) {
                    println("Sukses Edit Data : ${response.message()}")
                } else {
                    println("Gagal Edit Data : ${response.errorBody()}")
                }
            } catch (e: Exception) {
                println("Gagal Edit Data : ${e.message}")
            }
        }
    }
}