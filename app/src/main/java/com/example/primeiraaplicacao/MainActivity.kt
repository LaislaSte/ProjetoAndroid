package com.example.primeiraaplicacao

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.Px
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.primeiraaplicacao.roomDB.DatabasePessoa
import com.example.primeiraaplicacao.roomDB.Pessoa
import com.example.primeiraaplicacao.ui.theme.PrimeiraAplicacaoTheme
import com.example.primeiraaplicacao.viewModel.Repository
import com.example.primeiraaplicacao.viewModel.ViewModelPessoa

class MainActivity : ComponentActivity() {

    //funcoes lazy - pode ser criado o banco durante a run do app, no segundo plano (background)
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            DatabasePessoa::class.java,
                "pessoa.db"
        ).build()
    }

    private val viewModel by viewModels<ViewModelPessoa> (
        factoryProducer = {
            //objects podem receber qualquer coisa
            //(:) e um extend do Java, seria como se fosse uma heranca ou recebimento de propriedade
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>) : T {
                    return ViewModelPessoa(Repository(db)) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrimeiraAplicacaoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(viewModel, this)
                }
            }
        }
    }
}

@Composable
fun App(viewModel: ViewModelPessoa, mainActivity: MainActivity) {
    var nome by remember {
        mutableStateOf("")
    }
    var telefone by remember {
        mutableStateOf("")
    }

    val pessoa = Pessoa(
        nome,
        telefone
    )

    var pessoaList by remember {
        mutableStateOf(listOf<Pessoa>())
    }

    viewModel.getPessoa().observe(mainActivity){
        pessoaList = it
    }

    Column(
        Modifier

            .background(Color.Black)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth(),
            Arrangement.Center
        ) {
            Text(
                text = "App Database",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome:") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = telefone,
            onValueChange = { telefone = it },
            label = { Text("Telefone:") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.upsertPessoa(pessoa)
                nome =""
                telefone = ""
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Cadastrar")
        }

        Row(
            Modifier
                .fillMaxWidth(),
            Arrangement.Center
        ) {
            Column (
                Modifier
                    .fillMaxWidth(0.5f)
            ){
                Text(text = "Nome")
            }
            Column(
                Modifier
                    .fillMaxWidth(0.5f)
            ){
                Text(text = "telefone")
            }
        }
        Divider()
        LazyColumn {
            items(pessoaList){ pessoa ->
                Row(
                    Modifier
                        .clickable {
                            viewModel.deletePessoa(pessoa)
                        }
                        .fillMaxWidth(),
                    Arrangement.Center

                ) {
                    Column (
                        Modifier
                            .fillMaxWidth(0.5f)
                    ){
                        Text(text = "${pessoa.nome}")

                    }
                    Column(
                        Modifier
                            .fillMaxWidth(0.5f)
                    ){
                        Text(text = "${pessoa.telefone}")
                    }
                }
            }
        }
        Divider()
    }
}