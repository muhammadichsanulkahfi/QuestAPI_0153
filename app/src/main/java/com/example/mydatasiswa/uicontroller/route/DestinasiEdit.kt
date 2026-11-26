package com.example.mydatasiswa.uicontroller.route

import com.example.mydatasiswa.R

object DestinasiEdit : DestinasiNavigasi {
    override val route = "edit_siswa"
    override val titleRes = R.string.edit_siswa
    const val itemIdArg = "idSiswa"
    val routeWithArgs = "$route/{$itemIdArg}"
}