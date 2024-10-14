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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import org.w3c.dom.Text

class MainActivity : ComponentActivity() {

    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(db)
        }
    }
}

@SuppressLint("UnrememberedMutableState", "SuspiciousIndentation")
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
            .background(Color.White)
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
                color = Color.Black
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
                        Log.d("Firestorm", "DocumentSnapshot added with ID: ${documentReference.id}")
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
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        //LABELS
        Row(
            Modifier
                .fillMaxWidth(),
            Arrangement.Start
        ) {
            Column (
                Modifier
                    .fillMaxWidth(0.3f)
            ){
                Text( fontWeight = FontWeight.Bold, fontSize = 16.sp, text = "Nome")
            }
            Column(
                Modifier
                    .fillMaxWidth(0.4f)
            ){
                Text( fontWeight = FontWeight.Bold, fontSize = 16.sp, text = "telefone")
            }
            Column(
                Modifier
                    .fillMaxWidth(0.5f)
            ){
                Text( fontWeight = FontWeight.Bold, fontSize = 16.sp, text = "Funções")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth()
        ) {
//            SnapshotStateList<T>

            var users = remember { mutableStateListOf<Pair<String, Map<String, Any>>>() } // Document ID + Dados
//            LaunchedEffect(Unit) {
                db.collection("users")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            users.add(document.id to document.data)
                            Log.d("Firestore", "${document.id} => ${document.data}")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error getting documents", e)
                    }
//            }
            LazyColumn {
                items(users){pessoa ->
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        Arrangement.Start
                    ) {
                        Column (
                            Modifier
                                .fillMaxWidth(0.3f)
                        ){
                            val nome = pessoa.second["nome"] as? String ?: "Desconhecido"
                            Text(text = nome, fontSize = 14.sp)
                        }
                        Column(
                            Modifier
                                .fillMaxWidth(0.4f)
                        ){
                            val telefone = pessoa.second["telefone"] as? String ?: "Sem Telefone"
                            Text(text = telefone, fontSize = 14.sp)
                        }
                        Column(
                            Modifier
                                .fillMaxWidth(0.5f)
                        ){
                            val id = pessoa.first as? String ?: "Sem Telefone"
                            Button(onClick = {
                                db.collection("users").document(id)
                                    .delete()
                                    .addOnSuccessListener { Log.d("Firebase", "DocumentSnapshot successfully deleted! ${id}") }
                                    .addOnFailureListener { e -> Log.w("Firebase", "Error deleting document", e) }
                            }) {
                                Text(
                                    text = "🗑️",
                                    fontFamily = FontFamily.Serif,
                                    color = Color.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

    }
}