package com.example.mydatasiswa.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydatasiswa.modeldata.DataSiswa
import com.example.mydatasiswa.repositori.RepositoryDataSiswa
import com.example.mydatasiswa.uicontroller.route.DestinasiDetail
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface StatusUIDetail {
    data class Success(val satuSiswa: DataSiswa? = null) : StatusUIDetail
    object Error : StatusUIDetail
    object Loading : StatusUIDetail
}

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoryDataSiswa: RepositoryDataSiswa
) : ViewModel() {
    private val idSiswa: Int = checkNotNull(savedStateHandle[DestinasiDetail.itemIdArg])

    var statusUIDetail: StatusUIDetail by mutableStateOf(StatusUIDetail.Loading)
        private set

    init {
        getSatuSiswa()
    }

    fun getSatuSiswa() {
        viewModelScope.launch {
            statusUIDetail = StatusUIDetail.Loading
            statusUIDetail = try {
                StatusUIDetail.Success(satuSiswa = repositoryDataSiswa.getSatuSiswa(idSiswa))
            } catch (e: IOException) {
                StatusUIDetail.Error
            } catch (e: HttpException) {
                StatusUIDetail.Error
            } catch (e: Exception) {
                StatusUIDetail.Error
            }
        }
    }

    suspend fun hapusSatuSiswa() {
        try {
            val response = repositoryDataSiswa.hapusSatuSiswa(idSiswa)
            if (response.isSuccessful) {
                println("Sukses Hapus Data : ${response.message()}")
            } else {
                println("Gagal Hapus Data : ${response.errorBody()}")
            }
        } catch (e: Exception) {
            println("Gagal Hapus Data : ${e.message}")
        }
    }
}