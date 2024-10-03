package com.example.primeiraaplicacao

import android.annotation.SuppressLint
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.mutableStateListOf
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
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {

    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(db)
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun App(db: FirebaseFirestore) {
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

    Column(
        Modifier

            .background(Color.Black)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        //titulo
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

        //nome com set value
        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome:") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        //telefone com set value
        TextField(
            value = telefone,
            onValueChange = { telefone = it },
            label = { Text("Telefone:") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val user = hashMapOf(
                    "nome" to nome,
                    "telefone" to telefone
                )

                db.collection("users")
                    .add(user)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error adding document", e)
                    }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Cadastrar")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Titulo da Lista
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.Center
        ) {
            Text(
                text = "Nomes e telefones",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
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

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            Modifier
                .fillMaxWidth(),
            Arrangement.Center
        ) {
            Column (
                Modifier
                    .fillMaxWidth(0.5f)
            ){
                db.collection("users")
                    .get()
                    .addOnSuccessListener { documents ->
                        for(document in documents){
                            val list = hashMapOf(
                                "nome" to "${document.data.get("nome")}"
                            )
                            Log.d("Firestore", "${document.id} => ${document.data}")
                        }

                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error getting document", e)
                    }
            }
            Column(
                Modifier
                    .fillMaxWidth(0.5f)
            ){
                db.collection("users")
                    .get()
                    .addOnSuccessListener { documents ->
                        for(document in documents){
                            val list = hashMapOf(
                                "telefone" to "${document.data.get("telefone")}"
                            )
                            Log.d("Firestore", "${document.id} => ${document.data}")
                        }

                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error getting document", e)
                    }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Titulo da Lista de outra forma
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.Center
        ) {
            Text(
                text = "Nomes e telefones de outra forma",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth()
        ) {

            val pessoaList = mutableStateListOf<HashMap<String, String>>()
            db.collection("users")
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents){
                        val user = hashMapOf(
                            "nome" to "${document.data.get("nome")}",
                            "telefone" to "${document.data.get("telefone")}"
                        )
                        pessoaList.add(user)
                        Log.d("Firestore", "${document.id} => ${document.data}")
                    }

                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error getting document", e)
                }
            LazyColumn {
                items(pessoaList){ pessoa ->
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        Arrangement.Center
                    ) {
                        Column (
                            Modifier
                                .fillMaxWidth(0.5f)
                        ){
                            Text(text = pessoa["nome"] ?: "--")
                        }
                        Column(
                            Modifier
                                .fillMaxWidth(0.5f)
                        ){
                            Text(text = pessoa["telefone"] ?: "--")
                        }
                    }
                }
            }
        }


        Divider()
    }
}