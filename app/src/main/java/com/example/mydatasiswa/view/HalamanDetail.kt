
package com.example.mydatasiswa.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mydatasiswa.R
import com.example.mydatasiswa.modeldata.DataSiswa
import com.example.mydatasiswa.uicontroller.route.DestinasiDetail
import com.example.mydatasiswa.viewmodel.DetailViewModel
import com.example.mydatasiswa.viewmodel.PenyediaViewModel
import com.example.mydatasiswa.viewmodel.StatusUIDetail
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSiswaScreen(
    navigateToEditItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(
        factory = PenyediaViewModel.Factory
    )
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            SiswaTopAppBar(
                title = stringResource(DestinasiDetail.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            val state = viewModel.statusUIDetail

            if (state is StatusUIDetail.Success && state.satuSiswa != null) {
                FloatingActionButton(
                    onClick = {
                        navigateToEditItem(state.satuSiswa.id)
                    },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(
                        dimensionResource(id = R.dimen.padding_large)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_siswa)
                    )
                }
            }
        }
    ) { innerPadding ->
        BodyDetailDataSiswa(
            statusUIDetail = viewModel.statusUIDetail,
            retryAction = viewModel::getSatuSiswa,
            onDelete = {
                coroutineScope.launch {
                    viewModel.hapusSatuSiswa()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        )
    }
}

@Composable
fun BodyDetailDataSiswa(
    statusUIDetail: StatusUIDetail,
    retryAction: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var deleteConfirmationRequired by remember {
        mutableStateOf(false)
    }

    when (statusUIDetail) {
        is StatusUIDetail.Loading -> LoadingScreen(modifier = modifier)

        is StatusUIDetail.Error -> ErrorScreen(
            retryAction,
            modifier = modifier
        )

        is StatusUIDetail.Success -> {
            Column(
                modifier = modifier.padding(
                    dimensionResource(id = R.dimen.padding_medium)
                ),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.padding_medium)
                )
            ) {
                statusUIDetail.satuSiswa?.let {
                    ItemDetailSiswa(siswa = it)
                }

                OutlinedButton(
                    onClick = {
                        deleteConfirmationRequired = true
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.delete))
                }

                if (deleteConfirmationRequired) {
                    DeleteConfirmationDialog(
                        onDeleteConfirm = {
                            deleteConfirmationRequired = false
                            onDelete()
                        },
                        onDeleteCancel = {
                            deleteConfirmationRequired = false
                        },
                        modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.padding_medium)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ItemDetailSiswa(
    siswa: DataSiswa,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.padding_medium)
            )
        ) {
            BarisDetailSiswa(
                labelResID = R.string.nama,
                itemDetail = siswa.nama
            )
            BarisDetailSiswa(
                labelResID = R.string.alamat,
                itemDetail = siswa.alamat
            )
            BarisDetailSiswa(
                labelResID = R.string.telpon,
                itemDetail = siswa.telpon
            )
        }
    }
}

@Composable
private fun BarisDetailSiswa(
    labelResID: Int,
    itemDetail: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = itemDetail,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDeleteCancel,
        title = {
            Text(stringResource(R.string.attention))
        },
        text = {
            Text(stringResource(R.string.tanya))
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.yes))
            }
        }
    )
}
