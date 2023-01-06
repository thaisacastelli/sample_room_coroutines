package br.com.alura.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityListaProdutosActivityBinding
import br.com.alura.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.launch

class ListaProdutosActivity : AppCompatActivity() {

    private val adapter = ListaProdutosAdapter(context = this)
    private val binding by lazy {
        ActivityListaProdutosActivityBinding.inflate(layoutInflater)
    }
    private val obProdutoDao by lazy {
        AppDatabase.instancia(this).produtoDao()
    }
    /*usando lifecycleScope ja fica vinculado ao ciclo de vida da ativ e entao ja cancela
    * sozinho o scope ao destruir a activity e inicia rodando no scope Main e qdo for chamar
    * algo que precise ser fora muda usando withContext para IO por exemplo. Para chamar
    * funcoes do room nao precisa se estiver usando coroutine nele, usando suspend function nele,
    * pq internamente o room ja muda o contexto automaticamente p fora da main thread qdo acessa
    * o banco de dados*/
    //private val scope = MainScope()
    //private val job = Job()
    //private lateinit var jobImprimeLog: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configuraRecyclerView()
        configuraFab()

        /*usando coroutines no room, atribuindo suspend para suas funcoes no Dao pode chamar
        direto da coroutine que internamente ele ja muda o context para fora do main quando
        for executar acoes no banco de dados*/
        lifecycleScope.launch {
            obProdutoDao.buscaTodos().collect { produtos ->
                adapter.atualiza(produtos)
            }
        }

//exemplo produzir fluxo de dados e consumir dados emitidos pelo fluxo
//        val fluxoDeNumeros = flow {
//            repeat(100) {
//                emit(it)
//                delay(1000)
//            }
//        }
//
//        lifecycleScope.launch {
//            fluxoDeNumeros.collect { numero ->
//                Log.i("ListaProdutos", "onCreate: $numero")
//            }
//        }
    }

    /*withContext retorna o valor da ultima linha que tem nele, entao pode usar a funcao como
    uma expressao e atribuir o valor a ela direto, sem precisar usar return*/
//    private suspend fun buscaTodosProdutos() =
//        withContext(Dispatchers.IO) {
//            obProdutoDao.buscaTodos()
//        }

 /*OBS: nao precisa mais usar onResume para atualizar os dados antes de mostrar a tela, inicializa
    no onCreate e por estar usando Flow no retorno dos dados do Room automaticamente ele emite
    os dados que serao coletados e atualizarao a lista caso tenha alguma mudanca nos dados*/
 //   override fun onResume() {
 //       super.onResume()
//        scope.launch {
//            adapter.atualiza(buscaTodosProdutos())
//        }

//        val handlerCoroutineException = CoroutineExceptionHandler { _, throwable ->
//            runOnUiThread(Runnable {
//                Toast.makeText(
//                    this@ListaProdutosActivity,
//                    "${getString(R.string.lbl_erro)} $throwable",
//                    Toast.LENGTH_LONG
//                ).show()
//            })
//        }
//        val newScope = MainScope()
//        jobImprimeLog = scope.launch(handlerCoroutineException) {
//            repeat(1000) {
//                Log.i("TAG", "onResume: teste cancelar coroutine em execução $it")
//                delay(500)
//            }
//        }
//        newScope.launch(job + handlerCoroutineException) {
//            repeat(1000) {
//                Log.i("TAG", "onResume: teste job $it")
//                delay(1000)
//            }
//        }
//        scope.launch(handlerCoroutineException) {
//            newScope.launch(handlerCoroutineException) {
//                throw IllegalArgumentException("Lançando exception 2 na coroutine em outro scope")
//            }
//            val produtos = obProdutoDao.buscaTodos()
//            withContext(Dispatchers.Main) {
//                adapter.atualiza(produtos)
//                throw Exception("Lançando exception 1 na coroutine")
//            }
//        }
 //   }

    private fun configuraFab() {
        val fab = binding.activityListaProdutosFab
        fab.setOnClickListener {
            //jobImprimeLog.cancel()
            vaiParaFormularioProduto()
        }
    }

    private fun vaiParaFormularioProduto() {
        val intent = Intent(this, FormularioProdutoActivity::class.java)
        startActivity(intent)
    }

    private fun configuraRecyclerView() {
        val recyclerView = binding.activityListaProdutosRecyclerView
        recyclerView.adapter = adapter
        adapter.quandoClicaNoItem = {
            val intent = Intent(
                this,
                DetalhesProdutoActivity::class.java
            ).apply {
                putExtra(CHAVE_PRODUTO_ID, it.id)
            }
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista_produtos, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_ordenarNomeCresc -> {
                lifecycleScope.launch {
                    val produtos = obProdutoDao.buscaTodosOrdenadoNomeCrescOuDecresc(1)
                    adapter.atualiza(produtos)
                }
            }
            R.id.menu_ordenarNomeDecresc -> {
                lifecycleScope.launch {
                    val produtos = obProdutoDao.buscaTodosOrdenadoNomeCrescOuDecresc(2)
                    adapter.atualiza(produtos)
                }
            }
            R.id.menu_ordenarValorCresc -> {
                lifecycleScope.launch {
                    val produtos = obProdutoDao.buscaTodosOrdenadoValorCrescOuDecresc(1)
                     adapter.atualiza(produtos)
                }
            }
            R.id.menu_ordenarValorDecresc -> {
                lifecycleScope.launch {
                    val produtos = obProdutoDao.buscaTodosOrdenadoValorCrescOuDecresc(2)
                    adapter.atualiza(produtos)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //override fun onDestroy() {
     //   super.onDestroy()
        //jobImprimeLog.cancel()
        //job.cancel()
   // }
}